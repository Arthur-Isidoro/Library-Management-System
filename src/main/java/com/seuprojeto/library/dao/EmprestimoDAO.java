package com.seuprojeto.library.dao;

import com.seuprojeto.library.enums.StatusEmprestimo;
import com.seuprojeto.library.model.Emprestimo;
import com.seuprojeto.library.model.Livro;
import com.seuprojeto.library.model.Usuario;
import com.seuprojeto.library.util.ConexaoBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public void inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimo (usuario_id, livro_id, data_emprestimo, data_prevista_devolucao, data_devolucao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, emprestimo.getUsuario().getId());
            stmt.setInt(2, emprestimo.getLivro().getId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
            stmt.setDate(5, emprestimo.getDataDevolucao() != null ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            stmt.setString(6, emprestimo.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emprestimo.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir empréstimo: " + e.getMessage(), e);
        }
    }

    public Emprestimo buscarPorId(int emprestimoId) {
        String sql = "SELECT " +
                "e.id AS emprestimo_id, e.data_emprestimo, e.data_prevista_devolucao, e.data_devolucao, e.status, " +
                "u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email, " +
                "l.id AS livro_id, l.titulo AS livro_titulo, l.autor AS livro_autor, l.ano AS livro_ano " +
                "FROM emprestimo e " +
                "JOIN usuario u ON e.usuario_id = u.id " +
                "JOIN livro l ON e.livro_id = l.id " +
                "WHERE e.id = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEmprestimoComJoin(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empréstimo: " + e.getMessage(), e);
        }
    }

    public List<Emprestimo> listarAtivos() {
        List<Emprestimo> emprestimos = new ArrayList<>();

        String sql = "SELECT " +
                "e.id AS emprestimo_id, e.data_emprestimo, e.data_prevista_devolucao, e.data_devolucao, e.status, " +
                "u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email, " +
                "l.id AS livro_id, l.titulo AS livro_titulo, l.autor AS livro_autor, l.ano AS livro_ano " +
                "FROM emprestimo e " +
                "JOIN usuario u ON e.usuario_id = u.id " +
                "JOIN livro l ON e.livro_id = l.id " +
                "WHERE e.status = 'ATIVO'";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                emprestimos.add(mapearEmprestimoComJoin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empréstimos ativos: " + e.getMessage(), e);
        }

        return emprestimos;
    }

    public void registrarDevolucao(int emprestimoId) {
        String sql = "UPDATE emprestimo SET data_devolucao = ?, status = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setString(2, StatusEmprestimo.DEVOLVIDO.name());
            stmt.setInt(3, emprestimoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar devolução: " + e.getMessage(), e);
        }
    }

    public boolean livroEstaEmprestado(int livroId) {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE livro_id = ? AND status = 'ATIVO'";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livroId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar disponibilidade do livro: " + e.getMessage(), e);
        }
    }

    private Emprestimo mapearEmprestimoComJoin(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(rs.getString("usuario_nome"), rs.getString("usuario_email"));
        usuario.setId(rs.getInt("usuario_id"));

        Livro livro = new Livro(rs.getString("livro_titulo"), rs.getString("livro_autor"), rs.getInt("livro_ano"));
        livro.setId(rs.getInt("livro_id"));

        Emprestimo emprestimo = new Emprestimo(
                usuario,
                livro,
                rs.getDate("data_emprestimo").toLocalDate(),
                rs.getDate("data_prevista_devolucao").toLocalDate()
        );
        emprestimo.setId(rs.getInt("emprestimo_id"));
        emprestimo.setStatus(StatusEmprestimo.valueOf(rs.getString("status")));

        Date dataDevolucao = rs.getDate("data_devolucao");
        if (dataDevolucao != null) {
            emprestimo.setDataDevolucao(dataDevolucao.toLocalDate());
        }

        return emprestimo;
    }
}