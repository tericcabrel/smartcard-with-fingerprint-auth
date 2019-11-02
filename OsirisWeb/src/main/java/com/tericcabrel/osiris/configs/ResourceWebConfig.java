package com.tericcabrel.osiris.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/media/**").addResourceLocations("file:media/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:///D:/Card/OsirisWeb/src/main/uploads/");
    }
}
