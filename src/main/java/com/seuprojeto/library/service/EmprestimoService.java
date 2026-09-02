package com.seuprojeto.library.service;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.exception.LivroIndisponivelException;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;

import java.time.LocalDate;

public class EmprestimoService {

    private static final int PRAZO_PADRAO_DIAS = 7;

    private final EmprestimoDAO emprestimoDAO;

    public EmprestimoService(EmprestimoDAO emprestimoDAO) {
        this.emprestimoDAO = emprestimoDAO;
    }

    public Emprestimo realizarEmprestimo(Usuario usuario, Livro livro) {
        if (emprestimoDAO.livroEstaEmprestado(livro.getId())) {
            throw new LivroIndisponivelException(
                    "O livro \"" + livro.getTitulo() + "\" já está emprestado."
            );
        }

        Emprestimo emprestimo = new Emprestimo(
                usuario,
                livro,
                LocalDate.now(),
                LocalDate.now().plusDays(PRAZO_PADRAO_DIAS)
        );

        emprestimoDAO.inserir(emprestimo);
        return emprestimo;
    }
}