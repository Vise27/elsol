package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.CarritoItem;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.repository.CarritoItemRepository;
import com.example.proyecto_integrador.repository.CarritoRepository;
import com.example.proyecto_integrador.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    private final ProductoService productoService;
    private static final String PRODUCTO_API_URL = "https://elsol.up.railway.app/api/productos/";

    public Carrito obtenerCarritoPorUsuario(String username) {
        return carritoRepository.findByUsuarioUsername(username);
    }

    public List<CarritoItem> obtenerItemsDelCarrito(Long carritoId) {
        List<CarritoItem> carritoItems = carritoItemRepository.findByCarritoId(carritoId);

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
            System.err.println("Error al obtener el producto desde la API: " + e.getMessage());
            return null;
        }
    }


    public void agregarProductoAlCarrito(Carrito carrito, Producto producto, int cantidad) {
        if (cantidad <= 0) {
            System.err.println("La cantidad no puede ser menor o igual a cero.");
            return;
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("No hay suficiente stock para el producto: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

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
        carritoRepository.save(carrito);
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

    public CarritoItem obtenerCarritoItemPorId(Long id) {
        return carritoItemRepository.findById(id).orElse(null);
    }

    public void actualizarCantidadProducto(CarritoItem carritoItem, int nuevaCantidad) {
        if (carritoItem == null || nuevaCantidad <= 0) {
            System.err.println("La cantidad debe ser mayor que cero y el item no debe ser nulo.");
            return;
        }

        Producto producto = carritoItem.getProducto();

        int diferencia = nuevaCantidad - carritoItem.getCantidad();

        if (diferencia > 0 && producto.getStock() < diferencia) {
            throw new IllegalStateException("No hay suficiente stock para ajustar la cantidad.");
        }

        producto.setStock(producto.getStock() - diferencia);
        productoRepository.save(producto);

        carritoItem.setCantidad(nuevaCantidad);
        carritoItemRepository.save(carritoItem);
    }

    @Transactional
    public void vaciarCarrito(Long carritoId) {
        carritoItemRepository.deleteAllByCarritoId(carritoId);
    }



}