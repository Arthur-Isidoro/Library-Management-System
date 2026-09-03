package com.seuprojeto.library.service;

import com.seuprojeto.library.dao.LivroDAO;
import com.seuprojeto.library.exception.LivroInvalidoException;
import com.seuprojeto.library.model.Livro;

import java.time.LocalDate;

public class LivroService {

    private final LivroDAO livroDAO;

    public LivroService(LivroDAO livroDAO) {
        this.livroDAO = livroDAO;
    }

    public void cadastrar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new LivroInvalidoException("O título do livro não pode ser nulo ou vazio.");
        }

        if (livro.getAutor() == null || livro.getAutor().isBlank()) {
            throw new LivroInvalidoException("O autor do livro não pode ser nulo ou vazio.");
        }

        int anoAtual = LocalDate.now().getYear();
        if (livro.getAno() <= 0 || livro.getAno() > anoAtual) {
            throw new LivroInvalidoException("O ano \"" + livro.getAno() + "\" não é válido.");
        }

        livroDAO.inserir(livro);
    }
}