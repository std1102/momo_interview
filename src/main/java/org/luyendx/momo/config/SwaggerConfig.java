package org.luyendx.momo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger").setViewName("redirect:/swagger/swagger-ui/index.html");
    }

    @Bean
    InternalResourceViewResolver internalResourceViewResolver() {
        return new InternalResourceViewResolver();
    }
}
