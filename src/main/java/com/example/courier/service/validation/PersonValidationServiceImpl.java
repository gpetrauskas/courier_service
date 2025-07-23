package com.example.courier.service.validation;

import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.person.EmailValidator;
import com.example.courier.validation.person.NameValidator;
import com.example.courier.validation.person.PhoneValidator;
import org.springframework.stereotype.Service;

@Service
public class PersonValidationServiceImpl implements PersonValidationService {

    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;
    private final NameValidator nameValidator;
    private final PhoneValidator phoneValidator;

    public PersonValidationServiceImpl(PasswordValidator passwordValidator, EmailValidator emailValidator,
                                       NameValidator nameValidator, PhoneValidator phoneValidator) {
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
        this.nameValidator = nameValidator;
        this.phoneValidator = phoneValidator;
    }

    @Override
    public void validatePassword(String password) {
        passwordValidator.validatePassword(password);
    }

    @Override
    public boolean isEmailValid(String email) {
        return emailValidator.isValid(email);
    }

    @Override
    public boolean isNameValid(String name) {
        return nameValidator.isValid(name);
    }

    @Override
    public boolean isPhoneValid(String phone) {
        return phoneValidator.isValid(phone);
    }
}
