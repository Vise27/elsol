package com.example.proyecto_integrador.Config;

import com.example.proyecto_integrador.Component.AutenticacionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AutenticacionInterceptor autenticacionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInterceptor)
                .addPathPatterns("/**") // Aplica el interceptor a todas las rutas
                .excludePathPatterns("/login", "/register","/carrito/**" ,"/home/**","/","/recursos_plantilla/**","/styles/**","/categoria/**");
    }
}

