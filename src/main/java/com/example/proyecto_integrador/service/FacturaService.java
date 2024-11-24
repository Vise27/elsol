package com.example.proyecto_integrador.service;

import com.example.proyecto_integrador.model.Factura;
import com.example.proyecto_integrador.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

}
