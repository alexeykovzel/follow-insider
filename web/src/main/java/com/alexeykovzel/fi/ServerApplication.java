package com.alexeykovzel.fi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        new SpringApplication(ServerApplication.class).run(args);
    }
}
