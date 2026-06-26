package gytis.courier.domain.payment;



/**
 * Represents type of payment method.
 *
 * <p>Each attempt also records the {@link ProviderType} user for that attempt</p>
 * <ul>
 *     <li>{@code CREDIT_CARD} - user pays via credit card</li>
 *     <li>{@code PAYPAL} - //user pays via paypal (not implemented yet)</li>
 *     <li>{@code UNKNOWN} - used for payment attempt if it fails before knowing payment type</li>
 * </ul>*/
public enum ProviderType {
    CREDIT_CARD,
    PAYPAL,
    UNKNOWN;
}
