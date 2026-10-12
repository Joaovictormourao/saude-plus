package com.clinica.servlet;

import com.clinica.dao.EspecialidadeDAO;
import com.clinica.model.Especialidade;
import com.clinica.util.RespostaUtil;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de Especialidades.
 *
 * GET    /api/especialidades           → listar todas
 * GET    /api/especialidades?id={id}   → buscar por ID
 * POST   /api/especialidades           → inserir
 * PUT    /api/especialidades           → atualizar
 * DELETE /api/especialidades?id={id}   → excluir (soft delete)
 */
public class EspecialidadeServlet extends HttpServlet {

    private final EspecialidadeDAO dao = new EspecialidadeDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RespostaUtil.ok(resp, null);
    }

    // ── GET ──────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");

            if (idParam != null) {
                Especialidade e = dao.buscarPorId(Integer.parseInt(idParam));
                if (e != null) {
                    RespostaUtil.ok(resp, e);
                } else {
                    RespostaUtil.erro(resp, 404, "Especialidade não encontrada.");
                }
            } else {
                List<Especialidade> lista = dao.listarTodas();
                RespostaUtil.ok(resp, lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao consultar especialidades.");
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Especialidade e = RespostaUtil.GSON.fromJson(lerBody(req), Especialidade.class);

            if (e.getNome() == null || e.getNome().isBlank()) {
                RespostaUtil.erro(resp, 400, "Nome da especialidade é obrigatório.");
                return;
            }

            boolean ok = dao.inserir(e);
            if (ok) {
                RespostaUtil.criado(resp, new OkJson("Especialidade cadastrada com sucesso!"));
            } else {
                RespostaUtil.erro(resp, 500, "Não foi possível cadastrar a especialidade.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao salvar especialidade.");
        }
    }

    // ── PUT ──────────────────────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Especialidade e = RespostaUtil.GSON.fromJson(lerBody(req), Especialidade.class);

            if (e.getId() == 0) {
                RespostaUtil.erro(resp, 400, "ID é obrigatório para atualização.");
                return;
            }

            boolean ok = dao.atualizar(e);
            if (ok) {
                RespostaUtil.ok(resp, new OkJson("Especialidade atualizada com sucesso!"));
            } else {
                RespostaUtil.erro(resp, 404, "Especialidade não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao atualizar especialidade.");
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

            boolean ok = dao.excluir(Integer.parseInt(idParam));
            if (ok) {
                RespostaUtil.ok(resp, new OkJson("Especialidade excluída com sucesso."));
            } else {
                RespostaUtil.erro(resp, 404, "Especialidade não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao excluir especialidade.");
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