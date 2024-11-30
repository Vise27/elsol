
package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.model.Categoria;
import com.example.proyecto_integrador.model.Producto;
import com.example.proyecto_integrador.service.CategoriaService;
import com.example.proyecto_integrador.service.ProductoService;
import java.util.List;
import lombok.Generated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/"})
public class HomeController {

    private final ProductoService productoService;

    private final CategoriaService categoriaService;

    @ModelAttribute("categorias")
    public List<Categoria> obtenerCategorias() {
        return categoriaService.listarCategorias();
    }
    @GetMapping({"/"})
    public String listarProductos(@RequestParam(defaultValue = "0") int page,Model model) {

        List<Producto> productos = this.productoService.obtenerProductosDesdeApi();
        model.addAttribute("productos", productos);

        return "index";
    }


    @GetMapping({"/admin"})
    public String admin() {
        return "Admin";
    }

    @GetMapping("home/producto/detalle/{codigo}")
    public String detalleProducto(@PathVariable("codigo") String codigo, Model model) {
        try {
            Producto producto = productoService.obtenerProductoPorIdDesdeApi(codigo);

            if (producto != null) {
                model.addAttribute("producto", producto);
                return "Productos/detalle";
            } else {
                model.addAttribute("error", "Producto no encontrado.");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al obtener el producto: " + e.getMessage());
            return "error";
        }
    }


    @GetMapping({"/home/buscarProductos"})
    public String buscarProductos(@RequestParam("query") String query, Model model) {
        List<Producto> productos = this.productoService.buscarProductos(query);
        model.addAttribute("productos", productos);
        return "Productos/resultadoProducto";
    }

    @Generated
    public HomeController(final CategoriaService categoriaService, final ProductoService productoService, CategoriaService categoriaService1) {
        this.productoService = productoService;
        this.categoriaService = categoriaService1;
    }
}
