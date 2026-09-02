package com.seuprojeto.library.model;

import com.seuprojeto.library.enums.StatusEmprestimo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    private Usuario criarUsuario() {
        return new Usuario("Arthur", "arthur@email.com");
    }

    private Livro criarLivro() {
        return new Livro("Effective Java", "Joshua Bloch", 2018);
    }

    @Test
    void deveEstarAtrasadoQuandoAtivoEPrazoJaPassou() {
        Emprestimo emprestimo = new Emprestimo(
                criarUsuario(),
                criarLivro(),
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(3)
        );

        assertTrue(emprestimo.estaAtrasado());
    }

    @Test
    void naoDeveEstarAtrasadoQuandoPrazoVenceHoje() {
        Emprestimo emprestimo = new Emprestimo(
                criarUsuario(),
                criarLivro(),
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );

        assertFalse(emprestimo.estaAtrasado());
    }

    @Test
    void naoDeveEstarAtrasadoQuandoPrazoAindaNaoChegou() {
        Emprestimo emprestimo = new Emprestimo(
                criarUsuario(),
                criarLivro(),
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        assertFalse(emprestimo.estaAtrasado());
    }

    @Test
    void naoDeveEstarAtrasadoQuandoDevolvidoComPrazoVencido() {
        Emprestimo emprestimo = new Emprestimo(
                criarUsuario(),
                criarLivro(),
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(3)
        );
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        assertFalse(emprestimo.estaAtrasado());
    }

    @Test
    void naoDeveEstarAtrasadoAposDevolucaoDeEmprestimoQueEstavaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(
                criarUsuario(),
                criarLivro(),
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(3)
        );

        assertTrue(emprestimo.estaAtrasado(), "deveria estar atrasado antes da devolução");

        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucao(LocalDate.now());

        assertFalse(emprestimo.estaAtrasado(), "não deveria estar atrasado depois de devolvido");
    }
}