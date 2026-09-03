package com.seuprojeto.library.service;

import com.seuprojeto.library.dao.UsuarioDAO;
import com.seuprojeto.library.exception.EmailDuplicadoException;
import com.seuprojeto.library.exception.UsuarioInvalidoException;
import com.seuprojeto.library.model.Usuario;

public class UsuarioService {

    private static final String EMAIL_REGEX = ".+@.+\\..+";

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void cadastrar(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new UsuarioInvalidoException("O nome do usuário não pode ser nulo ou vazio.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new UsuarioInvalidoException("O email do usuário não pode ser nulo ou vazio.");
        }

        if (!usuario.getEmail().matches(EMAIL_REGEX)) {
            throw new UsuarioInvalidoException("O email \"" + usuario.getEmail() + "\" não tem um formato válido.");
        }

        if (usuarioDAO.buscarPorEmail(usuario.getEmail()) != null) {
            throw new EmailDuplicadoException("O email já está em uso.");
        }

        usuarioDAO.inserir(usuario);
    }
}