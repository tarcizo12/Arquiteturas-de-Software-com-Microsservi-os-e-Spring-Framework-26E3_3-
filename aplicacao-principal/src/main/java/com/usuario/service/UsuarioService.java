package com.usuario.service;

import com.usuario.domain.entity.UsuarioEntity;
import com.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.repository = usuarioRepository;
    }

    public UsuarioEntity getUsuarioById(Long id ){return this.repository.findById(id).orElse(null); }
}
