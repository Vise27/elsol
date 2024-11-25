package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.model.Venta;
import com.example.proyecto_integrador.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaService detalleVentaService;

    public Venta crearVenta(Venta venta, List<DetalleVenta> detalles) {
        Venta ventaGuardada = ventaRepository.save(venta);  // Guarda la venta
        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(ventaGuardada);  // Asociamos cada detalle con la venta
            detalleVentaService.crearDetalleVenta(detalle);  // Guardamos cada detalle
        }
        return ventaGuardada;
    }

    public List<Venta> obtenerHistorialPorUsuario(String username) {
        return ventaRepository.findByUsuario_Username(username);
    }

}
