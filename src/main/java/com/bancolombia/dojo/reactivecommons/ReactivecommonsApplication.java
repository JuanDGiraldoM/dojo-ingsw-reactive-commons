package com.bancolombia.dojo.reactivecommons;

import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMessageListeners
public class ReactivecommonsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactivecommonsApplication.class, args);
    }

}
