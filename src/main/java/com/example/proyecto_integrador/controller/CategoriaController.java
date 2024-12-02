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
        // Obtener los productos de la categoría específica
        List<Producto> productos = productoService.buscarPorCategoria(id);

        // Filtrar los productos con stock disponible
        List<Producto> productosConStock = productos.stream()
                .filter(producto -> producto.getStock() > 0)
                .toList();

        // Tamaño de la página (16 productos por página)
        int pageSize = 16;
        int totalPages = (int) Math.ceil((double) productosConStock.size() / pageSize);

        // Validar que el número de página esté dentro del rango
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        // Obtener la sublista correspondiente a la página actual
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, productosConStock.size());
        List<Producto> productosPaginados = productosConStock.subList(fromIndex, toIndex);

        // Agregar datos al modelo
        model.addAttribute("productos", productosPaginados);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // Obtener todas las categorías para mostrarlas en el filtro
        List<Categoria> categorias = categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);

        // Añadir el id de la categoría actual al modelo
        model.addAttribute("categoriaId", id);

        return "index"; // Renderizar la misma plantilla con productos paginados
    }


}
