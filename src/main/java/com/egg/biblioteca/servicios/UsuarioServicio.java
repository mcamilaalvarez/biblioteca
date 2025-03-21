package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.enumeradores.Rol;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.UsuarioRepositorio;

import jakarta.servlet.http.HttpSession;


@Service
public class UsuarioServicio implements UserDetailsService  {
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired 
    private ImagenServicio imagenServicio;

    @Transactional
    public void crearUsuario(String nombre, String email, String password, String password2, MultipartFile archivo) throws MiException{
        System.out.println("Estoy validando el usuario");
        validar(nombre, email, password, password2);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        Imagen imagen = imagenServicio.guardar(archivo);
        usuario.setImagen(imagen);
        usuarioRepositorio.save(usuario);
    }


    private void validar(String nombre, String email, String password, String password2) throws MiException {


        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacío");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios = usuarioRepositorio.findAll();
        return usuarios;
    }
    
    @Transactional(readOnly = true)
    public Usuario getOne(String id){
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Transactional
    public void modificarUsuario(String id, String nombre, String email, String password, String password2, MultipartFile archivo) throws MiException {

        validar(nombre, email, password, password2);

        Optional<Usuario> respuesta = Optional.ofNullable(usuarioRepositorio.buscarPorEmail(email));
        

        if (respuesta.isEmpty()) {
            throw new MiException("El usuario especificado no existe.");
        }

        Usuario usuario = respuesta.get();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        Imagen imagen = imagenServicio.guardar(archivo);
        usuario.setImagen(imagen);

        usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void cambiarRol(String id){
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if(respuesta.isPresent()){
            Usuario usuario = respuesta.get();
            if(usuario.getRol().equals(Rol.USER)){
                usuario.setRol(Rol.ADMIN);
            }else if (usuario.getRol().equals(Rol.ADMIN)){
                usuario.setRol(Rol.USER);
            }
            
        }
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
       
        
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }

}
}
