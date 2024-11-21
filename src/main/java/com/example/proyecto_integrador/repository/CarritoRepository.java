package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito,Long> {
    Carrito findByUsuarioUsername(String username);
}
