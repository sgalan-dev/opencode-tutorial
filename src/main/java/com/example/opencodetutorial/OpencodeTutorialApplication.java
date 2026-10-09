package com.example.opencodetutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the opencode-tutorial Spring Boot service.
 * Component scanning is anchored here so all layers (domain, application, infrastructure)
 * under {@code com.example.opencodetutorial} are picked up automatically.
 */
@SpringBootApplication
public class OpencodeTutorialApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args command-line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(OpencodeTutorialApplication.class, args);
    }
}