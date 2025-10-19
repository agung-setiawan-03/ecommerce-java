package com.yugungsetia.ecommerce_simple.config;

import com.xendit.Xendit;
import com.xendit.XenditClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XenditConfig {

    @Value("${xendit.api-key}")
    private String xenditApiKey;

    @Bean
    public Xendit xenditClient() {
        Xendit.apiKey = xenditApiKey;
        return new Xendit();
    }
}
