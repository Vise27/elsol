package com.example.proyecto_integrador.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lista de rutas públicas que no requieren autenticación
        List<String> rutasPublicas = Arrays.asList("/login", "/register","/carrito/**" , "/home/**","/","/recursos_plantilla/**","/styles/**","/categoria/**");

        String token = (String) request.getSession().getAttribute("token");

        // Si la ruta no es pública y el usuario no tiene token, redirigir al login ejemplo carrito
        //el carrito es publica para que pueda funcionar el modal sin errores pero si quieres acceder a la vista carrito
        //te va a pedir iniciar sesion
        if (!rutasPublicas.contains(request.getRequestURI()) && token == null) {
            response.sendRedirect("/login?message=Inicie%20sesión%20para%20acceder");
            return false;
        }

        return true;
    }
}
