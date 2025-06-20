package com.bibek.bookapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class BookapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);
    }
}
