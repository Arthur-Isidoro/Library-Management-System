package com.seuprojeto.library.service;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.enums.StatusEmprestimo;
import com.seuprojeto.library.exception.LivroIndisponivelException;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoDAO emprestimoDAO;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    void deveRealizarEmprestimoQuandoLivroDisponivel() {
        when(emprestimoDAO.livroEstaEmprestado(anyInt())).thenReturn(false);

        Usuario usuario = new Usuario("Arthur", "arthur@email.com");
        Livro livro = new Livro("Effective Java", "Joshua Bloch", 2018);

        Emprestimo resultado = emprestimoService.realizarEmprestimo(usuario, livro);

        assertEquals(usuario, resultado.getUsuario());
        assertEquals(StatusEmprestimo.ATIVO, resultado.getStatus());
        verify(emprestimoDAO).inserir(resultado);
    }

    @Test
    void deveLancarLivroIndisponivelExceptionQuandoLivroJaEmprestado() {
        when(emprestimoDAO.livroEstaEmprestado(anyInt())).thenReturn(true);

        Usuario usuario = new Usuario("Arthur", "arthur@email.com");
        Livro livro = new Livro("Effective Java", "Joshua Bloch", 2018);

        assertThrows(
                LivroIndisponivelException.class,
                () -> emprestimoService.realizarEmprestimo(usuario, livro)
        );

        verify(emprestimoDAO, never()).inserir(any());
    }
}