package com.clinica.dao;

import com.clinica.model.MensagemContato;
import com.clinica.model.MensagemRelatorio;
import com.clinica.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Mensagens — tabelas `mensagens_contato` e `mensagens_relatorio`.
 */
public class MensagemDAO {

    // ════════════════════════════════════════════════════════════════
    //  MENSAGENS DE CONTATO
    // ════════════════════════════════════════════════════════════════

    public boolean salvarContato(MensagemContato mc) throws SQLException {
        String sql = "INSERT INTO mensagens_contato (nome, email, telefone, assunto, mensagem) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mc.getNome());
            ps.setString(2, mc.getEmail());
            ps.setString(3, mc.getTelefone());
            ps.setString(4, mc.getAssunto());
            ps.setString(5, mc.getMensagem());
            return ps.executeUpdate() > 0;
        }
    }

    public List<MensagemContato> listarContatos() throws SQLException {
        List<MensagemContato> lista = new ArrayList<>();
        String sql = "SELECT * FROM mensagens_contato ORDER BY data_envio DESC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MensagemContato mc = new MensagemContato();
                mc.setId(rs.getInt("id"));
                mc.setNome(rs.getString("nome"));
                mc.setEmail(rs.getString("email"));
                mc.setTelefone(rs.getString("telefone"));
                mc.setAssunto(rs.getString("assunto"));
                mc.setMensagem(rs.getString("mensagem"));
                mc.setDataEnvio(rs.getTimestamp("data_envio"));
                mc.setLida(rs.getBoolean("lida"));
                lista.add(mc);
            }
        }
        return lista;
    }

    public boolean marcarContatoComoLido(int id) throws SQLException {
        String sql = "UPDATE mensagens_contato SET lida = TRUE WHERE id = ?";
        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  RELATÓRIOS / MENSAGENS MÉDICAS
    // ════════════════════════════════════════════════════════════════

    public boolean salvarRelatorio(MensagemRelatorio mr) throws SQLException {
        String sql = "INSERT INTO mensagens_relatorio "
                   + "(agendamento_id, medico_id, paciente_id, tipo, conteudo) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, mr.getAgendamentoId());
            ps.setInt(2, mr.getMedicoId());
            ps.setInt(3, mr.getPacienteId());
            ps.setString(4, mr.getTipo() != null ? mr.getTipo() : "relatorio");
            ps.setString(5, mr.getConteudo());
            return ps.executeUpdate() > 0;
        }
    }

    public List<MensagemRelatorio> listarPorPaciente(int pacienteId) throws SQLException {
        List<MensagemRelatorio> lista = new ArrayList<>();
        String sql = "SELECT mr.*, m.nome AS nome_medico, p.nome AS nome_paciente "
                   + "FROM mensagens_relatorio mr "
                   + "JOIN medicos m ON mr.medico_id = m.id "
                   + "JOIN pacientes p ON mr.paciente_id = p.id "
                   + "WHERE mr.paciente_id = ? ORDER BY mr.data_envio DESC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearRelatorio(rs));
            }
        }
        return lista;
    }

    public List<MensagemRelatorio> listarPorMedico(int medicoId) throws SQLException {
        List<MensagemRelatorio> lista = new ArrayList<>();
        String sql = "SELECT mr.*, m.nome AS nome_medico, p.nome AS nome_paciente "
                   + "FROM mensagens_relatorio mr "
                   + "JOIN medicos m ON mr.medico_id = m.id "
                   + "JOIN pacientes p ON mr.paciente_id = p.id "
                   + "WHERE mr.medico_id = ? ORDER BY mr.data_envio DESC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearRelatorio(rs));
            }
        }
        return lista;
    }

    public boolean marcarRelatorioComoLido(int id) throws SQLException {
        String sql = "UPDATE mensagens_relatorio SET lida = TRUE WHERE id = ?";
        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MAPEAMENTO ────────────────────────────────────────────────────────────
    private MensagemRelatorio mapearRelatorio(ResultSet rs) throws SQLException {
        MensagemRelatorio mr = new MensagemRelatorio();
        mr.setId(rs.getInt("id"));
        mr.setAgendamentoId(rs.getInt("agendamento_id"));
        mr.setMedicoId(rs.getInt("medico_id"));
        mr.setPacienteId(rs.getInt("paciente_id"));
        mr.setTipo(rs.getString("tipo"));
        mr.setConteudo(rs.getString("conteudo"));
        mr.setDataEnvio(rs.getTimestamp("data_envio"));
        mr.setLida(rs.getBoolean("lida"));
        mr.setNomeMedico(rs.getString("nome_medico"));
        mr.setNomePaciente(rs.getString("nome_paciente"));
        return mr;
    }
}