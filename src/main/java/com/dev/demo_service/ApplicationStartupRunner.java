package com.dev.demo_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("hello world");
    }

}