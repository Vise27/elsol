package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;

    private boolean isActive;
    private boolean isStaff;
    private boolean isSuperuser;

    private String firstName;
    private String lastName;
    private String email;
    private String role;

    private LocalDateTime dateJoined;

    // Constructor adicional que acepta solo username y email
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
