package com.clinica.dao;

import com.clinica.model.Paciente;
import com.clinica.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Paciente — operações CRUD na tabela `pacientes`.
 */
public class PacienteDAO {

    // ── LISTAR TODOS ──────────────────────────────────────────────────────────
    public List<Paciente> listarTodos() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, sexo, data_cadastro, ativo "
                   + "FROM pacientes WHERE ativo = TRUE ORDER BY nome";

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
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, sexo, data_cadastro, ativo "
                   + "FROM pacientes WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public Paciente login(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, sexo, data_cadastro, ativo "
                   + "FROM pacientes "
                   + "WHERE email = ? AND senha_hash = SHA2(?, 256) AND ativo = TRUE";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── CADASTRAR ────────────────────────────────────────────────────────────
    public boolean cadastrar(Paciente p) throws SQLException {
        String sql = "INSERT INTO pacientes (nome, cpf, email, telefone, data_nascimento, sexo, senha_hash) "
                   + "VALUES (?, ?, ?, ?, ?, ?, SHA2(?, 256))";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getTelefone());
            ps.setDate(5, Date.valueOf(p.getDataNascimento()));
            ps.setString(6, p.getSexo());
            ps.setString(7, p.getSenha());

            return ps.executeUpdate() > 0;
        }
    }

    // ── ATUALIZAR ────────────────────────────────────────────────────────────
    public boolean atualizar(Paciente p) throws SQLException {
        String sql = "UPDATE pacientes SET nome = ?, telefone = ?, sexo = ?, data_nascimento = ? "
                   + "WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getTelefone());
            ps.setString(3, p.getSexo());

            if (p.getDataNascimento() != null && !p.getDataNascimento().isBlank()) {
                ps.setDate(4, Date.valueOf(p.getDataNascimento()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── EXCLUIR (soft delete) ─────────────────────────────────────────────────
    public boolean excluir(int id) throws SQLException {
        String sql = "UPDATE pacientes SET ativo = FALSE WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── REDEFINIR SENHA ───────────────────────────────────────────────────────
    public boolean redefinirSenha(String email, String novaSenha) throws SQLException {
        String sql = "UPDATE pacientes SET senha_hash = SHA2(?, 256) WHERE email = ? AND ativo = TRUE";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, novaSenha);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MAPEAMENTO ResultSet → Paciente ──────────────────────────────────────
    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCpf(rs.getString("cpf"));
        p.setEmail(rs.getString("email"));
        p.setTelefone(rs.getString("telefone"));

        Date dn = rs.getDate("data_nascimento");
        p.setDataNascimento(dn != null ? dn.toString() : null);

        p.setSexo(rs.getString("sexo"));
        p.setDataCadastro(rs.getTimestamp("data_cadastro"));
        p.setAtivo(rs.getBoolean("ativo"));
        return p;
    }
}