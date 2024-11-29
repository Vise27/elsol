package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    Ubicacion findByUsuarioUsername(String username);
}