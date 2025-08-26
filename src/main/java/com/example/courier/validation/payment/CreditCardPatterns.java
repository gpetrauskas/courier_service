package com.example.courier.validation.payment;

import java.util.regex.Pattern;

public interface CreditCardPatterns {
    Pattern cardNumber();
    Pattern cvc();
    Pattern expiryDate();
}
