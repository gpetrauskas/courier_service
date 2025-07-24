package com.example.courier.validation;

import java.util.regex.Pattern;

public interface AddressPatterns {
    Pattern postCode();
    Pattern city();
    Pattern street();
    Pattern houseNumber();
    Pattern flatNumber();
}
