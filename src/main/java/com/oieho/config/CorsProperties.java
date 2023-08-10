package com.oieho.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private String allowedOrigins = "https://port-0-portfolio-v2-backend-3prof2lll3bfr1i.sel3.cloudtype.app/";
    private String allowedMethods;
    private String allowedHeaders;
    private Long maxAge;
}

