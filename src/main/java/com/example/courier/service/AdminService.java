package com.example.courier.service;

import com.example.courier.common.Role;
import com.example.courier.domain.*;
import com.example.courier.domain.Package;
import com.example.courier.dto.AdminOrderDTO;
import com.example.courier.dto.UserDetailsDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.dto.mapper.UserMapper;
import com.example.courier.exception.OrderNotFoundException;
import com.example.courier.exception.PaymentMethodNotFoundException;
import com.example.courier.exception.PricingOptionNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.PaymentRepository;
import com.example.courier.repository.PricingOptionRepository;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PricingOptionRepository pricingOptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public List<UserResponseDTO> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserResponseDTO> allUserResponseDTOs = allUsers.stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
        return allUserResponseDTOs;
    }

    public Optional<UserDetailsDTO> findUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found"));
            UserDetailsDTO userDetailsDTO = UserMapper.INSTANCE.toUserDetailsDTO(user);

            return Optional.of(userDetailsDTO);
        } catch (UserNotFoundException e) {
            logger.warn("User not found", e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            logger.error("Error occurred finding user", e);
            throw e;
        }
    }

    public void updateUser(Long id, UserDetailsDTO updatedUser) {
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

    private void updateUserFields(User existingUse, UserDetailsDTO updatedUser) {
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
            userRepository.delete(user);
    }

    public Page<AdminOrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

        Page<Order> orderPage = orderRepository.findAll(pageable);
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
}
