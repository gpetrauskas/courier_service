package com.example.courier;


import com.example.courier.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    public void test_UserConstructor() {
        User user = new User("Bilbo Baggins", "bilbo@middleearth.com", "Bag End", "underhill99" );

        assertEquals("Bilbo Baggins", user.getName());
        assertEquals("bilbo@middleearth.com", user.getEmail());
        assertEquals("Bag End", user.getAddress());
        assertEquals("underhill99", user.getPassword());
    }

    @Test
    public void test_UserSettersAndGetters() {
        User user = new User();
        user.setName("Bilbo Baggins");
        user.setEmail("bilbo@middleearth.com");
        user.setAddress("Bag End");
        user.setPassword("underhill99");

        assertEquals("Bilbo Baggins", user.getName());
        assertEquals("bilbo@middleearth.com", user.getEmail());
        assertEquals("Bag End", user.getAddress());
        assertEquals("underhill99", user.getPassword());
    }



}
