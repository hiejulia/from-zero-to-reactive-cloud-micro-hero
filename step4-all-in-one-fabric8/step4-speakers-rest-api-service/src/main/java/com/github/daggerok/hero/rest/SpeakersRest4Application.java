package com.github.daggerok.hero.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PostgresProps.class)
public class SpeakersRest4Application {
    public static void main(String[] args) {
        SpringApplication.run(SpeakersRest4Application.class, args);
    }
}
