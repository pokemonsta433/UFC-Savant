package com.example.ufcsavant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UfcSavantApplication {

    public static void main(String[] args) {
        SpringApplication.run(UfcSavantApplication.class, args);
    }

}
