package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.component.TokenManager;
import com.example.proyecto_integrador.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final TokenManager tokenManager;
    private final String API_URL = "https://elsol.up.railway.app";

    public UserDTO login(String username, String password) {
        String loginUrl = API_URL + "/api/token/";
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object tokenObj = response.getBody().get("access");
                if (tokenObj != null) {
                    String token = tokenObj.toString();
                    tokenManager.setToken(token);

                    UserDTO user = new UserDTO();
                    user.setUsername(username);
                    user.setToken(token);

                    return user;
                }
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        return null;
    }

    public UserDTO register(String username, String password, String email, String firstName, String lastName, String role) {
        String registerUrl = API_URL + "/api/register/";
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        request.put("email", email);
        request.put("first_name", firstName);
        request.put("last_name", lastName);
        request.put("role", role);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<UserDTO> response = restTemplate.exchange(registerUrl, HttpMethod.POST, entity, UserDTO.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error al registrar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        return null;
    }

    public UserDTO getUserProfile(String token) {
        String profileUrl = API_URL + "/api/user/profile/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/json");

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

                UserDTO user = new UserDTO();
                user.setUsername((String) body.get("username"));
                user.setEmail((String) body.get("email"));
                user.setFirstName((String) body.get("first_name"));
                user.setLastName((String) body.get("last_name"));
                user.setRole((String) body.get("role"));

                return user;
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de autenticación al obtener el perfil: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al obtener el perfil: " + e.getMessage());
        }

        return null;
    }
    public UserDTO editUserProfile(String token, String email, String firstName, String lastName) {
        String profileUrl = API_URL + "/api/user/profile/";

        // Crear un mapa con los datos que el usuario quiere actualizar
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("first_name", firstName);
        request.put("last_name", lastName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);  // Añadir el token de autenticación
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            // Realizar la solicitud PUT para actualizar los datos del perfil
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                    profileUrl, HttpMethod.PUT, entity, UserDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Retornar el UserDTO actualizado
                return response.getBody();
            } else {
                System.out.println("Error al editar el perfil: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error al editar el perfil: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error inesperado al editar el perfil: " + e.getMessage());
        }

        return null;
    }
}

