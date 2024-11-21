package com.example.proyecto_integrador.repository;

import com.example.proyecto_integrador.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends CrudRepository<User ,Integer> {

    User findByUsername(String username);
}
