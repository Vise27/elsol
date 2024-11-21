package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.*;
import com.example.proyecto_integrador.repository.UserRepository;
import com.example.proyecto_integrador.service.CarritoService;
import com.example.proyecto_integrador.service.ProductoService;
import com.example.proyecto_integrador.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    // Ver el carrito de compras
    @GetMapping
    public String verCarrito(@SessionAttribute("user") UserDTO user, Model model) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());

        if (carrito != null) {
            List<CarritoItem> cartItems = carritoService.obtenerItemsDelCarrito(carrito.getId());
            double totalCarrito = carritoService.calcularTotalCarrito(carrito);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalCarrito", totalCarrito);
        } else {
            model.addAttribute("mensaje", "No se encontró un carrito asociado al usuario.");
        }

        return "carrito"; // Vista carrito.html
    }

    // Eliminar un producto del carrito
    @PostMapping("/eliminar")
    public String eliminarProductoDelCarrito(@RequestParam("carritoItemId") Long carritoItemId,
                                             @SessionAttribute("user") UserDTO user) {
        CarritoItem carritoItem = carritoService.obtenerCarritoItemPorId(carritoItemId);

        if (carritoItem != null) {
            carritoService.eliminarProductoDelCarrito(carritoItem.getCarrito(), carritoItem.getProducto());
        }

        return "redirect:/carrito"; // Redirige al carrito actualizado
    }

    // Actualizar la cantidad de un producto en el carrito
    @PostMapping("/actualizarCantidad")
    public String actualizarCantidad(@RequestParam("carritoItemId") Long carritoItemId,
                                     @RequestParam("cantidad") int cantidad,
                                     @SessionAttribute("user") UserDTO user) {
        CarritoItem carritoItem = carritoService.obtenerCarritoItemPorId(carritoItemId);

        if (carritoItem != null && cantidad > 0) {
            carritoService.actualizarCantidadProducto(carritoItem, cantidad);
        }

        return "redirect:/carrito"; // Redirige al carrito actualizado
    }

    // Proceder al pago
    @PostMapping("/procederPago")
    public String procederPago(@SessionAttribute("user") UserDTO user, Model model) {
        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());

        // Verifica que el carrito no esté vacío antes de proceder
        if (carrito == null || carritoService.obtenerItemsDelCarrito(carrito.getId()).isEmpty()) {
            model.addAttribute("mensaje", "No se encontró el carrito o el carrito está vacío.");
            return "redirect:/carrito"; // Redirige al carrito si está vacío
        }

        // Calcula el total del carrito
        double totalCarrito = carritoService.calcularTotalCarrito(carrito);

        // Crear el usuario
        User usuario = userRepository.findByUsername(user.getUsername());  // Ahora podemos usar el userRepository
        if (usuario == null) {
            usuario = new User();
            usuario.setUsername(user.getUsername());
            usuario.setEmail(user.getEmail());
            // Si es necesario, guarda el usuario
            userRepository.save(usuario);
        }

        // Crea la venta y los detalles de la venta
        Venta venta = new Venta();
        venta.setCarrito(carrito);
        venta.setTotal((float) totalCarrito);
        venta.setUsuario(usuario);  // Ahora el usuario está guardado

        // Crear los detalles de la venta
        List<DetalleVenta> detalles = carritoService.obtenerItemsDelCarrito(carrito.getId()).stream()
                .map(item -> {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(item.getProducto().getPrecio());
                    detalle.setProducto(item.getProducto());
                    return detalle;
                }).collect(Collectors.toList());

        ventaService.crearVenta(venta, detalles);

        carritoService.vaciarCarrito(carrito);

        return "redirect:/pago_exito"; // Página de confirmación de pago exitosa
    }

    // Método simulado para procesar el pago
    private boolean procesarPago(double totalCarrito) {
        // Aquí puedes integrar una pasarela de pagos real
        // Por ejemplo, realizar una llamada a la API de un sistema de pagos.
        // Si el pago es exitoso, retornar true.
        return true; // Simula un pago exitoso
    }

    // Agregar un producto al carrito
    @PostMapping("/agregar")
    public String agregarProductoAlCarrito(@RequestParam("productoCodigo") Long productoCodigo,
                                           @RequestParam("cantidad") int cantidad,
                                           @SessionAttribute("user") UserDTO user) {
        Producto producto = productoService.obtenerProductoPorId(productoCodigo);

        if (producto != null && cantidad > 0 && producto.getStock() >= cantidad) {
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());

            if (carrito != null) {
                carritoService.agregarProductoAlCarrito(carrito, producto, cantidad);
            }
        }

        return "redirect:/carrito"; // Redirige al carrito actualizado
    }
}
