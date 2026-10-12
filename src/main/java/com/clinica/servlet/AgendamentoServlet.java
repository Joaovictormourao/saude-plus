package com.clinica.servlet;

import com.clinica.dao.AgendamentoDAO;
import com.clinica.model.Agendamento;
import com.clinica.util.RespostaUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de Agendamentos.
 *
 * GET    /api/agendamentos                     → listar todos
 * GET    /api/agendamentos?paciente={id}        → por paciente
 * GET    /api/agendamentos?medico={id}          → por médico
 * GET    /api/agendamentos?id={id}              → por ID
 * POST   /api/agendamentos                     → criar agendamento
 * PUT    /api/agendamentos/status              → atualizar status
 * DELETE /api/agendamentos?id={id}             → cancelar (status = Cancelado)
 */
public class AgendamentoServlet extends HttpServlet {

    private final AgendamentoDAO dao = new AgendamentoDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RespostaUtil.ok(resp, null);
    }

    // ── GET ──────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam       = req.getParameter("id");
            String pacienteParam = req.getParameter("paciente");
            String medicoParam   = req.getParameter("medico");

            if (idParam != null) {
                Agendamento ag = dao.buscarPorId(Integer.parseInt(idParam));
                if (ag != null) {
                    RespostaUtil.ok(resp, ag);
                } else {
                    RespostaUtil.erro(resp, 404, "Agendamento não encontrado.");
                }

            } else if (pacienteParam != null) {
                List<Agendamento> lista = dao.listarPorPaciente(Integer.parseInt(pacienteParam));
                RespostaUtil.ok(resp, lista);

            } else if (medicoParam != null) {
                List<Agendamento> lista = dao.listarPorMedico(Integer.parseInt(medicoParam));
                RespostaUtil.ok(resp, lista);

            } else {
                List<Agendamento> lista = dao.listarTodos();
                RespostaUtil.ok(resp, lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao consultar agendamentos.");
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Agendamento ag = RespostaUtil.GSON.fromJson(lerBody(req), Agendamento.class);

            if (ag.getPacienteId() == 0 || ag.getEspecialidadeId() == 0
                    || ag.getData() == null || ag.getHora() == null) {
                RespostaUtil.erro(resp, 400,
                        "Campos obrigatórios: pacienteId, especialidadeId, data, hora.");
                return;
            }

            boolean ok = dao.inserir(ag);
            if (ok) {
                RespostaUtil.criado(resp, new OkJson("Consulta agendada com sucesso!"));
            } else {
                // Caso o slot já exista para o mesmo médico no mesmo dia/hora
                RespostaUtil.erro(resp, 409, "Já existe uma consulta para este médico neste dia e horário.");
            }



        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao salvar agendamento.");
        }
    }

    // ── PUT ──────────────────────────────────────────────────────────────────
    // Usado pelo painel médico para confirmar/concluir consultas
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /status

        try {
            if ("/status".equals(pathInfo)) {
                JsonObject json   = RespostaUtil.GSON.fromJson(lerBody(req), JsonObject.class);
                int    id     = json.get("id").getAsInt();
                String status = json.get("status").getAsString();

                boolean ok = dao.atualizarStatus(id, status);
                if (ok) {
                    RespostaUtil.ok(resp, new OkJson("Status atualizado para: " + status));
                } else {
                    RespostaUtil.erro(resp, 404, "Agendamento não encontrado.");
                }
            } else {
                RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao atualizar status.");
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null) {
                RespostaUtil.erro(resp, 400, "Parâmetro 'id' é obrigatório.");
                return;
            }

            boolean ok = dao.cancelar(Integer.parseInt(idParam));
            if (ok) {
                RespostaUtil.ok(resp, new OkJson("Consulta cancelada com sucesso."));
            } else {
                RespostaUtil.erro(resp, 404, "Agendamento não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao cancelar agendamento.");
        }
    }

    private String lerBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    private static class OkJson {
        String mensagem;
        OkJson(String m) { this.mensagem = m; }
    }
}