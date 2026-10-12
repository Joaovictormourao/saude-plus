package com.clinica.dao;

import com.clinica.model.Especialidade;
import com.clinica.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Especialidade — CRUD na tabela `especialidades`.
 */
public class EspecialidadeDAO {

    // ── LISTAR TODAS (ativas) ─────────────────────────────────────────────────
    public List<Especialidade> listarTodas() throws SQLException {
        List<Especialidade> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, icone, ativo, data_criacao "
                   + "FROM especialidades WHERE ativo = TRUE ORDER BY nome";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────────────────────
    public Especialidade buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, icone, ativo, data_criacao "
                   + "FROM especialidades WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── INSERIR ───────────────────────────────────────────────────────────────
    public boolean inserir(Especialidade e) throws SQLException {
        String sql = "INSERT INTO especialidades (nome, descricao, icone) VALUES (?, ?, ?)";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.setString(3, e.getIcone());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ATUALIZAR ─────────────────────────────────────────────────────────────
    public boolean atualizar(Especialidade e) throws SQLException {
        String sql = "UPDATE especialidades SET nome = ?, descricao = ?, icone = ? WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.setString(3, e.getIcone());
            ps.setInt(4, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── EXCLUIR (soft delete) ─────────────────────────────────────────────────
    public boolean excluir(int id) throws SQLException {
        String sql = "UPDATE especialidades SET ativo = FALSE WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MAPEAMENTO ────────────────────────────────────────────────────────────
    private Especialidade mapear(ResultSet rs) throws SQLException {
        Especialidade e = new Especialidade();
        e.setId(rs.getInt("id"));
        e.setNome(rs.getString("nome"));
        e.setDescricao(rs.getString("descricao"));
        e.setIcone(rs.getString("icone"));
        e.setAtivo(rs.getBoolean("ativo"));
        e.setDataCriacao(rs.getTimestamp("data_criacao"));
        return e;
    }
}