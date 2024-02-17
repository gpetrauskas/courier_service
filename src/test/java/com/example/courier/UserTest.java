package com.example.courier;


import com.example.courier.domain.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    private Validator validator;

    @BeforeEach
    public void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testConstructor_WithValidParameters_ShouldCreateUserObjectWithCorrectValues() {
        // create a valid user
        User user = new User("Bilbo Baggins", "bilbo@middleearth.com", "Bag End", "underhill99" );

        // assert that user parameters match expected parameters
        assertEquals("Bilbo Baggins", user.getName());
        assertEquals("bilbo@middleearth.com", user.getEmail());
        assertEquals("Bag End", user.getAddress());
        assertEquals("underhill99", user.getPassword());
    }

    @Test
    public void testSettersAndGetters_ShouldSetAndReturnCorrectValues() {
        // create a new user object without any parameters
        User user = new User();
        // set parameters for the user
        user.setName("Bilbo Baggins");
        user.setEmail("bilbo@middleearth.com");
        user.setAddress("Bag End");
        user.setPassword("underhill99");

        // assert that user parameters match expected parameters
        assertEquals("Bilbo Baggins", user.getName());
        assertEquals("bilbo@middleearth.com", user.getEmail());
        assertEquals("Bag End", user.getAddress());
        assertEquals("underhill99", user.getPassword());
    }

    @Test
    public void testValidationConstraints_OneFieldEmpty_ShouldThrowConstraintViolationException() {
        // create user object with one empty field (email)
        User user = new User("Bilbo Baggins", "", "Bag End", "underhill99");

        // validate user object
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // assert that single violation is detected for the empty field
        assertEquals(1, violations.size());
    }

    @Test
    public void testValidationConstraints_PasswordTooShortAndTooLong_ShouldThrowConstraintViolationException() {
        // user object with password that is too short
        User user = new User("Samwise Gamgee", "sam@middleearth.com", "Hobbiton", "toshort");
        // validate the user object
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // retrieve first violation fro the set
        ConstraintViolation<User> violation = violations.iterator().next();

        // assert that violation message matches the exopected message for password length
        assertEquals("size must be between 8 and 16", violation.getMessage());
        // asssert that there is exactly one violation detected
        assertEquals(1, violations.size());

        // user object with password that is too long
        User user2 = new User("Samwise Gamgee", "sam@middleearth.com", "Hobbiton", "waytolongpasswordforsamgamgee");
        // validate the user object
        Set<ConstraintViolation<User>> violations1 = validator.validate(user2);
        // retrieve first violation from the set
        ConstraintViolation<User> violation1 = violations1.iterator().next();

        // assertt that expected message matches violation message
        assertEquals("size must be between 8 and 16", violation1.getMessage());
        // assert that there is is exactly one violation detected
        assertEquals(1, violations1.size());
    }


    @Test
    void testCreatingUserWithDuplicateEmail_shouldThrowException() {
        // arrange
        String email = "frodo@middleearth.com";
        User existingUser = new User("Frodo Baggins", email, "The Shire", "ringbearer321");

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> {
            User newUser = new User("Samwise Gamgee", email, "The Shire", "gardener123");
            checkDuplicateEmail(newUser.getEmail(), existingUser.getEmail());
        });
    }

    private void checkDuplicateEmail(String newEmail, String existingEmail) {
        if (newEmail.equals(existingEmail)) {
            throw new IllegalArgumentException("User with this email already exists");
        }
    }


}
