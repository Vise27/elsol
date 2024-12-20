package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.*;
import com.example.proyecto_integrador.repository.UserRepository;
import com.example.proyecto_integrador.service.CarritoService;
import com.example.proyecto_integrador.service.FacturaService;
import com.example.proyecto_integrador.service.ProductoService;
import com.example.proyecto_integrador.service.VentaService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@AllArgsConstructor
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final UserRepository userRepository;

    private final CarritoService carritoService;

    private final ProductoService productoService;

    private final VentaService ventaService;

    @GetMapping
    public String verCarrito(@SessionAttribute("user") UserDTO user, Model model) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());

        List<CarritoItem> cartItems = carritoService.obtenerItemsDelCarrito(carrito.getId());
        double totalCarrito = carritoService.calcularTotalCarrito(carrito);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalCarrito", totalCarrito);


        return "Carrito/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarProductoDelCarrito(@RequestParam("carritoItemId") Long carritoItemId,
                                             @SessionAttribute("user") UserDTO user) {
        CarritoItem carritoItem = carritoService.obtenerCarritoItemPorId(carritoItemId);

        if (carritoItem != null) {
            carritoService.eliminarProductoDelCarrito(carritoItem.getCarrito(), carritoItem.getProducto());
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizarCantidad")
    public String actualizarCantidad(@RequestParam("carritoItemId") Long carritoItemId,
                                     @RequestParam("cantidad") int cantidad,
                                     @SessionAttribute("user") UserDTO user) {
        CarritoItem carritoItem = carritoService.obtenerCarritoItemPorId(carritoItemId);

        if (carritoItem != null && cantidad > 0) {
            carritoService.actualizarCantidadProducto(carritoItem, cantidad);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/procederPago")
    public ResponseEntity<?> procederPago(@SessionAttribute("user") UserDTO user) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());

        double totalCarrito = carritoService.calcularTotalCarrito(carrito);

        User usuario = userRepository.findByUsername(user.getUsername());


        Venta venta = new Venta();
        venta.setCarrito(carrito);
        venta.setTotal((float) totalCarrito);
        venta.setUsuario(usuario);

        List<DetalleVenta> detallesVenta = carritoService.obtenerItemsDelCarrito(carrito.getId()).stream()
                .map(item -> {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(item.getProducto().getPrecio());
                    detalle.setProducto(item.getProducto());
                    return detalle;
                }).collect(Collectors.toList());

        ventaService.crearVenta(venta, detallesVenta);

        return ResponseEntity.ok().body(Collections.singletonMap("redirectUrl", "/factura/generarFactura"));
    }


    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProductoAlCarrito(
            @RequestParam("productoCodigo") Long productoCodigo,
            @RequestParam("cantidad") int cantidad,
            HttpSession session) {

        UserDTO user = (UserDTO) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para agregar productos al carrito."));
        }

        Producto producto = productoService.obtenerProductoPorId(productoCodigo);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "El producto no existe."));
        }

        if (cantidad <= 0 || producto.getStock() < cantidad) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Cantidad inválida o no hay suficiente stock."));
        }

        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());
        carritoService.agregarProductoAlCarrito(carrito, producto, cantidad);

        return ResponseEntity.ok(Map.of("success", true, "message", "Producto agregado al carrito exitosamente."));
    }


    @GetMapping("pago_exitoso")
    public String pagoExitoso() {
        return "Carrito/confirmacion";
    }
}
