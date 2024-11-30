package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.repository.DetalleVentaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;

    // Crear un detalle de venta
    public DetalleVenta crearDetalleVenta(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

}
