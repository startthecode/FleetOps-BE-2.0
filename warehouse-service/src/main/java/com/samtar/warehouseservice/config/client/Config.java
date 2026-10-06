package com.samtar.warehouseservice.config.client;

import com.samtar.warehouseservice.client.LocationClient;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class Config {

    @Bean
    public LocationClient locationClient() {

        /*
         * 1. Connection pool
         */
        var connectionManager =
                PoolingHttpClientConnectionManagerBuilder
                        .create()
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(20)
                        .build();


        /*
         * 2. HTTP timeout configuration
         */
        RequestConfig requestConfig =
                RequestConfig.custom()

                        // Maximum time to establish connection
                        .setConnectTimeout(
                                Timeout.ofSeconds(1)
                        )

                        // Maximum time waiting for response/data
                        .setResponseTimeout(
                                Timeout.ofSeconds(3)
                        )

                        // Maximum time waiting for a connection
                        // from the connection pool
                        .setConnectionRequestTimeout(
                                Timeout.ofSeconds(1)
                        )

                        .build();


        /*
         * 3. Apache HttpClient
         */
        CloseableHttpClient httpClient =
                HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(requestConfig)
                        .build();


        /*
         * 4. Give Apache HttpClient to Spring
         */
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);


        /*
         * 5. Spring RestClient
         */
        RestClient restClient =
                RestClient.builder()
                        .baseUrl("http://location-service")
                        .requestFactory(requestFactory)
                        .build();


        /*
         * 6. Adapt RestClient
         *    for Spring HTTP Service Client
         */
        RestClientAdapter adapter =
                RestClientAdapter.create(restClient);


        /*
         * 7. Create HTTP interface proxy
         */
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(adapter)
                        .build();


        /*
         * 8. Generate LocationClient implementation
         */
        return factory.createClient(LocationClient.class);
    }
}