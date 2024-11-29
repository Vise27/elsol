    package com.example.proyecto_integrador.service;

    import com.example.proyecto_integrador.model.*;
    import com.example.proyecto_integrador.repository.DetalleFacturaRepository;
    import com.example.proyecto_integrador.repository.FacturaRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import java.util.List;

    @Service
    public class FacturaService {

        @Autowired
        private FacturaRepository facturaRepository;

        @Autowired
        private DetalleFacturaRepository detalleFacturaRepository;

        @Autowired
        private CarritoService carritoService;  // Inyectar el CarritoService

        public Factura crearFactura(Carrito carrito, User usuario) {
            Factura factura = null;

            try {
                double totalCarrito = carritoService.calcularTotalCarrito(carrito);

                // Crear y guardar la factura
                factura = new Factura();
                factura.setUsuario(usuario);
                factura.setCarrito(carrito);
                factura.setTotal(totalCarrito);
                factura = facturaRepository.save(factura);

                // Crear los detalles de la factura
                List<CarritoItem> carritoItems = carrito.getItems();
                for (CarritoItem item : carritoItems) {
                    DetalleFactura detalle = new DetalleFactura();
                    detalle.setFactura(factura);
                    detalle.setProducto(item.getProducto());
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(item.getProducto().getPrecio());
                    detalleFacturaRepository.save(detalle);
                }

                // Vaciar el carrito después de guardar la factura y los detalles
                carritoService.vaciarCarrito(carrito.getId());
            } catch (Exception e) {
                // Registrar errores
                System.err.println("Error al crear la factura: " + e.getMessage());
            }

            return factura;
        }
    }
