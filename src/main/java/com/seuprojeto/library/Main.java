package com.seuprojeto.library;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.dao.LivroDAO;
import com.seuprojeto.library.dao.UsuarioDAO;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;
import com.seuprojeto.library.service.EmprestimoService;
import com.seuprojeto.library.service.LivroService;
import com.seuprojeto.library.service.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final LivroDAO livroDAO = new LivroDAO();
    private static final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    private static final UsuarioService usuarioService = new UsuarioService(usuarioDAO);
    private static final LivroService livroService = new LivroService(livroDAO);
    private static final EmprestimoService emprestimoService = new EmprestimoService(emprestimoDAO);

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            exibirMenu();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1 -> cadastrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> cadastrarLivro();
                case 4 -> listarLivros();
                case 5 -> realizarEmprestimo();
                case 6 -> devolverLivro();
                case 7 -> listarEmprestimosAtivos();
                case 0 -> {
                    System.out.println("Encerrando o sistema. Até logo!");
                    continuar = false;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("=== SISTEMA DE BIBLIOTECA ===");
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Listar usuários");
        System.out.println("3 - Cadastrar livro");
        System.out.println("4 - Listar livros");
        System.out.println("5 - Realizar empréstimo");
        System.out.println("6 - Devolver livro");
        System.out.println("7 - Listar empréstimos ativos");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        String entrada = scanner.nextLine();
        try {
            return Integer.parseInt(entrada.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int lerInt(String mensagem) {
        System.out.print(mensagem);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            Usuario usuario = new Usuario(nome, email);
            usuarioService.cadastrar(usuario);
            System.out.println("Usuário cadastrado com sucesso: " + usuario);
        } catch (RuntimeException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private static void cadastrarLivro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        try {
            int ano = lerInt("Ano: ");
            Livro livro = new Livro(titulo, autor, ano);
            livroService.cadastrar(livro);
            System.out.println("Livro cadastrado com sucesso: " + livro);
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido — digite apenas números.");
        } catch (RuntimeException e) {
            System.out.println("Erro ao cadastrar livro: " + e.getMessage());
        }
    }

    private static void listarLivros() {
        List<Livro> livros = livroDAO.listarLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        livros.forEach(System.out::println);
    }

    private static void realizarEmprestimo() {
        try {
            int usuarioId = lerInt("ID do usuário: ");
            int livroId = lerInt("ID do livro: ");

            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
            if (usuario == null) {
                System.out.println("Nenhum usuário encontrado com id " + usuarioId);
                return;
            }

            Livro livro = livroDAO.buscarPorId(livroId);
            if (livro == null) {
                System.out.println("Nenhum livro encontrado com id " + livroId);
                return;
            }

            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuario, livro);
            System.out.println("Empréstimo realizado com sucesso: " + emprestimo);

        } catch (NumberFormatException e) {
            System.out.println("ID inválido — digite apenas números.");
        } catch (RuntimeException e) {
            System.out.println("Erro ao realizar empréstimo: " + e.getMessage());
        }
    }

    private static void devolverLivro() {
        try {
            int emprestimoId = lerInt("ID do empréstimo: ");
            emprestimoService.devolver(emprestimoId);
            System.out.println("Devolução registrada com sucesso.");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido — digite apenas números.");
        } catch (RuntimeException e) {
            System.out.println("Erro ao registrar devolução: " + e.getMessage());
        }
    }

    private static void listarEmprestimosAtivos() {
        List<Emprestimo> ativos = emprestimoDAO.listarAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo no momento.");
            return;
        }
        for (Emprestimo e : ativos) {
            String atraso = e.estaAtrasado() ? " [ATRASADO]" : "";
            System.out.println(e + atraso);
        }
    }
}