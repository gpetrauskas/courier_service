package com.example.courier.service;

import com.example.courier.common.*;
import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.*;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.PricingOptionNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.*;
import com.example.courier.specification.OrderSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private PricingOptionRepository pricingOptionRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PersonMapper personMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public PersonDetailsDTO findPersonById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User was not found"));

        PersonDetailsDTO personDetailsDTO;
        personDetailsDTO = personMapper.toPersonDetailsDTO(person);

        return personDetailsDTO;
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

        if (orderPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> orderIds =   orderPage.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        List<Payment> allPayments = paymentRepository.findAllByOrderIdIn(orderIds);

        Map<Long, Payment> paymentMap = allPayments.stream()
                .collect(Collectors.toMap(
                        payment -> payment.getOrder().getId(),
                        payment -> payment
                ));


        return orderPage.map(order -> AdminOrderDTO.fromOrder(order, paymentMap.get(order.getId())));
    }

    public AdminOrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order was not found."));
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                new PaymentMethodNotFoundException("Payment not found"));
        return AdminOrderDTO.fromOrder(order, payment);
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
        }
        return report.toString();
    }

    public String generateOrderReport() {
        List<Order> orderList = orderRepository.findAll();

        StringBuilder report = new StringBuilder();

        report.append("Order Report\n");
        report.append("Order ID\tUser ID\tSender Address\tRecipient Address\tDeliveryPreferencies\t" +
                "Order Status\tOrder Create Date\tParcel ID\tWeight\tDimensions\tContents\tTracking Number\tParcel Status");

        for (Order order : orderList) {
            Parcel parcelDetails = order.getParcelDetails();
            Payment paymentDetails = paymentRepository.findById(order.getId()).orElse(null);
            report.append(order.getId()).append("\t");
            report.append(order.getUser().getId()).append("\t");
            report.append(order.getSenderAddress()).append("\t");
            report.append(order.getRecipientAddress()).append("\t");
            report.append(order.getDeliveryPreferences()).append("\t");
            report.append(order.getStatus()).append("\t");
            report.append(order.getCreateDate()).append("\t");
            report.append(parcelDetails.getId()).append("\t");
            report.append(parcelDetails.getWeight()).append("\t");
            report.append(parcelDetails.getDimensions()).append("\t");
            report.append(parcelDetails.getContents()).append("\t");
            report.append(parcelDetails.getTrackingNumber()).append("\t");
            report.append(parcelDetails.getStatus()).append("\t");
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

    public PricingOptionDTO getPricingOptionById(Long id) {
            return pricingOptionRepository.findById(id)
                    .map(PricingOptionDTO::fromPricingOption)
                    .orElseThrow(() -> new PricingOptionNotFoundException("Pricing option not found."));
    }

    public void createPricingOption(PricingOption pricingOption) {
        PricingOption newPricingOption = new PricingOption();
        newPricingOption.setName(pricingOption.getName());
        newPricingOption.setDescription(pricingOption.getDescription());
        newPricingOption.setPrice(pricingOption.getPrice());

        pricingOptionRepository.save(newPricingOption);
        logger.info("New pricing option was added successfully. {}", newPricingOption.getName());
    }

    public void updatePricingOption(Long id, PricingOption newValues) {
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
    }

    public String deletePricingOption(Long id) {
        if (!pricingOptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pricing option was not found.");
        }

        pricingOptionRepository.deleteById(id);
        return "Pricing option deleted successfully.";
    }

    public void updateSection(Map<String, Object> updatedData) {
        Order order = orderRepository.findById(Long.valueOf((Integer) updatedData.get("id"))).orElseThrow(() ->
                new ResourceNotFoundException("Order with usch id was not found"));

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
            case "parcelSection" -> updateParcelSection(updatedData, order);
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

    private void updateParcelSection(Map<String, Object> updatedData, Order order) {
        String parcelStatus = (String) updatedData.get("status");
        ParcelStatus parcelStatusEnum = ParcelStatus.valueOf(parcelStatus);
        String contents = (String) updatedData.get("contents");

        order.getParcelDetails().setStatus(parcelStatusEnum);
        order.getParcelDetails().setContents(contents);

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
        validateCreateTaskInput(taskDTO);

        Admin admin = getAuthenticatedAdmin();
        Courier courier = findCourierById(taskDTO.courierId());

        logger.info(taskDTO.parcelsIds().toString());

        DeliveryTask deliveryTask = createDeliveryTask(taskDTO, admin, courier);
        List<Parcel> parcelList = parcelRepository.findAllById(taskDTO.parcelsIds());
        List<DeliveryTaskItem> items = createDeliveryTaskItems(parcelList, deliveryTask, taskDTO);

        deliveryTask.setItems(items);
        courier.setHasActiveTask(true);

        deliveryTaskRepository.save(deliveryTask);
        courierRepository.save(courier);
        parcelRepository.saveAll(parcelList);
    }

    private Courier findCourierById(Long id) {
        return courierRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Courier not found with id: " + id));
    }

    private void validateCreateTaskInput(CreateTaskDTO taskDTO) {
        if (taskDTO.parcelsIds() == null || taskDTO.parcelsIds().isEmpty()) {
            throw new IllegalArgumentException("Parcel IDs cannot be null or empty");
        }
    }

    private DeliveryTask createDeliveryTask(CreateTaskDTO createTaskDTO, Admin admin, Courier courier) {
        DeliveryTask deliveryTask = new DeliveryTask();
        deliveryTask.setTaskType(createTaskDTO.taskType().equalsIgnoreCase("PICKING_UP") ?
                TaskType.PICKUP : TaskType.DELIVERY);
        deliveryTask.setCreatedBy(admin);
        deliveryTask.setCourier(courier);
        deliveryTask.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);

        return deliveryTask;
    }

    private List<DeliveryTaskItem> createDeliveryTaskItems(
            List<Parcel> parcelList, DeliveryTask deliveryTask, CreateTaskDTO taskDTO) {
        return parcelList.stream()
                .map(p -> createDeliveryTaskItem(p, deliveryTask, taskDTO))
                .toList();
    }

    private DeliveryTaskItem createDeliveryTaskItem(Parcel p, DeliveryTask deliveryTask, CreateTaskDTO taskDTO) {
        p.setStatus(taskDTO.taskType().equalsIgnoreCase("PICKING_UP") ?
                ParcelStatus.PICKING_UP : ParcelStatus.DELIVERING);
        p.setAssigned(true);

        DeliveryTaskItem item = new DeliveryTaskItem();
        item.setParcel(p);
        item.setTask(deliveryTask);
        item.setStatus(p.getStatus());

        Order order = orderRepository.findByParcelDetails(p)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for parcel: " + p.getId()));

        item.setSenderAddress(order.getSenderAddress());
        item.setRecipientAddress(order.getRecipientAddress());

        item.setDeliveryPreference(order.getDeliveryPreferences());
        return item;
    }

    public Map<String, Long> getItemsForTheListCount() {
        List<ParcelStatus> statuses = List.of(ParcelStatus.PICKING_UP, ParcelStatus.DELIVERING);
        logger.info("fetching items count");

        Map<String, Long> response = statuses.stream()
                .collect(Collectors.toMap(
                        status -> status.name().toLowerCase(),
                        status -> parcelRepository.countByStatusAndIsAssignedFalse(status)
                ));

        logger.info("Items count fetched successfully: {}", response);
        return response;
    }

    public Page<OrderDTO> getItemsByStatus(int page, int size, String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Task type (status) must be specified");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

        Specification<Order> specification = Specification.where(OrderSpecification.hasParcelIsAssignedFalse()
                .and(OrderSpecification.hasParcelStatus(status)));

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        logger.info("aa" + orderPage);

        return orderPage.map(orderMapper::toOrderDTO);
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

/*
    private Specification<Person> buildSpecification(String role, String search) {
        Specification<Person> specification = Specification.where(PersonSpecification.isNotDeleted());

        if (role != null) {
            specification = specification.and(PersonSpecification.hasRole(role));
        }
        if (search != null && !search.isEmpty()) {
            specification = specification.and(PersonSpecification.hasKeyword(search));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                root.type().in(Admin.class, Courier.class, User.class));

        return specification;
    }
*/

    public void deleteDeliveryTaskItem(Long taskId, Long itemId) {
        DeliveryTask task = deliveryTaskRepository.getReferenceById(taskId);

        isItemValidForTask(task, itemId);

        task.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .forEach(i -> {
                    i.getParcel().setAssigned(false);
                    i.setStatus(ParcelStatus.REMOVED_FROM_THE_LIST);
                });

        checkIfNotLastItemInTheTask(task, taskId);

        deliveryTaskRepository.save(task);
    }

    private void isItemValidForTask(DeliveryTask task, Long itemId) {
        boolean isValid = task.getItems().stream()
                .anyMatch(i ->
                        i.getId().equals(itemId) &&
                        !i.getStatus().equals(ParcelStatus.CANCELED) &&
                        !i.getStatus().equals(ParcelStatus.REMOVED_FROM_THE_LIST)
                );

        if (!isValid || !task.getDeliveryStatus().equals(DeliveryStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Item does not belong to the specified task or" +
                    " the task is not in a progress");
        }
    }

    private void checkIfNotLastItemInTheTask(DeliveryTask task, Long taskId) {
        System.out.println("check items in the task count" + task.getItems().size());
        boolean hasRemainingItems = task.getItems().stream()
                .anyMatch(i ->
                        !i.getStatus().equals(ParcelStatus.REMOVED_FROM_THE_LIST) &&
                        !i.getStatus().equals(ParcelStatus.CANCELED)
                );

        if (!hasRemainingItems) {
            cancelTask(taskId);
        }
    }

    @Transactional
    public void cancelTask(Long taskId) {
        Admin admin = getAuthenticatedAdmin();
        DeliveryTask task = deliveryTaskRepository.getReferenceById(taskId);



        task.getItems().forEach(i -> {
            i.getParcel().setAssigned(false);
            if (!i.getStatus().equals(ParcelStatus.REMOVED_FROM_THE_LIST)) {
                i.setStatus(ParcelStatus.CANCELED);
            }
            parcelRepository.save(i.getParcel());
        });

        task.setDeliveryStatus(DeliveryStatus.CANCELED);
        task.setCanceledByAdminId(admin.getId());

        task.getCourier().setHasActiveTask(false);

        courierRepository.save(task.getCourier());
        deliveryTaskRepository.save(task);
    }
}
