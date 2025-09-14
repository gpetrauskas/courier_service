package com.example.courier.service.address;

import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.domain.Address;
import com.example.courier.common.AddressValidationMode;
import com.example.courier.exception.UserAddressMismatchException;

import java.util.List;

/** Service interface for managing addresses.
 */
public interface AddressService {
    /** Updates an existing {@link OrderAddress} using the given {@link AddressSectionUpdateRequest}.
     * <p>
     *     For {@code ADMIN} access only.
     * </p>
     *
     * <p>
     *     {@link AddressSectionUpdateRequest} must have at least one nonnull field to apply.
     * </p>
     *
     * @param request the new data for updating the existing order address.
     * @throws AddressNotFoundException if address not found with the given ID.
     */
    void addressSectionUpdate(AddressSectionUpdateRequest request);

    /** Retrieves all saved {@link AddressDTO} entries for the currently authenticated user.
     *
     * @return a list of saved addresses
     * @throws UnauthorizedAccessException if no user is currently authenticated
     */
    List<AddressDTO> getAllMyAddresses();

    /** Updates an existing {@link Address} entity belonging to the currently authenticated user,
     * identified by the given ID, with the given {@link AddressDTO} data.
     *
     * @param addressId the ID of the address to update
     * @param dto new address data
     * @return an updated address as dto
     * @throws UnauthorizedAccessException if user is not authenticated
     * @throws AddressNotFoundException if address is not found or not belongs to current user by the given id
     */
    AddressDTO updateAddress(Long addressId, AddressDTO dto);

    /** Deletes {@link Address} belonging to the currently authorized user,
     * identified by the given address id.
     *
     * @param addressId the ID of address to delete
     * @throws AddressNotFoundException if address is not found or does not belong to current user
     */
    void deleteAddressById(Long addressId);

    /** Retrieves an existing {@link OrderAddress} for the given {@link AddressDTO} and {@link User},
     * or creates a new one if the DTO has no ID.
     *
     * @param addressDTO the address data (either the id of existing address or full details for a new one)
     * @param user the owner of the address
     * @return the persisted order address
     * @throws NullPointerException if either {@link User} or {@link AddressDTO} is {@code null}
     * @throws IllegalArgumentException if unsupported {@link AddressValidationMode} is used
     * @throws UserAddressMismatchException if address was not found or not owned by the current user
     */
    OrderAddress fetchOrCreateOrderAddress(AddressDTO addressDTO, User user);

}
