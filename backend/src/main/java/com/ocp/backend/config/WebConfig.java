package com.ocp.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map any request that starts with "/uploads/**" to physical file path.
        // Use .toUri().toString() to guarantee a correctly formatted RFC 8089 URI
        // that works on both Windows (file:///C:/...) and Linux/Docker (file:///app/...).
        // Manual concatenation ("file:/" + path) produces "file://app/uploads/" on Linux,
        // where Spring Boot misinterprets "app" as a network hostname, causing a 500 error.

        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadUri = uploadDir.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri);

        // Map "/images/**" to static resources inside the jar
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
