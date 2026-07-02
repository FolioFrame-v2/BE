package com.folioframe.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 60_000;

    @Value("${ai-service.base-url}")
    private String aiServiceBaseUrl;

    @Bean
    public RestClient aiServiceRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MS);

        return RestClient.builder()
                .baseUrl(aiServiceBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
