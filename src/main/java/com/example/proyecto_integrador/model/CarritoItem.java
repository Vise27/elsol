package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "elsol_carritoitem")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Transient
    public double getTotalPrecio() {
        return cantidad * producto.getPrecio();
    }
}
