package com.tericcabrel.osiris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OsirisApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsirisApplication.class, args);
    }
}
