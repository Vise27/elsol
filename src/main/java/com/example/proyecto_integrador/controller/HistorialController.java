package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.model.Venta;
import com.example.proyecto_integrador.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;
import java.util.List;
@Controller
public class HistorialController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/historial")
    public String mostrarHistorial(@SessionAttribute("user") UserDTO user, Model model) {
        List<Venta> historial = ventaService.obtenerHistorialPorUsuario(user.getUsername());

        // Verifica si el historial está vacío
        if (historial.isEmpty()) {
            model.addAttribute("mensaje", "Aún no tienes compras registradas.");
        } else {
            model.addAttribute("historial", historial);
        }

        return "historial"; // Ruta a la vista del historial
    }

    @GetMapping("/historial/detalles/{id}")
    public String verDetallesVenta(@PathVariable("id") Long id, Model model) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta == null) {
            model.addAttribute("mensaje", "La venta no existe.");
            return "error";
        }

        List<DetalleVenta> detalles = ventaService.obtenerDetallesPorVenta(id);
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);

        return "detalle_venta";
    }
}
