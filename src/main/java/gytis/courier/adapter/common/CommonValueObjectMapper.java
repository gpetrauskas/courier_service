package gytis.courier.adapter.common;

import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.Password;
import gytis.courier.domain.person.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommonValueObjectMapper {
    default String mapEmailToString(Email email) {
        return email == null ? null : email.toString();
    }

    default Email mapStringToEmail(String email) {
        return email == null ? null : new Email(email);
    }

    @Named("dateToString")
    default String mapDateToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    default Password mapStringToPassword(String password) {
        return password == null ? null : new Password(password);
    }

    default PhoneNumber mapStringToPhoneNumber(String phoneNumber) {
        return phoneNumber == null ? null : new PhoneNumber(phoneNumber);
    }
    default String mapPhoneNumberToString(PhoneNumber phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.number();
    }
}
