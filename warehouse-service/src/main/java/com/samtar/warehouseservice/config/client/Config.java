package com.samtar.warehouseservice.config.client;

import com.samtar.consts.ReqHeadersKeys;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class Config {

    @Bean
    public LocationClient locationClient() {


        var connectionManager =
                PoolingHttpClientConnectionManagerBuilder
                        .create()
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(20)
                        .build();

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

        CloseableHttpClient httpClient =
                HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(requestConfig)
                        .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestClient restClient =
                RestClient.builder()
                        .baseUrl("http://location-service")
                        .requestFactory(requestFactory)
                        .requestInterceptor((req, body, execution) -> {
                            // Forward headers
                            String userid =
                                    RequestContextHolder.getRequestAttributes() != null
                                            ? ((ServletRequestAttributes)
                                            RequestContextHolder.getRequestAttributes())
                                            .getRequest()
                                            .getHeader(ReqHeadersKeys.USER_ID)
                                            : null;
                            String userRole =
                                    RequestContextHolder.getRequestAttributes() != null
                                            ? ((ServletRequestAttributes)
                                            RequestContextHolder.getRequestAttributes())
                                            .getRequest()
                                            .getHeader(ReqHeadersKeys.USER_ROLE)
                                            : null;
                            if (userid != null && userRole != null) {
                                req.getHeaders().set(ReqHeadersKeys.USER_ID, userid);
                                req.getHeaders().set(ReqHeadersKeys.USER_ROLE, userRole);
                            }
                            return execution.execute(req, body);
                        })
                        .build();

        RestClientAdapter adapter =
                RestClientAdapter.create(restClient);


        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(adapter)
                        .build();


        return factory.createClient(LocationClient.class);
    }
}