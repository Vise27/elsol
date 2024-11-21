package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.CarritoItem;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.repository.CarritoItemRepository;
import com.example.proyecto_integrador.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String PRODUCTO_API_URL = "https://api-zsm7.onrender.com/api/productos/";

    // Obtener el carrito de un usuario por su username
    public Carrito obtenerCarritoPorUsuario(String username) {
        return carritoRepository.findByUsuarioUsername(username);
    }

    // Obtener todos los items del carrito
    public List<CarritoItem> obtenerItemsDelCarrito(Long carritoId) {
        List<CarritoItem> carritoItems = carritoItemRepository.findByCarritoId(carritoId);

        // Enriquecer los datos de los productos con la API
        for (CarritoItem item : carritoItems) {
            Producto producto = obtenerDetallesProductoDesdeAPI(item.getProducto().getCodigo());
            if (producto != null) {
                item.setProducto(producto);
            }
        }

        return carritoItems;
    }

    public Producto obtenerDetallesProductoDesdeAPI(String productoCodigo) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(PRODUCTO_API_URL)
                    .queryParam("codigo", productoCodigo);

            Producto[] productos = restTemplate.getForObject(uriBuilder.toUriString(), Producto[].class);

            if (productos != null && productos.length > 0) {
                return productos[0];
            } else {
                System.err.println("No se encontró el producto.");
                return null;
            }
        } catch (RestClientException e) {
            // Manejo de errores de la API
            System.err.println("Error al obtener el producto desde la API: " + e.getMessage());
            return null;
        }
    }


    // Agregar un producto al carrito
    public void agregarProductoAlCarrito(Carrito carrito, Producto producto, int cantidad) {
        if (cantidad <= 0) {
            System.err.println("La cantidad no puede ser menor o igual a cero.");
            return;
        }

        CarritoItem carritoItem = carritoItemRepository.findByCarritoAndProductoCodigo(carrito, producto.getCodigo());

        if (carritoItem != null) {
            carritoItem.setCantidad(carritoItem.getCantidad() + cantidad);
        } else {
            carritoItem = new CarritoItem();
            carritoItem.setCarrito(carrito);
            carritoItem.setProducto(producto);
            carritoItem.setCantidad(cantidad);
        }

        carritoItemRepository.save(carritoItem);
        carritoRepository.save(carrito);  // Asegúrate de que el carrito también se guarda
    }


    // Eliminar un producto del carrito
    public void eliminarProductoDelCarrito(Carrito carrito, Producto producto) {
        CarritoItem carritoItem = carritoItemRepository.findByCarritoAndProductoCodigo(carrito, producto.getCodigo());

        if (carritoItem != null) {
            carritoItemRepository.delete(carritoItem);
        } else {
            System.err.println("El producto no está en el carrito.");
        }
    }

    // Calcular el total del carrito
    public double calcularTotalCarrito(Carrito carrito) {
        double total = 0.0;
        List<CarritoItem> items = obtenerItemsDelCarrito(carrito.getId());
        for (CarritoItem item : items) {
            if (item.getProducto() != null && item.getCantidad() > 0) {
                total += item.getCantidad() * item.getProducto().getPrecio();
            }
        }
        return total;
    }

    // Obtener un item del carrito por su ID
    public CarritoItem obtenerCarritoItemPorId(Long id) {
        return carritoItemRepository.findById(id).orElse(null);
    }

    // Actualizar la cantidad de un producto en el carrito
    public void actualizarCantidadProducto(CarritoItem carritoItem, int cantidad) {
        if (carritoItem != null && cantidad > 0) {
            carritoItem.setCantidad(cantidad);
            carritoItemRepository.save(carritoItem);
        } else {
            System.err.println("La cantidad debe ser mayor que cero.");
        }
    }

    public void vaciarCarrito(Carrito carrito) {
        // Obtener todos los items del carrito y eliminarlos de una sola vez
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        carritoItemRepository.deleteAll(items);

    }
}
