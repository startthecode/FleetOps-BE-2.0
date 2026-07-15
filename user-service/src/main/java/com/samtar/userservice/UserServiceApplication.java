package com.samtar.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        // Windows JVMs report the legacy zone id "Asia/Calcutta", which the
        // Debian-13-based postgres:17 image no longer accepts at connect time.
        // Running the service in UTC avoids that and keeps timestamps uniform
        // across services.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
