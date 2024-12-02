package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.DetalleVenta;
import com.example.proyecto_integrador.model.Venta;
import com.example.proyecto_integrador.service.VentaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
@AllArgsConstructor
@Controller
public class HistorialController {

    private final VentaService ventaService;

    @GetMapping("/historial")
    public String mostrarHistorial(@SessionAttribute("user") UserDTO user,
                                   @RequestParam(defaultValue = "0") int page, Model model) {
        List<Venta> historial = ventaService.obtenerHistorialPorUsuario(user.getUsername());

        // Ordenar el historial de más reciente a más antiguo
        historial.sort((venta1, venta2) -> venta2.getFecha().compareTo(venta1.getFecha()));

        // Verifica si el historial está vacío
        if (historial.isEmpty()) {
            model.addAttribute("mensaje", "Aún no tienes compras registradas.");
        } else {
            // Tamaño de la página (10 compras por página)
            int pageSize = 10;
            int totalPages = (int) Math.ceil((double) historial.size() / pageSize);

            // Validar que el número de página esté dentro del rango
            if (page < 0) page = 0;
            if (page >= totalPages) page = totalPages - 1;

            // Obtener la sublista correspondiente a la página actual
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, historial.size());
            List<Venta> historialPaginado = historial.subList(fromIndex, toIndex);

            // Agregar datos al modelo
            model.addAttribute("historial", historialPaginado);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
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
