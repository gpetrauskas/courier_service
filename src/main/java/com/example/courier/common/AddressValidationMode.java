package com.example.courier.common;

/**
 * Defines how an address should be handled during validation or processing.
 *
 * <p>Lifecycle:</p>
 * <ul>
 *     <li>{@code CREATE_NEW} - the address is new and all required fields must be provided</li>
 *     <li>{@code USE_EXISTING} - the address is existing and only certain fields are validated</li>
 *     <li>{@code UPDATE} = the address is updating and all fields is optional, validated only given ones</li>
 * </ul>*/
public enum AddressValidationMode {
    CREATE_NEW,
    USE_EXISTING,
    UPDATE
}
