package com.boiler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.boiler.mapper")
public class BoilerManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BoilerManagementApplication.class, args);
    }
}
