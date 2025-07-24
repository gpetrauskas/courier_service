package com.example.courier.validation;

import java.util.regex.Pattern;

public interface PersonPatterns {
    Pattern phone();
    Pattern email();
    Pattern fullName();
}
