package com.example.proyecto_integrador.config;

import com.example.proyecto_integrador.component.AutenticacionInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final AutenticacionInterceptor autenticacionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInterceptor)
                .addPathPatterns("/**") // Aplica el interceptor a todas las rutas
                .excludePathPatterns("/login", "/register","/carrito/**" ,"/home/**","/","/animations/**","/recursos_plantilla/**","/styles/**","/categoria/**");
    }
}

