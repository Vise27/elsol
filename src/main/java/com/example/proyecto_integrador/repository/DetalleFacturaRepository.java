package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura,Long> {
}
