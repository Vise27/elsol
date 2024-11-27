package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    @Modifying
    @Query("DELETE FROM CarritoItem ci WHERE ci.carrito.id = :carritoId")
    void deleteAllByCarritoId(@Param("carritoId") Long carritoId);
    // Método para buscar un CarritoItem por carrito y código del producto
    CarritoItem findByCarritoAndProductoCodigo(Carrito carrito, String codigo);

    // Método para obtener los items de un carrito por su ID
    List<CarritoItem> findByCarritoId(Long idCarrito);
}
