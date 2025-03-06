package com.egg.biblioteca.repositorios;

import org.springframework.stereotype.Repository;
import com.egg.biblioteca.entidades.Autor;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor,UUID>{
    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
  public Autor buscaAutor(@Param("nombre") String nombre);

    //    @Query("SELECT a FROM Autor a WHERE a.id = :id")
    //    public Autor buscarPorId(@Param("id") String id);
   }