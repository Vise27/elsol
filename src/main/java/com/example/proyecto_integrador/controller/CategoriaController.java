package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.model.Categoria;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.service.CategoriaService;
import com.example.proyecto_integrador.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CategoriaController {
    @Autowired
    private final CategoriaService categoriaService;

    @Autowired
    private final ProductoService productoService;

    public CategoriaController(CategoriaService categoriaService, ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }



    @GetMapping({"home/categorias"})
    public String listarCategorias(Model model) {
        List<Categoria> categorias = this.categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);
        return "home/Categories";
    }


    @GetMapping("/categoria/{id}")
    public String listarProductosPorCategoria(@PathVariable Long id, Model model) {
        List<Producto> productos = productoService.buscarPorCategoria(id);
        model.addAttribute("productos", productos);

        List<Categoria> categorias = categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);

        model.addAttribute("categoriaId", id);

        return "index"; // Renderizar la misma plantilla
    }


}
