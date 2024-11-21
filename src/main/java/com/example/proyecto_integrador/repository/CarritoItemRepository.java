package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    // Método para buscar un CarritoItem por carrito y código del producto
    CarritoItem findByCarritoAndProductoCodigo(Carrito carrito, String codigo);

    // Método para obtener los items de un carrito por su ID
    List<CarritoItem> findByCarritoId(Long idCarrito);
}
