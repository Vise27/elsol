package com.example.proyecto_integrador.Config;

import com.example.proyecto_integrador.Component.SessionValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final SessionValidationFilter sessionValidationFilter;

    public SecurityConfig(SessionValidationFilter sessionValidationFilter) {
        this.sessionValidationFilter = sessionValidationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/recursos_plantilla/**", "/home/**", "/styles/**").permitAll() // Rutas públicas
                        .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
                )
                .addFilterBefore(sessionValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> login
                        .loginPage("/login") // Página de inicio de sesión personalizada
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Redirige a la página de inicio tras cerrar sesión
                        .permitAll()
                );

        return http.build();
    }

}
