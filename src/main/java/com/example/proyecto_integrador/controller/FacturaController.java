package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.Factura;
import com.example.proyecto_integrador.model.User;
import com.example.proyecto_integrador.repository.UserRepository;
import com.example.proyecto_integrador.service.CarritoService;
import com.example.proyecto_integrador.service.FacturaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller

public class FacturaController {

    private  final CarritoService carritoService;
    private final UserRepository userRepository;
    private final FacturaService facturaService;

    public FacturaController(CarritoService carritoService, UserRepository userRepository, FacturaService facturaService) {
        this.carritoService = carritoService;
        this.userRepository = userRepository;
        this.facturaService = facturaService;
    }

    @GetMapping("/factura/generarFactura")
    public String generarFactura(@SessionAttribute("user") UserDTO user, Model model) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());
        if (carrito == null || carritoService.obtenerItemsDelCarrito(carrito.getId()).isEmpty()) {
            model.addAttribute("mensaje", "No se encontró el carrito o el carrito está vacío.");
            return "redirect:/carrito"; // Redirige al carrito si está vacío
        }

        User usuario = userRepository.findByUsername(user.getUsername());
        if (usuario == null) {
            model.addAttribute("mensaje", "Usuario no encontrado.");
            return "redirect:/login"; // Redirige al login si no se encuentra el usuario
        }

        Factura factura = facturaService.crearFactura(carrito, usuario);

        model.addAttribute("mensaje", "Pago realizado con éxito. Número de factura: " + factura.getIdFactura());
        carritoService.vaciarCarrito(carrito);

        return "redirect:/";
    }
}
