package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://api-zsm7.onrender.com/api/productos/";

    public ProductoService() {
    }

    public List<Producto> listarProductos() {
        return this.productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return this.productoRepository.findById(id).orElse(null);
    }

    public List<Producto> obtenerProductosDesdeApi() {
        try {
            ResponseEntity<Producto[]> response = this.restTemplate.getForEntity(API_URL, Producto[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                System.out.println("Error: API respondió con código no exitoso.");
                return this.listarProductos();
            }
        } catch (RestClientException e) {
            System.out.println("Error al consumir la API externa: " + e.getMessage());
            return this.listarProductos();
        }
    }

    // Aquí cambiamos el parámetro a String, ya que ahora estamos usando 'codigo' como String
    public Producto obtenerProductoPorIdDesdeApi(String codigo) {
        try {
            String url = API_URL + "?codigo=" + codigo;
            ResponseEntity<Producto[]> response = restTemplate.getForEntity(url, Producto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Producto[] productos = response.getBody();
                if (productos.length > 0) {
                    return productos[0]; // Devuelve el primer producto
                }
            }
        } catch (RestClientException e) {
            System.out.println("Error al consumir la API externa: " + e.getMessage());
        }

        return null; // Devuelve null si no se encontró el producto
    }

    public List<Producto> buscarProductos(String query) {
        List<Producto> productosApi = this.obtenerProductosDesdeApi();
        return productosApi.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public boolean verificarStock(Long productoId, int cantidad) {
        Producto producto = this.obtenerProductoPorIdDesdeApi(String.valueOf(productoId)); // Convertimos el Long a String si es necesario
        return producto != null && producto.getStock() >= cantidad;
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        try {
            String url = API_URL + "?categoria=" + categoriaId;
            ResponseEntity<Producto[]> response = restTemplate.getForEntity(url, Producto[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                System.out.println("Error: API respondió con un código no exitoso.");
                return List.of();
            }
        } catch (RestClientException e) {
            System.out.println("Error al consumir la API: " + e.getMessage());
            return List.of();
        }
    }
}
