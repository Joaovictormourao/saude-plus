package com.clinica.dao;

import com.clinica.model.Medico;
import com.clinica.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Médico — CRUD + login na tabela `medicos`.
 */
public class MedicoDAO {

    // ── LISTAR TODOS (ativo) ──────────────────────────────────────────────────
    public List<Medico> listarTodos() throws SQLException {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT m.id, m.nome, m.especialidade_id, e.nome AS especialidade, "
                   + "m.crm, m.email, m.telefone, m.data_cadastro, m.ativo "
                   + "FROM medicos m "
                   + "JOIN especialidades e ON m.especialidade_id = e.id "
                   + "WHERE m.ativo = TRUE ORDER BY m.nome";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── LISTAR POR ESPECIALIDADE ──────────────────────────────────────────────
    public List<Medico> listarPorEspecialidade(int especialidadeId) throws SQLException {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT m.id, m.nome, m.especialidade_id, e.nome AS especialidade, "
                   + "m.crm, m.email, m.telefone, m.data_cadastro, m.ativo "
                   + "FROM medicos m "
                   + "JOIN especialidades e ON m.especialidade_id = e.id "
                   + "WHERE m.especialidade_id = ? AND m.ativo = TRUE ORDER BY m.nome";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, especialidadeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────────────────────
    public Medico buscarPorId(int id) throws SQLException {
        String sql = "SELECT m.id, m.nome, m.especialidade_id, e.nome AS especialidade, "
                   + "m.crm, m.email, m.telefone, m.data_cadastro, m.ativo "
                   + "FROM medicos m "
                   + "JOIN especialidades e ON m.especialidade_id = e.id "
                   + "WHERE m.id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── LOGIN MÉDICO ──────────────────────────────────────────────────────────
    public Medico login(String email, String senha) throws SQLException {
        String sql = "SELECT m.id, m.nome, m.especialidade_id, e.nome AS especialidade, "
                   + "m.crm, m.email, m.telefone, m.data_cadastro, m.ativo "
                   + "FROM medicos m "
                   + "JOIN especialidades e ON m.especialidade_id = e.id "
                   + "WHERE m.email = ? AND m.senha_hash = SHA2(?, 256) AND m.ativo = TRUE";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── MAPEAMENTO ────────────────────────────────────────────────────────────
    private Medico mapear(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setId(rs.getInt("id"));
        m.setNome(rs.getString("nome"));
        m.setEspecialidadeId(rs.getInt("especialidade_id"));
        m.setEspecialidade(rs.getString("especialidade"));
        m.setCrm(rs.getString("crm"));
        m.setEmail(rs.getString("email"));
        m.setTelefone(rs.getString("telefone"));
        m.setDataCadastro(rs.getTimestamp("data_cadastro"));
        m.setAtivo(rs.getBoolean("ativo"));
        return m;
    }
}