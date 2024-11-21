package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.Component.TokenManager;
import com.example.proyecto_integrador.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenManager tokenManager;


    private final String API_URL = "https://api-zsm7.onrender.com";  // URL base de la API de Django

    // Método para hacer login
    public UserDTO login(String username, String password) {
        String loginUrl = API_URL + "/api/token/";
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);

        try {
            // Realizar la solicitud POST y obtener el token en formato Map
            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Verificar si la respuesta contiene el token
                Object tokenObj = response.getBody().get("access"); // Cambiado a "access" si tu API devuelve "access"
                if (tokenObj != null) {
                    String token = tokenObj.toString();
                    tokenManager.setToken(token);

                    // Crear el UserDTO y asignar el token
                    UserDTO user = new UserDTO();
                    user.setUsername(username);
                    user.setToken(token);

                    return user;
                } else {
                    System.out.println("No se encontró el token en la respuesta");
                }
            } else {
                System.out.println("Error al obtener el token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de autenticación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        return null; // Retorna null si no se obtuvo el usuario
    }

    // Método para hacer el registro
    public UserDTO register(String username, String password, String email, String firstName, String lastName, String role) {
        String registerUrl = API_URL + "/api/register/";  // Endpoint de registro en Django

        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        request.put("email", email);
        request.put("first_name", firstName);
        request.put("last_name", lastName);
        request.put("role", role);

        try {
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request);
            ResponseEntity<UserDTO> response = restTemplate.exchange(registerUrl, HttpMethod.POST, entity, UserDTO.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return response.getBody();
            } else {
                System.out.println("Error al registrar el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error al registrar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        return null;
    }

    // Método para actualizar los datos del usuario
    public UserDTO updateUser(UserDTO user, String token) {
        String updateUrl = API_URL + "/api/user/update/";  // Endpoint de actualización en Django

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserDTO> entity = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, UserDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                System.out.println("Error al actualizar el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error al actualizar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        return null;
    }

    // Método para obtener los datos del perfil del usuario
    public UserDTO getUserProfile(String token) {
        String profileUrl = API_URL + "/api/user/profile/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    profileUrl,
                    HttpMethod.GET,
                    entity,
                    new org.springframework.core.ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Crear un UserDTO manualmente a partir de los datos recibidos
                UserDTO user = new UserDTO();
                user.setUsername((String) body.get("username"));
                user.setEmail((String) body.get("email"));
                user.setFirstName((String) body.get("first_name"));
                user.setLastName((String) body.get("last_name"));
                user.setRole((String) body.get("role"));

                return user;
            } else {
                System.out.println("Error al obtener el perfil del usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de autenticación al obtener el perfil: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al obtener el perfil: " + e.getMessage());
        }

        return null;
    }

}
