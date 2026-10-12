package com.clinica.servlet;

import com.clinica.dao.AdminDAO;
import com.clinica.model.Administrador;
import com.clinica.util.RespostaUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet de Administrador.
 *
 * POST /api/admin/login  → autenticar administrador
 */
public class AdminServlet extends HttpServlet {

    private final AdminDAO dao = new AdminDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RespostaUtil.ok(resp, null);
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /login

        if ("/login".equals(pathInfo)) {
            try {
                String body = lerBody(req);
                JsonObject json = RespostaUtil.GSON.fromJson(body, JsonObject.class);

                String email = json.get("email").getAsString();
                String senha = json.get("senha").getAsString();

                Administrador admin = dao.login(email, senha);
                if (admin != null) {
                    RespostaUtil.ok(resp, admin);
                } else {
                    RespostaUtil.erro(resp, 401, "E-mail ou senha incorretos.");
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