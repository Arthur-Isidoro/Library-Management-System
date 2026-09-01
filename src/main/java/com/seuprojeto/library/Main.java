package com.seuprojeto.library;

import com.seuprojeto.library.dao.EmprestimoDAO;
import com.seuprojeto.library.dao.LivroDAO;
import com.seuprojeto.library.dao.UsuarioDAO;
import com.seuprojeto.library.enums.StatusEmprestimo;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

        System.out.println("=== PASSO 1: Criando e inserindo Usuário e Livro ===");
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Art");
        novoUsuario.setEmail("art@email.com");
        
        usuarioDAO.inserir(novoUsuario); 
        System.out.println("Usuário criado com ID: " + novoUsuario.getId());

        Livro novoLivro = new Livro();
        novoLivro.setTitulo("Clean Code");
        novoLivro.setAutor("Robert C. Martin");
        novoLivro.setAno(2008);

        livroDAO.inserir(novoLivro);
        System.out.println("Livro criado com ID: " + novoLivro.getId());

        
        System.out.println("\n=== PASSO 2: Criando e inserindo o Empréstimo ===");
        
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(novoUsuario);
        emprestimo.setLivro(novoLivro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(14)); 
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        emprestimoDAO.inserir(emprestimo);
        System.out.println("Empréstimo realizado com sucesso! ID do Empréstimo: " + emprestimo.getId());


        System.out.println("\n=== PASSO 3: Validando listarAtivos() e o JOIN de Usuário/Livro ===");
        
        List<Emprestimo> ativos = emprestimoDAO.listarAtivos();
        System.out.println("Quantidade de empréstimos ativos encontrados: " + ativos.size());
        
        for (Emprestimo e : ativos) {
            System.out.println("--- Dados do Empréstimo ---");
            System.out.println("ID Empréstimo: " + e.getId());
            System.out.println("Usuário (JOIN): " + (e.getUsuario() != null ? e.getUsuario().getNome() : "NULO"));
            System.out.println("Livro (JOIN): " + (e.getLivro() != null ? e.getLivro().getTitulo() : "NULO"));
            System.out.println("Status: " + e.getStatus());
        }


        System.out.println("\n=== PASSO 4: Registrando Devolução ===");
        
        emprestimoDAO.registrarDevolucao(emprestimo.getId());
        System.out.println("Chamada de registrarDevolucao executada.");


        System.out.println("\n=== PASSO 5: Validando listarAtivos() após Devolução ===");
        
        List<Emprestimo> ativosAposDevolucao = emprestimoDAO.listarAtivos();
        System.out.println("Quantidade de empréstimos ativos após devolução: " + ativosAposDevolucao.size());
        
        boolean encontrado = ativosAposDevolucao.stream()
                .anyMatch(e -> e.getId() == emprestimo.getId());

        if (!encontrado) {
            System.out.println("SUCESSO: O empréstimo ID " + emprestimo.getId() + " não consta mais na lista de ATIVOS!");
        } else {
            System.out.println("ERRO: O empréstimo ainda está aparecendo como ativo.");
        }
    }
}