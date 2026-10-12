package com.clinica.servlet;

import com.clinica.dao.MensagemDAO;
import com.clinica.model.MensagemContato;
import com.clinica.model.MensagemRelatorio;
import com.clinica.util.RespostaUtil;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de Mensagens.
 *
 * ── Contato ──────────────────────────────────────────────────────────────────
 * POST   /api/mensagens/contato             → salvar mensagem de contato
 * GET    /api/mensagens/contato             → listar mensagens (admin)
 * PUT    /api/mensagens/contato?id={id}     → marcar como lida
 *
 * ── Relatórios / Médico ───────────────────────────────────────────────────────
 * POST   /api/mensagens/relatorio           → médico envia relatório
 * GET    /api/mensagens/relatorio?paciente={id}  → relatórios do paciente
 * GET    /api/mensagens/relatorio?medico={id}    → relatórios enviados pelo médico
 * PUT    /api/mensagens/relatorio?id={id}        → marcar como lido
 */
public class MensagemServlet extends HttpServlet {

    private final MensagemDAO dao = new MensagemDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RespostaUtil.ok(resp, null);
    }

    // ── GET ──────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /contato  ou  /relatorio

        try {
            if ("/contato".equals(pathInfo)) {
                List<MensagemContato> lista = dao.listarContatos();
                RespostaUtil.ok(resp, lista);

            } else if ("/relatorio".equals(pathInfo)) {
                String pacienteParam = req.getParameter("paciente");
                String medicoParam   = req.getParameter("medico");

                if (pacienteParam != null) {
                    List<MensagemRelatorio> lista = dao.listarPorPaciente(Integer.parseInt(pacienteParam));
                    RespostaUtil.ok(resp, lista);

                } else if (medicoParam != null) {
                    List<MensagemRelatorio> lista = dao.listarPorMedico(Integer.parseInt(medicoParam));
                    RespostaUtil.ok(resp, lista);

                } else {
                    RespostaUtil.erro(resp, 400, "Informe ?paciente={id} ou ?medico={id}.");
                }
            } else {
                RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao consultar mensagens.");
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String body     = lerBody(req);

        try {
            if ("/contato".equals(pathInfo)) {
                MensagemContato mc = RespostaUtil.GSON.fromJson(body, MensagemContato.class);

                if (mc.getNome() == null || mc.getEmail() == null || mc.getMensagem() == null) {
                    RespostaUtil.erro(resp, 400, "Campos obrigatórios: nome, email, mensagem.");
                    return;
                }

                boolean ok = dao.salvarContato(mc);
                if (ok) {
                    RespostaUtil.criado(resp, new OkJson("Mensagem enviada com sucesso!"));
                } else {
                    RespostaUtil.erro(resp, 500, "Não foi possível salvar a mensagem.");
                }

            } else if ("/relatorio".equals(pathInfo)) {
                MensagemRelatorio mr = RespostaUtil.GSON.fromJson(body, MensagemRelatorio.class);

                if (mr.getMedicoId() == 0 || mr.getPacienteId() == 0 || mr.getConteudo() == null) {
                    RespostaUtil.erro(resp, 400, "Campos obrigatórios: medicoId, pacienteId, conteudo.");
                    return;
                }

                boolean ok = dao.salvarRelatorio(mr);
                if (ok) {
                    RespostaUtil.criado(resp, new OkJson("Relatório enviado com sucesso!"));
                } else {
                    RespostaUtil.erro(resp, 500, "Não foi possível salvar o relatório.");
                }
            } else {
                RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao salvar mensagem.");
        }
    }

    // ── PUT ── marcar como lida ───────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String idParam  = req.getParameter("id");

        if (idParam == null) {
            RespostaUtil.erro(resp, 400, "Parâmetro 'id' é obrigatório.");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            boolean ok;

            if ("/contato".equals(pathInfo)) {
                ok = dao.marcarContatoComoLido(id);
            } else if ("/relatorio".equals(pathInfo)) {
                ok = dao.marcarRelatorioComoLido(id);
            } else {
                RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
                return;
            }

            if (ok) {
                RespostaUtil.ok(resp, new OkJson("Marcado como lido."));
            } else {
                RespostaUtil.erro(resp, 404, "Mensagem não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao atualizar mensagem.");
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