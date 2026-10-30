package com.falag.youssef.config;

import com.falag.youssef.client.AccountClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    ErrorDecoder accountClientErrorDecoder() {
        return new AccountClientErrorDecoder();
    }
}
