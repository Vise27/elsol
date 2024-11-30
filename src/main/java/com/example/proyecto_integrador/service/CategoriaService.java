package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Categoria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
@AllArgsConstructor
@Service
public class CategoriaService {

    private static final String API_URL = "https://api-zsm7.onrender.com/api/categorias/";

    private final RestTemplate restTemplate;


    public List<Categoria> listarCategorias() {
        Categoria[] categoriasArray = restTemplate.getForObject(API_URL, Categoria[].class);
        return categoriasArray != null ? Arrays.asList(categoriasArray) : null;
    }
}