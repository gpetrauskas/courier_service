package com.example.courier.service.validation;

public interface PersonValidationService {
    void validatePassword(String password);
    boolean isEmailValid(String email);
    boolean isNameValid(String name);
    boolean isPhoneValid(String phone);
}
