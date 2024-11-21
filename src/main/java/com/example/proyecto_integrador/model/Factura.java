package com.example.proyecto_integrador.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "elsol_factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  // Garantizar la no nulidad del usuario
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)  // Garantizar la no nulidad del carrito
    private Carrito carrito;
}
