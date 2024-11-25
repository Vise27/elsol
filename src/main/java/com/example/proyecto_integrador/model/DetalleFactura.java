package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="elsol_detallefactura")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @JoinColumn(name = "cantidad")
    private int cantidad;
    @JoinColumn(name = "precio_unitario")
    private double precioUnitario;

    @JoinColumn(name = "precio_total")
    private double precioTotal;

    @PrePersist
    @PreUpdate
    public void calcularPrecioTotal() {
        this.precioTotal = this.cantidad * this.precioUnitario;
    }



}
