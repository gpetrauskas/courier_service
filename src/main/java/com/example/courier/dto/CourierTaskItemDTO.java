package com.example.courier.dto;

import java.util.List;

public record CourierTaskItemDTO(Long id, String weight, String dimensions, String contents,
                                 String deliveryPreference, String status, List<String> notes, OrderAddressDTO customerAddress) {
}
