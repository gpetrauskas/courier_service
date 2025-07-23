package com.example.courier.service.transformation;

import com.example.courier.validation.person.PhoneValidator;
import org.springframework.stereotype.Service;

@Service
public class PersonTransformationServiceImpl implements PersonTransformationService {

    private final PhoneValidator phoneValidator;

    public PersonTransformationServiceImpl(PhoneValidator phoneValidator) {
        this.phoneValidator = phoneValidator;
    }

    @Override
    public String formatPhone(String validPhone) {
        return phoneValidator.format(validPhone);
    }

    @Override
    public String validateAndFormatPhone(String phone) {
        return phoneValidator.validateAndFormat(phone);
    }
}
