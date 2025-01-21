package com.example.courier.service;

import com.example.courier.common.*;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.*;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.PricingOptionNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.*;
import com.example.courier.specification.OrderSpecification;
import com.example.courier.specification.UserSpecification;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private OrderAddressRepository orderAddressRepository;
    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private PricingOptionRepository pricingOptionRepository;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PricingOptionService pricingOptionService;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public Page<PersonResponseDTO> findAllUsers(int page, int size, String role, String search) {
        System.out.println("Fetching users with page: " + page + ", size: " + size + ", role: " + role + ", search:" + search);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Specification<Person> specification = buildSpecification(role, search);

        Page<Person> personPage = personRepository.findAll(specification, pageable);
        List<Person> persons = personPage.getContent();

        if (persons.isEmpty()) {
            return Page.empty();
        }

        List<PersonResponseDTO> allPersonDTOs = persons.stream()
                .map(PersonResponseDTO::fromPerson)
                .collect(Collectors.toList());

        return new PageImpl<>(allPersonDTOs, pageable, personPage.getTotalElements());
    }

    public Optional<PersonDetailsDTO> findPersonById(Long id) {
        try {
            Person person = personRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found"));
            PersonDetailsDTO personDetailsDTO = PersonMapper.INSTANCE.toPersonDetailsDTO(person);

            return Optional.of(personDetailsDTO);
        } catch (UserNotFoundException e) {
            logger.warn("User not found", e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            logger.error("Error occurred finding user", e);
            throw e;
        }
    }

    public void updateUser(Long id, PersonDetailsDTO updatedUser) {
        try {
            User existingUser = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found."));
            logger.info("AdminService: updateUser after findById");

            updateUserFields(existingUser, updatedUser);
            userRepository.save(existingUser);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User was not found");
        }
    }

    private void updateUserFields(User existingUse, PersonDetailsDTO updatedUser) {
        if (updatedUser.name() != null) {
            existingUse.setName(updatedUser.name());
        }
        if (updatedUser.email() != null) {
            existingUse.setEmail(updatedUser.email());
        }
        if (updatedUser.role() != null) {
            existingUse.setRole(Role.valueOf(updatedUser.role()));
        }
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User was not found."));
        logger.info("User was found for deletion");
        user.setDeleted(true);

        userRepository.save(user);
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User was not found."));

        user.setBlocked(true);
        userRepository.save(user);

        logger.info("User: name {} - id {}, was banned successfully.", user.getName(), user.getId());
    }

    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User was not found"));

        user.setBlocked(false);
        userRepository.save(user);
        logger.info("User: name {} - id {}, was unbanned successfully", user.getName(), userId);
    }

    public Page<AdminOrderDTO> getAllOrders(int page, int size, Long userId, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
        Specification<Order> specification = Specification.where(null);

        if (userId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("id"), userId));
        }

        if (status != null && !status.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        List<Order> allOrders = orderPage.getContent();

        if (allOrders.isEmpty()) {
            return Page.empty();
        }

        List<Long> orderIds = allOrders.stream().map(Order::getId).collect(Collectors.toList());
        List<Payment> allPayments = paymentRepository.findAllByOrderIdIn(orderIds);

        Map<Long, Payment> paymentMap = allPayments.stream()
                .collect(Collectors.toMap(payment -> payment.getOrder().getId(), payment -> payment));

        List<AdminOrderDTO> allOrderDTOs = allOrders.stream()
                .map(order -> AdminOrderDTO.fromOrder(order, paymentMap.get(order.getId())))
                .toList();

        return new PageImpl<>(allOrderDTOs, pageable, orderPage.getTotalElements());
    }

    public AdminOrderDTO getOrderById(Long id) {
        try {
            Order order = orderRepository.findById(id).orElseThrow(() ->
                    new OrderNotFoundException("Order was not found."));
            Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                    new PaymentMethodNotFoundException("Payment not found"));
            return AdminOrderDTO.fromOrder(order, payment);
        } catch (OrderNotFoundException e) {
            logger.warn("Order was not found for id: " + id);
            return null;
        } catch (Exception e) {
            logger.warn("Error occurred while retrieving order with id: " + id);
            return null;
        }
    }

    public String generateUserReport() {
        List<User> reportList = userRepository.findAll();

        StringBuilder report = new StringBuilder();
        report.append("User Report\n");
        report.append("ID\tName\tEmail\tAddress\tRole\n");

        for (User user : reportList) {
            report.append(user.getId()).append("\t");
            report.append(user.getName()).append("\t");
            report.append(user.getEmail()).append("\t");
            report.append(user.getAddresses()).append("\t");
            report.append(user.getRole()).append("\n");
        }
        return report.toString();
    }

    public String generateOrderReport() {
        List<Order> orderList = orderRepository.findAll();

        StringBuilder report = new StringBuilder();

        report.append("Order Report\n");
        report.append("Order ID\tUser ID\tSender Address\tRecipient Address\tDeliveryPreferencies\t" +
                "Order Status\tOrder Create Date\tPackage ID\tWeight\tDimensions\tContents\tTracking Number\tPackage Status");

        for (Order order : orderList) {
            Package packageDetails = order.getPackageDetails();
            Payment paymentDetails = paymentRepository.findById(order.getId()).orElse(null);
            report.append(order.getId()).append("\t");
            report.append(order.getUser().getId()).append("\t");
            report.append(order.getSenderAddress()).append("\t");
            report.append(order.getRecipientAddress()).append("\t");
            report.append(order.getDeliveryPreferences()).append("\t");
            report.append(order.getStatus()).append("\t");
            report.append(order.getCreateDate()).append("\t");
            report.append(packageDetails.getId()).append("\t");
            report.append(packageDetails.getWeight()).append("\t");
            report.append(packageDetails.getDimensions()).append("\t");
            report.append(packageDetails.getContents()).append("\t");
            report.append(packageDetails.getTrackingNumber()).append("\t");
            report.append(packageDetails.getStatus()).append("\t");
            if (paymentDetails != null) {
                report.append(paymentDetails.getId()).append("\t");
                report.append(paymentDetails.getPaymentMethod()).append("\t");
                report.append(paymentDetails.getAmount()).append("\t");
                report.append(paymentDetails.getStatus()).append("\n");
            } else {
                report.append("N/A\tN/A\tN/A\tN/A\n");
            }
        }
        return report.toString();
    }

    public Optional<PricingOption> getPricingOptionById(Long id) {
            return Optional.ofNullable(pricingOptionRepository.findById(id).orElseThrow(() ->
                    new PricingOptionNotFoundException("Pricing option not found")));
    }

    public void createPricingOption(PricingOption pricingOption) {
        try {
            PricingOption newPricingOption = new PricingOption();
            newPricingOption.setName(pricingOption.getName());
            newPricingOption.setDescription(pricingOption.getDescription());
            newPricingOption.setPrice(pricingOption.getPrice());

            pricingOptionRepository.save(newPricingOption);
            logger.info("New pricing option was added successfully. {}", newPricingOption.getName());
            ResponseEntity.ok("New pricing option was added successfully.");
        } catch (Exception e) {
            throw e;
        }
    }

    public void updatePricingOption(Long id, PricingOption newValues) {
        try {
            PricingOption pricingOption = pricingOptionRepository.findById(id).orElseThrow(() ->
                    new PricingOptionNotFoundException("Pricing option with id: " + id + " was not found."));

            if (newValues.getName() != null) {
                pricingOption.setName(newValues.getName());
            }
            if (newValues.getDescription() != null) {
                pricingOption.setDescription(newValues.getDescription());
            }
            if (newValues.getPrice() != null) {
                pricingOption.setPrice(newValues.getPrice());
            }

            pricingOptionRepository.save(pricingOption);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> deletePricingOption(Long id) {
        if (!pricingOptionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pricing Option was not found.");
        }

        try {
            pricingOptionRepository.deleteById(id);
            return ResponseEntity.ok("Pricing Option deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e);
        }
    }

    public void updateSection(Map<String, Object> updatedData) {
        Order order = orderRepository.findById(Long.valueOf((Integer) updatedData.get("id"))).orElseThrow(() ->
                new OrderNotFoundException("Order with usch id was not found"));

        System.out.println(updatedData.entrySet());
        System.out.println(updatedData);
        System.out.println(updatedData.values());
        System.out.println(updatedData.get("status"));
        System.out.println(updatedData.get("deliveryPreferences"));

        String section = updatedData.get("sectionToEdit").toString();
        System.out.println(section);

        switch (section) {
            case "orderSection" -> updateOrderSection(updatedData, order);
            case "paymentSection" -> updatePaymentSection(updatedData, order);
            case "packageSection" -> updatePackageSection(updatedData, order);
            case "senderSection" -> updateAddressSection(updatedData, order, true);
            case "recipientSection" -> updateAddressSection(updatedData, order, false);
        }

    }

    private void updateOrderSection(Map<String, Object> updatedData, Order order) {

        String statusString = (String) updatedData.get("status");
        OrderStatus statusEnum = OrderStatus.valueOf(statusString.toUpperCase());

        order.setStatus(statusEnum);
        order.setDeliveryPreferences(updatedData.get("deliveryPreferences").toString());
        logger.info("order successfully updated");
        orderRepository.save(order);
    }

    private void updatePaymentSection(Map<String, Object> updatedData, Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                new PaymentMethodNotFoundException("Payment not found"));

        String paymentStatus = (String) updatedData.get("status");
        PaymentStatus statusEnum = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        payment.setStatus(statusEnum);
        logger.info("payment successfully updated");

        paymentRepository.save(payment);
    }

    private void updatePackageSection(Map<String, Object> updatedData, Order order) {
        String packageStatus = (String) updatedData.get("status");
        PackageStatus packageStatusEnum = PackageStatus.valueOf(packageStatus);
        String contents = (String) updatedData.get("contents");

        order.getPackageDetails().setStatus(packageStatusEnum);
        order.getPackageDetails().setContents(contents);

        orderRepository.save(order);
    }

    private void updateAddressSection(Map<String, Object> updatedData, Order order, boolean isSender) {
        OrderAddress orderAddress = isSender ? order.getSenderAddress() : order.getRecipientAddress();

        @SuppressWarnings("unchecked")
        Map<String, Object> addressData = (Map<String, Object>) updatedData.get("address");
        logger.info("Address Data: " + addressData);

        orderAddress.setName((String) addressData.get("name"));
        orderAddress.setStreet((String) addressData.get("street"));
        orderAddress.setHouseNumber((String) addressData.get("houseNumber"));
        orderAddress.setCity((String) addressData.get("city"));
        orderAddress.setPostCode((String) addressData.get("postCode"));
        orderAddress.setPhoneNumber((String) addressData.get("phoneNumber"));
        orderAddress.setFlatNumber((String) addressData.get("flatNumber"));

        if (isSender) order.setSenderAddress(orderAddress);
        else order.setRecipientAddress(orderAddress);

        logger.info((isSender ? "Sender" : "Recipient") + " address successfully updated");
        orderRepository.save(order);
    }

    @Transactional
    public void createNewCourierTask(CreateTaskDTO taskDTO) {
        try {
            Admin admin = getAuthenticatedAdmin();
            Courier courier = courierRepository.findById(taskDTO.courierId()).orElseThrow(() ->
                    new RuntimeException("Courier not found with given id"));

            logger.info(taskDTO.parcelsIds().toString());


            DeliveryTask deliveryTask = new DeliveryTask();
            deliveryTask.setTaskType(taskDTO.taskType().equalsIgnoreCase("PICKING_UP") ?
                    TaskType.PICKUP : TaskType.DELIVERY);
            deliveryTask.setCreatedBy(admin);
            deliveryTask.setCourier(courier);
            deliveryTask.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);

            System.out.println(taskDTO.parcelsIds());

            List<Package> packageList = packageRepository.findAllById(taskDTO.parcelsIds());

            List<DeliveryTaskItem> items = packageList.stream()
                    .map(p -> {
                        p.setStatus(taskDTO.taskType().equalsIgnoreCase("PICKING_UP") ?
                                PackageStatus.PICKING_UP : PackageStatus.DELIVERING);
                        p.setAssigned(true);
                        DeliveryTaskItem item = new DeliveryTaskItem();
                        item.setParcel(p);
                        item.setTask(deliveryTask);
                        item.setStatus(p.getStatus());

                        Order order = orderRepository.findByPackageDetails(p).orElseThrow(() ->
                                new RuntimeException("Order not found"));

                        item.setSenderAddress(order.getSenderAddress());
                        item.setRecipientAddress(order.getRecipientAddress());
                        return item;
                    }).toList();

            deliveryTask.setItems(items);
            courier.setHasActiveTask(true);

            deliveryTaskRepository.save(deliveryTask);
            courierRepository.save(courier);
            packageRepository.saveAll(packageList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public Map<String, Number> getItemsForTheListCount() {
        List<Package> packagesToPickup = packageRepository.findByStatus(PackageStatus.PICKING_UP);
        List<Package> packagesDelivering = packageRepository.findByStatus(PackageStatus.DELIVERING);

        Map<String, Number> response = new HashMap<>();
        response.put("toPickup", packagesToPickup.size());
        response.put("delivering", packagesDelivering.size());

        return response;
    }

    public Map<String, Object> getItemsByStatus(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

        Specification<Order> specification = Specification.where(OrderSpecification.hasPackageIsAssignedFalse());
        if (status != null && !status.isEmpty()) {
            specification = specification.and(OrderSpecification.hasPackageStatus(status));
        }

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);

        Map<String, Object> mappedOrdersPage = new HashMap<>();
        mappedOrdersPage.put("packages", orderPage.stream().map(orderMapper::toOrderDTO).toList());
        mappedOrdersPage.put("currentPage", orderPage.getNumber());
        mappedOrdersPage.put("totalPages", orderPage.getTotalPages());

        return mappedOrdersPage;
    }

    public List<CourierDTO> getAvailableCouriers() {
        List<Courier> allCouriers = courierRepository.findByHasActiveTaskFalse();

        return allCouriers.stream()
                .map(c -> new CourierDTO(c.getId(), c.getName(), c.getEmail()))
                .toList();
    }

    public Admin getAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("Authenticated email: " + authentication.getName());
            return adminRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Admin was not found"));
        }
        throw new RuntimeException("No authenticated admin found");
    }
    public List<DeliveryTaskDTO> getAllDeliveryLists() {
        List<DeliveryTask> tasksList = deliveryTaskRepository.findAll();
        System.out.println("Tasks list size: " + tasksList.size());

        return tasksList.stream()
                .map(t -> {
                    System.out.println("Processing task ID: " + t.getId());
                    List<DeliveryTaskItemDTO> items = t.getItems().stream()
                            .map(item -> {
                                System.out.println("Processing item ID: " + item.getId());
                                OrderAddressDTO senderAddressDTO = OrderAddressDTO.fromOrderAddress(item.getSenderAddress());
                                OrderAddressDTO recipientAddressDTO = OrderAddressDTO.fromOrderAddress(item.getRecipientAddress());
                                DeliveryTaskItemDTO dto = DeliveryTaskItemDTO.fromDeliveryTaskItem(item, senderAddressDTO, recipientAddressDTO, item.getStatus());
                                System.out.println("Created DeliveryTaskItemDTO for item ID: " + item.getId());
                                return dto;
                            })
                            .toList();

                    return new DeliveryTaskDTO(
                            t.getId(),
                            new CourierDTO(t.getCourier().getId(), t.getCourier().getName(), t.getCourier().getEmail()),
                            t.getCreatedBy().getId(),
                            items,
                            t.getTaskType(),
                            t.getDeliveryStatus(),
                            t.getCreatedAt(),
                            t.getCompletedAt()
                    );
                })
                .toList();
    }

    private Specification<Person> buildSpecification(String role, String search) {
        Specification<Person> specification = Specification.where(UserSpecification.isNotDeleted());

        if (role != null) {
            specification = specification.and(UserSpecification.hasRole(role));
        }
        if (search != null && !search.isEmpty()) {
            specification = specification.and(UserSpecification.hasKeyword(search));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                root.type().in(Admin.class, Courier.class, User.class));

        return specification;
    }

    public void deleteDeliveryTaskItem(Long taskId, Long itemId) {
        DeliveryTask task = deliveryTaskRepository.getReferenceById(taskId);

        task.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .forEach(i -> i.setStatus(PackageStatus.REMOVED_FROM_THE_LIST));

        deliveryTaskRepository.save(task);
    }

    @Transactional
    public void cancelTask(Long taskId) {
        Admin admin = getAuthenticatedAdmin();
        DeliveryTask task = deliveryTaskRepository.getReferenceById(taskId);

        task.getItems().forEach(i -> {
            i.getParcel().setAssigned(false);
            i.setStatus(PackageStatus.CANCELED);
            packageRepository.save(i.getParcel());
        });

        task.setDeliveryStatus(DeliveryStatus.CANCELED);
        task.setCanceledByAdminId(admin.getId());

        task.getCourier().setHasActiveTask(false);

        courierRepository.save(task.getCourier());
        deliveryTaskRepository.save(task);
    }
}
