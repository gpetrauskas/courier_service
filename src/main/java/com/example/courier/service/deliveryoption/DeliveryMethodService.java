package com.example.courier.service.deliveryoption;

import com.example.courier.common.DeliveryGroup;
import com.example.courier.domain.DeliveryMethod;
import com.example.courier.domain.Order;
import com.example.courier.dto.OrderDTO;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeliveryMethodService {
    Map<DeliveryGroup, List<DeliveryMethodDTO>> getAllDeliveryOptions();
    List<DeliveryMethodAdminResponseDTO> getDeliveryOptionsNotCategorized();
    void updateDeliveryOption(Long id, UpdateDeliveryMethodDTO dto);
    void addNewDeliveryOption(CreateDeliveryMethodDTO dto);
    void deleteDeliveryOption(Long id);
    DeliveryMethodDTO getById(Long id);
    BigDecimal calculateShippingCost(Order order);
    String getDescriptionById(Long id);
    DeliveryMethod getDeliveryOptionById(Long id);
    Set<String> getDeliveryPreferences();
}
