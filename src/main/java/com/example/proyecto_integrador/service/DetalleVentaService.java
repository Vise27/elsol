package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.repository.DetalleVentaRepository;
import com.example.proyecto_integrador.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Crear un detalle de venta
    public DetalleVenta crearDetalleVenta(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }
}
