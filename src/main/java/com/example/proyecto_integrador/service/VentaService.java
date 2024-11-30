package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.model.Venta;
import com.example.proyecto_integrador.repository.DetalleVentaRepository;
import com.example.proyecto_integrador.repository.VentaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class VentaService {


    private final VentaRepository ventaRepository;

    private final DetalleVentaRepository detalleVentaRepository;


    private final DetalleVentaService detalleVentaService;

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
    public List<DetalleVenta> obtenerDetallesPorVenta(Long ventaId) {
        return detalleVentaRepository.findByVenta_Codigo(ventaId);
    }
    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

}
