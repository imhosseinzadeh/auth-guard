package com.imho.authguard;

import org.springframework.boot.SpringApplication;

public class TestAuthGuardApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuthGuardApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
