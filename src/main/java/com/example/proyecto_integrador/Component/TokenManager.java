package com.example.proyecto_integrador.Component;


import org.springframework.stereotype.Component;

@Component
public class TokenManager {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }
}
