package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.*;
import com.example.proyecto_integrador.repository.UserRepository;
import com.example.proyecto_integrador.service.CarritoService;
import com.example.proyecto_integrador.service.FacturaService;
import com.example.proyecto_integrador.service.ProductoService;
import com.example.proyecto_integrador.service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    @Autowired
    private FacturaService facturaService;

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

        return "Carrito/carrito";
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

        // Obtener o crear el usuario
        User usuario = userRepository.findByUsername(user.getUsername());
        if (usuario == null) {
            usuario = new User();
            usuario.setUsername(user.getUsername());
            usuario.setEmail(user.getEmail());
            userRepository.save(usuario); // Guarda el usuario si no existe
        }

        // Crear la venta
        Venta venta = new Venta();
        venta.setCarrito(carrito);
        venta.setTotal((float) totalCarrito);
        venta.setUsuario(usuario);

        // Crear los detalles de la venta
        List<DetalleVenta> detallesVenta = carritoService.obtenerItemsDelCarrito(carrito.getId()).stream()
                .map(item -> {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(item.getProducto().getPrecio());
                    detalle.setProducto(item.getProducto());
                    return detalle;
                }).collect(Collectors.toList());

        // Guardar la venta y sus detalles
        ventaService.crearVenta(venta, detallesVenta);

        // Redirige a la página para generar la factura
        return "redirect:/factura/generarFactura";
    }


    private boolean xd(double totalCarrito) {

        return true;
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProductoAlCarrito(
            @RequestParam("productoCodigo") Long productoCodigo,
            @RequestParam("cantidad") int cantidad,
            HttpSession session) {

        // Obtén el usuario desde la sesión
        UserDTO user = (UserDTO) session.getAttribute("user");

        if (user == null) {
            // Si el usuario no está autenticado, devuelve un error 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Debes iniciar sesión para agregar productos al carrito."));
        }

        // Lógica para obtener el producto
        Producto producto = productoService.obtenerProductoPorId(productoCodigo);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "El producto no existe."));
        }

        // Verifica el stock y la cantidad
        if (cantidad <= 0 || producto.getStock() < cantidad) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Cantidad inválida o no hay suficiente stock."));
        }

        // Obtén el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());
        if (carrito == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "No se pudo obtener el carrito del usuario."));
        }

        // Agrega el producto al carrito
        carritoService.agregarProductoAlCarrito(carrito, producto, cantidad);

        return ResponseEntity.ok(Map.of("success", true, "message", "Producto agregado al carrito exitosamente."));
    }

}
