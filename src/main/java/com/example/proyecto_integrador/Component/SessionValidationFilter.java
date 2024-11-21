package com.example.proyecto_integrador.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SessionValidationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/",               // Página de inicio
            "/login",          // Ruta de login
            "/register",       // Ruta de registro
            "/recursos_plantilla/", // Recursos como CSS, JS, imágenes
            "/home/",          // Rutas públicas relacionadas al home
            "/styles/"         // Otros estilos o recursos
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Verifica si el usuario está en la sesión
        Object user = request.getSession().getAttribute("user");

        if (user == null && !isPublicRoute(request)) {
            response.sendRedirect("/login"); // Redirige a login si no está autenticado
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Verifica si la ruta comienza con alguna de las rutas públicas
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }
}
