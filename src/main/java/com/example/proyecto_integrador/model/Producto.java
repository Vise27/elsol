package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "elsol_producto")
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String codigo;

    @Column(nullable = false, length = 255)  // Asegurar que el nombre no sea nulo
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)  // Asegurar que el precio no sea nulo
    private Double precio;

    @Column(nullable = false)  // Asegurar que el stock no sea nulo
    private int stock;

    private String imagen;
}
