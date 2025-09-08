package com.example.courier.validation.payment;

import com.example.courier.domain.CreditCard;
import com.example.courier.dto.CreditCardDTO;
import com.example.courier.dto.OneTimeCard;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Component
public class CreditCardValidator {
    private final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
    private final Pattern CARD_CVC_PATTERN = Pattern.compile("\\d{3}");
    private final Pattern CARD_EXPIRY_DATE = Pattern.compile("(0[1-9]|1[0-2])/\\d{2}");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    public CreditCardValidator() {}

    /**
     * Full setup validation ({@link CreditCardDTO} from user input)
     *
     * @param dto the {@link CreditCardDTO} request
     * @param cvc the card security code
     * @throws IllegalArgumentException {@link IllegalArgumentException} when patterns fails
     */
    public void validateForSetup(CreditCardDTO dto, String cvc) {
        requireNonNull(dto, "CreditCardDTO cannot be null");

        validateNumber(dto.cardNumber());
        validateExpiry(dto.expiryDate());
        validateHolder(dto.cardHolderName());
        validateCvc(cvc);
    }

    /**
     * Full setup validation ({@link OneTimeCard} from user input)
     *
     * @param card the {@link OneTimeCard} request
     * @param cvc the card security code
     * @throws IllegalArgumentException {@link IllegalArgumentException} when patterns fails
     */
    public void validateForOneTime(OneTimeCard card, String cvc) {
        requireNonNull(card, "OneTimeCard cannot be null");

        validateNumber(card.getCardNumber());
        validateExpiry(card.getExpiryDate());
        validateHolder(card.getCardHolderName());
        validateCvc(cvc);
    }

    /**
     * Full setup validation ({@link CreditCard} from user input)
     *
     * @param card the {@link CreditCard} request
     * @param cvc the card security code
     * @throws IllegalArgumentException {@link IllegalArgumentException} when patterns fails
     */
    public void validateForSaved(CreditCard card, String cvc) {
        requireNonNull(card, "CreditCard cannot be null");

        validateExpiry(card.getExpiryDate());
        validateCvc(cvc);
    }


    /*
    * private reusable methods
    */

    private void validateNumber(String number) {
        if (number == null || !CARD_NUMBER_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Invalid card number");
        }
    }

    private void validateExpiry(String expiry) {
        if (expiry == null || !CARD_EXPIRY_DATE.matcher(expiry).matches()) {
            throw new IllegalArgumentException("Invalid expiry date");
        }

        YearMonth exp = YearMonth.parse(expiry, DATE_FORMATTER);
        if (exp.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card expired");
        }
    }

    private void validateCvc(String cvc) {
        if (cvc == null || !CARD_CVC_PATTERN.matcher(cvc).matches()) {
            throw new IllegalArgumentException("Invalid CVC");
        }
    }

    private void validateHolder(String holder) {
        if (holder == null || holder.isBlank()) {
            throw new IllegalArgumentException("Invalid card holder name");
        }
    }

    private void requireNonNull(Object o, String msg) {
        if (o == null) throw new IllegalArgumentException(msg);
    }
}
