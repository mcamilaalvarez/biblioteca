package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.entidades.Libro;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.AutorRepositorio;
import com.egg.biblioteca.repositorios.EditorialRepositorio;
import com.egg.biblioteca.repositorios.LibroRepositorio;

@Service
public class LibroServicio {
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearLibro(Long isbn, String titulo, Integer ejemplares, UUID idAutor, UUID idEditorial) throws MiException{
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);
        Autor autor = autorRepositorio.findById(idAutor).get();
        Editorial editorial = editorialRepositorio.findById(idEditorial).get();

        Libro libro = new Libro();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setEjemplares(ejemplares);
        libro.setAlta(new Date());
        libro.setAutor(autor);
        libro.setEditorial(editorial);

        libroRepositorio.save(libro);
    }

    @Transactional(readOnly = true)
    public List<Libro> listaLibros(){
        List<Libro> libros = new ArrayList<>();
        libros = libroRepositorio.findAll();
        return libros;
    }

    
    @Transactional
    public void modificarLibro(Long isbn, String titulo, int ejemplares, UUID idAutor, UUID idEditorial) throws MiException{
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);
        Optional<Libro>  respuesta = libroRepositorio.findById(isbn);
        Optional<Autor>  autor = autorRepositorio.findById(idAutor);
        Optional<Editorial>  editorial = editorialRepositorio.findById(idEditorial);


        if(respuesta.isPresent()){
            Libro libro = respuesta.get();
            libro.setTitulo(titulo);
            libro.setEjemplares(ejemplares);
            if(autor.isPresent()){
                Autor autorMod = autor.get();
                libro.setAutor(autorMod);
            }
            if(editorial.isPresent()){
                Editorial editorialMod = editorial.get();
                libro.setEditorial(editorialMod);
            }
         libroRepositorio.save(libro);
        }
    }

     private void validar(Long isbn, String titulo, Integer ejemplares, UUID idAutor, UUID idEditorial) throws MiException {
        if ( isbn == null) {
            throw new MiException("el isbn no puede ser nulo ");
        }
        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (ejemplares == null) {
            throw new MiException("Los ejemplares no pueden ser nulo");
        }
        if ( idAutor == null) {
            throw new MiException("El id del autor no puede ser nulo ");
        }
        if ( idEditorial == null) {
            throw new MiException("El id de la editorial no puede ser nulo ");
        }
    }

    @Transactional(readOnly = true)
    public Libro getOne(Long isbn){
        return libroRepositorio.getReferenceById(isbn);
    }
}
