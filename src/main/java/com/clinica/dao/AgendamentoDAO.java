package com.clinica.dao;

import com.clinica.model.Agendamento;
import com.clinica.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Agendamento — CRUD na tabela `agendamentos`.
 */
public class AgendamentoDAO {

    // ── LISTAR TODOS ──────────────────────────────────────────────────────────
    public List<Agendamento> listarTodos() throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = buildSelectBase() + " ORDER BY a.data DESC, a.hora DESC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── LISTAR POR PACIENTE ───────────────────────────────────────────────────
    public List<Agendamento> listarPorPaciente(int pacienteId) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = buildSelectBase() + " WHERE a.paciente_id = ? ORDER BY a.data DESC, a.hora DESC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── LISTAR POR MÉDICO ─────────────────────────────────────────────────────
    public List<Agendamento> listarPorMedico(int medicoId) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = buildSelectBase() + " WHERE a.medico_id = ? ORDER BY a.data ASC, a.hora ASC";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────────────────────
    public Agendamento buscarPorId(int id) throws SQLException {
        String sql = buildSelectBase() + " WHERE a.id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── INSERIR ───────────────────────────────────────────────────────────────
    public boolean inserir(Agendamento ag) throws SQLException {
        // Validação de consistência (mesmo dia + mesmo médico + mesma hora)
        // para impedir que dois usuários agendem o mesmo slot.
        if (ag.getMedicoId() == null) {
            throw new SQLException("medico_id é obrigatório para agendar.");
        }

        if (slotOcupado(ag.getMedicoId(), ag.getData(), ag.getHora())) {
            return false;
        }

        String sql = "INSERT INTO agendamentos "
                   + "(paciente_id, medico_id, especialidade_id, data, hora, descricao, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'Agendado')";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ag.getPacienteId());
            ps.setInt(2, ag.getMedicoId());
            ps.setInt(3, ag.getEspecialidadeId());
            ps.setDate(4, Date.valueOf(ag.getData()));
            ps.setTime(5, Time.valueOf(ag.getHora() + ":00"));
            ps.setString(6, ag.getDescricao() != null ? ag.getDescricao() : "Agendamento via portal");

            return ps.executeUpdate() > 0;
        }
    }

    private boolean slotOcupado(Integer medicoId, String dataISO, String horaHHmm) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM agendamentos "
                   + "WHERE medico_id = ? AND data = ? AND hora = ? AND status IN ('Agendado','Confirmado')";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, medicoId);
            ps.setDate(2, Date.valueOf(dataISO));
            ps.setTime(3, Time.valueOf(horaHHmm + ":00"));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    return total > 0;
                }
            }
        }
        return false;
    }


    // ── CANCELAR (atualizar status) ───────────────────────────────────────────
    public boolean cancelar(int id) throws SQLException {
        String sql = "UPDATE agendamentos SET status = 'Cancelado' WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── ATUALIZAR STATUS (médico confirma/conclui) ────────────────────────────
    public boolean atualizarStatus(int id, String status) throws SQLException {
        String sql = "UPDATE agendamentos SET status = ? WHERE id = ?";

        try (Connection con = ConexaoDB.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT BASE com JOINs ──────────────────────────────────────────────────
    private String buildSelectBase() {
        return "SELECT a.id, a.paciente_id, a.medico_id, a.especialidade_id, "
             + "a.data, a.hora, a.descricao, a.status, a.data_agendamento, "
             + "p.nome AS paciente, "
             + "IFNULL(m.nome, 'A definir') AS medico, "
             + "e.nome AS especialidade "
             + "FROM agendamentos a "
             + "JOIN pacientes p ON a.paciente_id = p.id "
             + "LEFT JOIN medicos m ON a.medico_id = m.id "
             + "JOIN especialidades e ON a.especialidade_id = e.id";
    }

    // ── MAPEAMENTO ────────────────────────────────────────────────────────────
    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("id"));
        a.setPacienteId(rs.getInt("paciente_id"));

        int medicoId = rs.getInt("medico_id");
        a.setMedicoId(rs.wasNull() ? null : medicoId);

        a.setEspecialidadeId(rs.getInt("especialidade_id"));

        Date d = rs.getDate("data");
        a.setData(d != null ? d.toString() : null);

        Time h = rs.getTime("hora");
        a.setHora(h != null ? h.toString().substring(0, 5) : null); // HH:mm

        a.setDescricao(rs.getString("descricao"));
        a.setStatus(rs.getString("status"));
        a.setDataAgendamento(rs.getTimestamp("data_agendamento"));
        a.setPaciente(rs.getString("paciente"));
        a.setMedico(rs.getString("medico"));
        a.setEspecialidade(rs.getString("especialidade"));
        return a;
    }
}