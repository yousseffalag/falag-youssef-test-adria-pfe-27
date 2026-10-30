package com.falag.youssef.transactionservice.config;

import com.falag.youssef.transactionservice.client.AccountClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    ErrorDecoder accountClientErrorDecoder() {
        return new AccountClientErrorDecoder();
    }
}
