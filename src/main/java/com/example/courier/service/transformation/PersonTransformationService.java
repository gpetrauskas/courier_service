package com.example.courier.service.transformation;

public interface PersonTransformationService {
    String formatPhone(String validPhone);
    String validateAndFormatPhone(String phone);
}
