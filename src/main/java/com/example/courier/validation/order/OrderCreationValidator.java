package com.example.courier.validation.order;

import com.example.courier.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderCreationValidator {
    private final static Logger log = LoggerFactory.getLogger(OrderCreationValidator.class);

    public void validate(OrderDTO orderDTO) {
        log.debug("Validating order: {}", orderDTO);

        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }

        if (orderDTO.senderAddress() == null) {
            throw new IllegalArgumentException("Sender address cannot be null");
        }
        if (orderDTO.recipientAddress() == null) {
            throw new IllegalArgumentException("Recipient address cannot be null");
        }
        if (orderDTO.deliveryMethod() == null) {
            throw new IllegalArgumentException("Delivery method cannot be null");
        }

        if (orderDTO.parcelDetails() == null) {
            throw new IllegalArgumentException("Parcel details cannot be null");
        }

    }
}
