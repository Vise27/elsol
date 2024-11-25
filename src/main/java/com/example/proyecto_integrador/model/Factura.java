package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "elsol_factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id_factura")

    private Long idFactura;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> detalles;

    @Column(nullable = false, updatable = false)
    @JoinColumn(name = "fecha_emision")
    private LocalDateTime fechaEmision = LocalDateTime.now();


    private double total;
}
