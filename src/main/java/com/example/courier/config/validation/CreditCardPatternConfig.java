package com.example.courier.config.validation;

import com.example.courier.validation.payment.CreditCardPatterns;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class CreditCardPatternConfig implements CreditCardPatterns {
    private final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
    private final Pattern CARD_CVC_PATTERN = Pattern.compile("\\d{3}");
    private final Pattern CARD_EXPIRY_DATE = Pattern.compile("(0[1-9]|1[0-2])/\\d{2}");

    @Override
    public Pattern cardNumber() {
        return CARD_NUMBER_PATTERN;
    }

    @Override
    public Pattern cvc() {
        return CARD_CVC_PATTERN;
    }

    @Override
    public Pattern expiryDate() {
        return CARD_EXPIRY_DATE;
    }
}
