package com.example.courier.validation;

import java.util.regex.Pattern;

public interface ValidationPatterns {
    Pattern phone();
    Pattern email();
    Pattern fullName();
}
