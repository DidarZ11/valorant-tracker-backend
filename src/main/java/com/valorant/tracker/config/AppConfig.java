package com.valorant.tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper; // <-- Можно тоже удалить, если больше не используется
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${valorant.api.key}") String apiKey) {

        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("TRN-Api-Key", apiKey);
            return execution.execute(request, body);
        });
        return rt;
    }
}
