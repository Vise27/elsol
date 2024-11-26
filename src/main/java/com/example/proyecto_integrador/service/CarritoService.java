package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.CarritoItem;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.repository.CarritoItemRepository;
import com.example.proyecto_integrador.repository.CarritoRepository;
import com.example.proyecto_integrador.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
@AllArgsConstructor
@Service
public class CarritoService {

    private  final CarritoRepository carritoRepository;

    private final CarritoItemRepository carritoItemRepository;

    private final RestTemplate restTemplate;

    private final ProductoRepository productoRepository;


    @Autowired

    private ProductoService productoService;
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

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("No hay suficiente stock para el producto: " + producto.getNombre());
        }

        // Reducir el stock del producto
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto); // Guardar los cambios en el stock

        // Buscar o crear el item del carrito
        CarritoItem carritoItem = carritoItemRepository.findByCarritoAndProductoCodigo(carrito, producto.getCodigo());

        if (carritoItem != null) {
            carritoItem.setCantidad(carritoItem.getCantidad() + cantidad);
        } else {
            carritoItem = new CarritoItem();
            carritoItem.setCarrito(carrito);
            carritoItem.setProducto(producto);
            carritoItem.setCantidad(cantidad);
        }

        carritoItemRepository.save(carritoItem); // Guardar el item del carrito
        carritoRepository.save(carrito);        // Asegurar que el carrito también se guarda
    }

    public void eliminarProductoDelCarrito(Carrito carrito, Producto producto) {
        CarritoItem carritoItem = carritoItemRepository.findByCarritoAndProductoCodigo(carrito, producto.getCodigo());

        if (carritoItem != null) {
            producto.setStock(producto.getStock() + carritoItem.getCantidad());
            productoRepository.save(producto);

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
    public void actualizarCantidadProducto(CarritoItem carritoItem, int nuevaCantidad) {
        if (carritoItem == null || nuevaCantidad <= 0) {
            System.err.println("La cantidad debe ser mayor que cero y el item no debe ser nulo.");
            return;
        }

        // Obtener el producto asociado al CarritoItem
        Producto producto = carritoItem.getProducto();
        if (producto == null) {
            System.err.println("El producto asociado no existe.");
            return;
        }

        // Calcular la diferencia entre la cantidad actual y la nueva cantidad
        int diferencia = nuevaCantidad - carritoItem.getCantidad();

        // Verificar si hay suficiente stock si la diferencia es positiva (incremento de cantidad)
        if (diferencia > 0 && producto.getStock() < diferencia) {
            throw new IllegalStateException("No hay suficiente stock para ajustar la cantidad.");
        }

        // Ajustar el stock del producto
        producto.setStock(producto.getStock() - diferencia);
        productoRepository.save(producto); // Guardar los cambios en el stock

        // Actualizar la cantidad en el CarritoItem
        carritoItem.setCantidad(nuevaCantidad);
        carritoItemRepository.save(carritoItem); // Guardar los cambios del CarritoItem
    }


    public void vaciarCarrito(Carrito carrito) {
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        carritoItemRepository.deleteAll(items);

    }


    //public void vaciarCarrito(Carrito carrito) {
        // Obtener todos los items del carrito y eliminarlos de una sola vez
     //   List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
       // carritoItemRepository.deleteAll(items);

    //}
}