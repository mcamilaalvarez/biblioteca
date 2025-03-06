package com.egg.biblioteca.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.egg.biblioteca.entidades.Editorial;

@Repository

public interface EditorialRepositorio extends JpaRepository<Editorial, UUID>{

    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre")
  public Editorial buscarPorNombreEditorial(@Param("nombre") String nombreEditorial);

    // @Query("SELECT e FROM Editorial WHERE e.id = :id")
    // public Editorial buscarPorId(@Param("id") String id);
}