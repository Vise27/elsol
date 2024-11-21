package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "elsol_favorito")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)  // Asegurar que el producto no sea nulo
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  // Asegurar que el usuario no sea nulo
    private User usuario;
}
