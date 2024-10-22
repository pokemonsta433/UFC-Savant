package com.example.ufcsavant.config;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfiguration {
        public ThymeleafConfiguration(){

        }
        @Autowired
        private ServletContext context;
}
