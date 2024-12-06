package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.model.Categoria;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.service.CategoriaService;
import com.example.proyecto_integrador.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoriaController {
    private final CategoriaService categoriaService;

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
    public String listarProductosPorCategoria(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, Model model) {
        List<Producto> productos = productoService.buscarPorCategoria(id);

        List<Producto> productosConStock = productos.stream()
                .filter(producto -> producto.getStock() > 0)
                .toList();

        // PARA LA PAGINACION XD
        int pageSize = 8;
        int totalPages = (int) Math.ceil((double) productosConStock.size() / pageSize);

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, productosConStock.size());
        List<Producto> productosPaginados = productosConStock.subList(fromIndex, toIndex);

        model.addAttribute("productos", productosPaginados);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        List<Categoria> categorias = categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);

        model.addAttribute("categoriaId", id);

        return "index";
    }


}
