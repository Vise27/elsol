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
            double totalCarrito = carritoService.calcularTotalCarrito(carrito);  // Cambiar a carritoService

            // Crear la factura
            Factura factura = new Factura();
            factura.setUsuario(usuario);
            factura.setCarrito(carrito);
            factura.setTotal(totalCarrito);

            factura = facturaRepository.save(factura); // Guardar la factura

            // Crear los detalles de la factura
            List<CarritoItem> carritoItems = carrito.getItems();
            for (CarritoItem item : carritoItems) {
                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setProducto(item.getProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getProducto().getPrecio());

                detalleFacturaRepository.save(detalle); // Guardar el detalle
            }

            return factura;
        }
    }
