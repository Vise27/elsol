
package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Categoria;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.repository.CategoriaRepository;
import com.example.proyecto_integrador.repository.ProductoRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private RestTemplate restTemplate;
    private static final String API_URL = "https://api-zsm7.onrender.com/api/productos/";

    public ProductoService() {
    }

    public List<Producto> listarProductos() {
        return this.productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return (Producto)this.productoRepository.findById(id).orElse((Producto) null);
    }

    public Producto guardarProducto(Producto producto) {
        return (Producto)this.productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        this.productoRepository.deleteById(id);
    }

    public List<Categoria> obtenerCategorias() {
        return this.categoriaRepository.findAll();
    }

    public List<Producto> obtenerProductosDesdeApi() {
        try {
            ResponseEntity<Producto[]> response = this.restTemplate.getForEntity("https://api-zsm7.onrender.com/api/productos/", Producto[].class, new Object[0]);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList((Producto[])response.getBody());
            } else {
                System.out.println("Error: API respondió con código no exitoso.");
                return this.listarProductos();
            }
        } catch (RestClientException var2) {
            RestClientException e = var2;
            System.out.println("Error al consumir la API externa: " + e.getMessage());
            return this.listarProductos();
        }
    }

    public Producto obtenerProductoPorIdDesdeApi(Long id) {
        try {
            ResponseEntity<Producto> response = this.restTemplate.getForEntity("https://api-zsm7.onrender.com/api/productos/" + id, Producto.class, new Object[0]);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Producto)response.getBody();
            }
        } catch (RestClientException var3) {
            RestClientException e = var3;
            System.out.println("Error al consumir la API externa para obtener producto por ID: " + e.getMessage());
        }

        return this.obtenerProductoPorId(id);
    }

    public List<Producto> buscarProductos(String query) {
        List<Producto> productosApi = this.obtenerProductosDesdeApi();
        return productosApi.stream().filter((p) -> {
            return p.getNombre().toLowerCase().contains(query.toLowerCase());
        }).toList();
    }
    public boolean verificarStock(Long productoId, int cantidad) {
        Producto producto = this.obtenerProductoPorIdDesdeApi(productoId);
        return producto != null && producto.getStock() >= cantidad;
    }

}
