package com.pex.colorindicator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for SpringBoot
 */
@SpringBootApplication
public class Starter {
    /**
     * Run Spring boot and pass command line arguments
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }
}
