package com.tw.heima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HeimaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeimaApplication.class, args);
    }

}
