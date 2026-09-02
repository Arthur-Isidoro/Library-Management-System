package com.seuprojeto.library;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.dao.LivroDAO;
import com.seuprojeto.library.dao.UsuarioDAO;
import com.seuprojeto.library.exception.LivroIndisponivelException;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;
import com.seuprojeto.library.service.EmprestimoService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoDAO);

        // 1. Cadastrar um usuário e um livro
        Usuario usuario = new Usuario("Arthur Isidoro", "arthur@email.com");
        usuarioDAO.inserir(usuario);
        System.out.println("Usuário inserido: " + usuario);

        Livro livro = new Livro("Effective Java", "Joshua Bloch", 2018);
        livroDAO.inserir(livro);
        System.out.println("Livro inserido: " + livro);

        // 2. Realizar o empréstimo através do Service (não mais direto pelo DAO)
        Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuario, livro);
        System.out.println("Empréstimo criado: " + emprestimo);

        // 3. Tentar emprestar o MESMO livro de novo — deve lançar LivroIndisponivelException
        System.out.println("\nTentando emprestar o mesmo livro novamente...");
        try {
            emprestimoService.realizarEmprestimo(usuario, livro);
        } catch (LivroIndisponivelException e) {
            System.out.println("Bloqueado como esperado: " + e.getMessage());
        }

        // 4. Listar ativos (deve ter só 1, mesmo após a tentativa bloqueada)
        System.out.println("\n--- Empréstimos ativos ---");
        List<Emprestimo> ativos = emprestimoDAO.listarAtivos();
        for (Emprestimo e : ativos) {
            System.out.println(e);
        }

        // 5. Registrar a devolução
        emprestimoDAO.registrarDevolucao(emprestimo.getId());
        System.out.println("\nDevolução registrada para o empréstimo id " + emprestimo.getId());

        // 6. Agora o mesmo livro deveria estar disponível de novo
        System.out.println("\nTentando emprestar o mesmo livro após devolução...");
        Emprestimo novoEmprestimo = emprestimoService.realizarEmprestimo(usuario, livro);
        System.out.println("Novo empréstimo criado com sucesso: " + novoEmprestimo);
    }
}