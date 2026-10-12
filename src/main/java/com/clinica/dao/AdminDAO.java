package com.clinica.dao;

import com.clinica.model.Administrador;
import com.clinica.util.ConexaoDB;

import java.sql.*;

/**
 * DAO de Administrador — autenticação na tabela `administradores`.
 */
public class AdminDAO {

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public Administrador login(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, email, data_cadastro, ativo "
                   + "FROM administradores "
                   + "WHERE email = ? AND senha_hash = SHA2(?, 256) AND ativo = TRUE";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Administrador a = new Administrador();
                    a.setId(rs.getInt("id"));
                    a.setNome(rs.getString("nome"));
                    a.setEmail(rs.getString("email"));
                    a.setDataCadastro(rs.getTimestamp("data_cadastro"));
                    a.setAtivo(rs.getBoolean("ativo"));
                    return a;
                }
                return null;
            }
        }
    }
}