package com.seuprojeto.library;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.dao.LivroDAO;
import com.seuprojeto.library.dao.UsuarioDAO;
import com.seuprojeto.library.exception.EmprestimoJaDevolvidoException;
import com.seuprojeto.library.exception.LivroIndisponivelException;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;
import com.seuprojeto.library.service.EmprestimoService;

public class Main {

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoDAO);

        Usuario usuario = new Usuario("Arthur Isidoro", "arthur@email.com");
        usuarioDAO.inserir(usuario);
        System.out.println("Usuário inserido: " + usuario);

        Livro livro = new Livro("Effective Java", "Joshua Bloch", 2018);
        livroDAO.inserir(livro);
        System.out.println("Livro inserido: " + livro);

        Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuario, livro);
        System.out.println("Empréstimo criado: " + emprestimo);

        System.out.println("\nTentando emprestar o mesmo livro novamente...");
        try {
            emprestimoService.realizarEmprestimo(usuario, livro);
        } catch (LivroIndisponivelException e) {
            System.out.println("Bloqueado como esperado: " + e.getMessage());
        }

        System.out.println("\n--- Empréstimos ativos ---");
        for (Emprestimo e : emprestimoDAO.listarAtivos()) {
            System.out.println(e);
        }

        emprestimoService.devolver(emprestimo.getId());
        System.out.println("\nDevolução registrada para o empréstimo id " + emprestimo.getId());

        System.out.println("\nTentando devolver o mesmo empréstimo de novo...");
        try {
            emprestimoService.devolver(emprestimo.getId());
        } catch (EmprestimoJaDevolvidoException e) {
            System.out.println("Bloqueado como esperado: " + e.getMessage());
        }

        System.out.println("\nTentando emprestar o mesmo livro após devolução...");
        Emprestimo novoEmprestimo = emprestimoService.realizarEmprestimo(usuario, livro);
        System.out.println("Novo empréstimo criado com sucesso: " + novoEmprestimo);

        System.out.println("\nTentando devolver um empréstimo com id inexistente (99999)...");
        try {
            emprestimoService.devolver(99999);
        } catch (RuntimeException e) {
            System.out.println("Bloqueado como esperado: " + e.getMessage());
        }
    }
}