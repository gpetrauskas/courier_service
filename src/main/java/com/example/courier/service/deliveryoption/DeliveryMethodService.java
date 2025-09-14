package com.example.courier.service.deliveryoption;

import com.example.courier.common.DeliveryGroup;
import com.example.courier.domain.DeliveryMethod;
import com.example.courier.domain.Order;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Service for mapping and retrieving delivery methods and related information.
 *
 */
public interface DeliveryMethodService {
    /** Retrieves all available delivery options grouped by their {@link DeliveryGroup}
     *
     * @return a map where the key is the {@link DeliveryGroup} and the value is the list
     * of {@link DeliveryMethodDTO} in that group
     */
    Map<DeliveryGroup, List<DeliveryMethodDTO>> getAllDeliveryOptions();

    /** Retrieves delivery options that are not assigned to any {@link DeliveryGroup}.
     *
     * @return a list of uncategorized delivery methods
     */
    List<DeliveryMethodAdminResponseDTO> getDeliveryOptionsNotCategorized();

    /** Update an existing delivery option.
     *
     * @param id the ID of delivery option to update
     * @param dto the updated delivery method details
     */
    void updateDeliveryOption(Long id, UpdateDeliveryMethodDTO dto);

    /** Create new delivery option.
     *
     * @param dto the details of the new delivery method
     */
    void addNewDeliveryOption(CreateDeliveryMethodDTO dto);

    /** Delete an existing delivery method.
     *
     * @param id the ID of delivery option to be deleted
     */
    void deleteDeliveryOption(Long id);

    /** Retrieves delivery option for admin by its id.
     *
     * @param id the ID of delivery option
     * @return a delivery method details
     */
    DeliveryMethodDTO getAdminDeliveryOptionById(Long id);

    /** Calculates shipping cost for the given order.
     *
     * @param order the order for which to calculate shipping
     * @return the calculated shipping cost
     */
    BigDecimal calculateShippingCost(Order order);

    /** Retrieves delivery option description by its ID.
     *
     * @param id the ID of a delivery option.
     * @return the description as a string
     */
    String getDescriptionById(Long id);

    /** Retrieves the {@link DeliveryMethod} entity byt its id.
     *
     * @param id the id of the delivery option
     * @return the delivery method entity
     */
    DeliveryMethod getDeliveryOptionById(Long id);

    /** Retrieves a set of available delivery preferences.
     *
     * @return a set of delivery preferences
     */
    Set<String> getDeliveryPreferences();
}
