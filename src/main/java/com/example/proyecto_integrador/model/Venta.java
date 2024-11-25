package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "elsol_venta")
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)  // Asegurar que carrito no sea nulo
    private Carrito carrito;

    private LocalDateTime fecha = LocalDateTime.now();

    @Column(nullable = false)  // Asegurar que el total no sea nulo
    private double total;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  // Asegurar que el usuario no sea nulo
    private User usuario;
    @JoinColumn(name = "estado")
    @Column(nullable = false, length = 20)
    private String estado = "pendiente";
}
