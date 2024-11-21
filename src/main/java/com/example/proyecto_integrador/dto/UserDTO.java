package com.example.proyecto_integrador.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String role;
    private String token;

    private String firstName;

    private String lastName;


}
