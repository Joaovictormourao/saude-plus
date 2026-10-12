package com.clinica.servlet;

import com.clinica.dao.MedicoDAO;
import com.clinica.model.Medico;
import com.clinica.util.RespostaUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de Médicos.
 *
 * GET  /api/medicos                          → listar todos
 * GET  /api/medicos?especialidade={id}       → filtrar por especialidade
 * GET  /api/medicos?id={id}                  → buscar por ID
 * POST /api/medicos/login                    → autenticar médico
 */
public class MedicoServlet extends HttpServlet {

    private final MedicoDAO dao = new MedicoDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RespostaUtil.ok(resp, null);
    }

    // ── GET ──────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam           = req.getParameter("id");
            String especialidadeParam = req.getParameter("especialidade");

            if (idParam != null) {
                Medico m = dao.buscarPorId(Integer.parseInt(idParam));
                if (m != null) {
                    RespostaUtil.ok(resp, m);
                } else {
                    RespostaUtil.erro(resp, 404, "Médico não encontrado.");
                }

            } else if (especialidadeParam != null) {
                List<Medico> lista = dao.listarPorEspecialidade(Integer.parseInt(especialidadeParam));
                RespostaUtil.ok(resp, lista);

            } else {
                List<Medico> lista = dao.listarTodos();
                RespostaUtil.ok(resp, lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao consultar médicos.");
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /login

        if ("/login".equals(pathInfo)) {
            try {
                String body   = lerBody(req);
                JsonObject json = RespostaUtil.GSON.fromJson(body, JsonObject.class);

                String email = json.get("email").getAsString();
                String senha = json.get("senha").getAsString();

                Medico m = dao.login(email, senha);
                if (m != null) {
                    RespostaUtil.ok(resp, m);
                } else {
                    RespostaUtil.erro(resp, 401, "E-mail ou senha inválidos.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                RespostaUtil.erro(resp, 500, "Erro interno no servidor.");
            }
        } else {
            RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
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
}