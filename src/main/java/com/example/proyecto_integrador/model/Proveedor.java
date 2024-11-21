package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "elsol_proveedor")
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  // Asegurar que el nombre no sea nulo
    private String nombre;

    private Integer telefono;

    @Column(nullable = false)  // Asegurar que el correo no sea nulo
    private String correo;

    private String direccion;
}
