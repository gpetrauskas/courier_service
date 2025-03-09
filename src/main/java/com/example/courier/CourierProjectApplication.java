package com.example.courier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CourierProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourierProjectApplication.class, args);
	}

}
