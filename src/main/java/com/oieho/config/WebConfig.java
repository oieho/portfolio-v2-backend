package com.oieho.config;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // 기본 resourceHandler 유지하면서 추가
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/") // '/'으로 끝나도록
                .setCachePeriod(20);

    }
}