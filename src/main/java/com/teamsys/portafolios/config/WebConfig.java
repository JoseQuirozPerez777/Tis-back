package com.teamsys.portafolios.config;

import com.teamsys.portafolios.interceptors.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Aplicamos la protección solo a rutas sensibles
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/usuarios/registro", "/api/usuarios/login");
    }
}