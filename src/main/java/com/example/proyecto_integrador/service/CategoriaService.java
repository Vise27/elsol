package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Categoria;
import com.example.proyecto_integrador.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoriaService {

    private static final String API_URL = "https://api-zsm7.onrender.com/api/categorias/";

    @Autowired
    private RestTemplate restTemplate;


    public List<Categoria> listarCategorias() {
        Categoria[] categoriasArray = restTemplate.getForObject(API_URL, Categoria[].class);
        return categoriasArray != null ? Arrays.asList(categoriasArray) : null;
    }
}