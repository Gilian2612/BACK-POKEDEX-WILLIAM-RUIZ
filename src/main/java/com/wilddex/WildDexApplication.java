package com.wilddex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WilddexApplication {

    public static void main(String[] args) {
        SpringApplication.run(WilddexApplication.class, args);
    }
}
