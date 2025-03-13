package com.example.courier.service;

import com.example.courier.common.*;
import com.example.courier.domain.*;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.*;
import com.example.courier.dto.mapper.OrderMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.DeliveryOptionNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.*;
import com.example.courier.specification.order.OrderSpecification;
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
    private DeliveryOptionRepository deliveryOptionRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private CourierRepository courierRepository;
    private final OrderMapper orderMapper;
    @Autowired
    private PersonMapper personMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public PersonDetailsDTO findPersonById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User was not found"));

        PersonDetailsDTO personDetailsDTO;
        personDetailsDTO = personMapper.toPersonDetailsDTO(person);

        return personDetailsDTO;
    }

    public AdminOrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order was not found."));
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseThrow(() ->
                new PaymentMethodNotFoundException("Payment not found"));
        return orderMapper.toAdminOrderDTO(order, payment);
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
            report.append(order.getDeliveryMethod()).append("\t");
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

    public DeliveryOptionBaseDTO getDeliveryOptionById(Long id) {
            return deliveryOptionRepository.findById(id)
                    .map(DeliveryOptionBaseDTO::fromDeliveryOption)
                    .orElseThrow(() -> new DeliveryOptionNotFoundException("Delivery option not found."));
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
                .map(c -> new CourierDTO(c.getId(), c.getName(), c.getEmail(), c.hasActiveTask()))
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
