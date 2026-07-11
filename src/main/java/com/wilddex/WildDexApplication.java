package com.wilddex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WildDexApplication {

    public static void main(String[] args) {
        SpringApplication.run(WildDexApplication.class, args);
    }
}
