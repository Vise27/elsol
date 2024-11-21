package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "elsol_detalleventa")
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(nullable = false)  // Añadido para garantizar que cantidad no sea nula
    private Integer cantidad;

    @Column(nullable = false)  // Añadido para garantizar que precioUnitario no sea nulo
    private double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "codigo")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "venta_id", referencedColumnName = "codigo")
    private Venta venta;
}
