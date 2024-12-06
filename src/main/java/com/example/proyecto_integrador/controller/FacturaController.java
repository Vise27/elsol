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


        User usuario = userRepository.findByUsername(user.getUsername());
        Factura factura = facturaService.crearFactura(carrito, usuario);
        carritoService.vaciarCarrito(carrito.getId());
        model.addAttribute("mensaje", "Pago realizado con éxito. Número de factura: " + factura.getIdFactura());


            return "redirect:/carrito/pago_exitoso"; // Redirige a la comfirmacio xdd
    }

}
