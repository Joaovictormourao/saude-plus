package com.clinica.servlet;

import com.clinica.dao.PacienteDAO;
import com.clinica.model.Paciente;
import com.clinica.util.RespostaUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de Pacientes.
 *
 * GET    /api/pacientes                  → listar todos
 * GET    /api/pacientes?id={id}          → buscar por ID
 * POST   /api/pacientes/login            → autenticar
 * POST   /api/pacientes/cadastrar        → novo paciente
 * POST   /api/pacientes/redefinir-senha  → redefinir senha (esqueci minha senha)
 * PUT    /api/pacientes                  → atualizar
 * DELETE /api/pacientes?id={id}          → excluir (soft delete)
 */
public class PacienteServlet extends HttpServlet {

    private final PacienteDAO dao = new PacienteDAO();

    private static String apenasDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D+", "");
    }

    private static boolean validarCpf(String cpf) {
        String num = apenasDigitos(cpf);
        if (num.length() != 11) return false;

        // Rejeita CPFs com todos os dígitos iguais
        if (num.matches("^(\\d)\\1{10}$")) return false;

        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Character.getNumericValue(num.charAt(i));
        }

        // Primeiro dígito verificador
        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += digits[i] * (10 - i);
        }
        int resto1 = (soma1 * 10) % 11;
        int digito1 = (resto1 == 10) ? 0 : resto1;

        // Segundo dígito verificador
        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += digits[i] * (11 - i);
        }
        int resto2 = (soma2 * 10) % 11;
        int digito2 = (resto2 == 10) ? 0 : resto2;

        return digits[9] == digito1 && digits[10] == digito2;
    }

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
                Paciente p = dao.buscarPorId(Integer.parseInt(idParam));
                if (p != null) {
                    RespostaUtil.ok(resp, p);
                } else {
                    RespostaUtil.erro(resp, 404, "Paciente não encontrado.");
                }
            } else {
                List<Paciente> lista = dao.listarTodos();
                RespostaUtil.ok(resp, lista);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao consultar banco de dados.");
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /login  ou  /cadastrar
        String body     = lerBody(req);

        try {
            if ("/login".equals(pathInfo)) {
                // ── Login
                JsonObject json = RespostaUtil.GSON.fromJson(body, JsonObject.class);
                String email = json.get("email").getAsString();
                String senha = json.get("senha").getAsString();

                Paciente p = dao.login(email, senha);
                if (p != null) {
                    RespostaUtil.ok(resp, p);
                } else {
                    RespostaUtil.erro(resp, 401, "E-mail ou senha inválidos.");
                }

            } else if ("/redefinir-senha".equals(pathInfo)) {
                // ── Redefinição de senha (esqueci minha senha)
                JsonObject json = RespostaUtil.GSON.fromJson(body, JsonObject.class);

                String email = (json.has("email") && !json.get("email").isJsonNull())
                        ? json.get("email").getAsString() : null;
                String novaSenha = (json.has("novaSenha") && !json.get("novaSenha").isJsonNull())
                        ? json.get("novaSenha").getAsString() : null;

                if (email == null || email.isBlank() || novaSenha == null || novaSenha.isBlank()) {
                    RespostaUtil.erro(resp, 400, "Informe e-mail e nova senha.");
                    return;
                }

                if (novaSenha.length() < 6) {
                    RespostaUtil.erro(resp, 400, "A nova senha deve ter no mínimo 6 caracteres.");
                    return;
                }

                boolean ok = dao.redefinirSenha(email, novaSenha);
                if (ok) {
                    RespostaUtil.ok(resp, new OkJson("Senha redefinida com sucesso!"));
                } else {
                    RespostaUtil.erro(resp, 404, "Nenhum paciente ativo encontrado com este e-mail.");
                }

            } else if ("/cadastrar".equals(pathInfo) || pathInfo == null) {
                // ── Cadastro
                Paciente p = RespostaUtil.GSON.fromJson(body, Paciente.class);

                if (p.getNome() == null || p.getEmail() == null || p.getSenha() == null) {
                    RespostaUtil.erro(resp, 400, "Campos obrigatórios: nome, email, senha.");
                    return;
                }

                // Validação de CPF antes de chamar o DAO
                String cpf = p.getCpf();
                if (!validarCpf(cpf)) {
                    RespostaUtil.erro(resp, 400, "CPF inválido.");
                    return;
                }

                // Normaliza CPF para apenas dígitos (mantém consistência no banco)
                p.setCpf(apenasDigitos(cpf));

                boolean ok = dao.cadastrar(p);
                if (ok) {
                    RespostaUtil.criado(resp, new OkJson("Cadastro realizado com sucesso!"));
                } else {
                    RespostaUtil.erro(resp, 500, "Não foi possível realizar o cadastro.");
                }

            } else {
                RespostaUtil.erro(resp, 404, "Endpoint não encontrado.");
            }


        } catch (com.google.gson.JsonSyntaxException e) {
            RespostaUtil.erro(resp, 400, "JSON inválido.");
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg != null && msg.contains("Duplicate entry")) {
                RespostaUtil.erro(resp, 409, "E-mail ou CPF já cadastrado.");
            } else {
                RespostaUtil.erro(resp, 500, "Erro ao salvar no banco de dados.");
            }
        }
    }

    // ── PUT ──────────────────────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Paciente p = RespostaUtil.GSON.fromJson(lerBody(req), Paciente.class);

            if (p.getId() == 0) {
                RespostaUtil.erro(resp, 400, "ID do paciente é obrigatório.");
                return;
            }

            boolean ok = dao.atualizar(p);
            if (ok) {
                RespostaUtil.ok(resp, new OkJson("Paciente atualizado com sucesso!"));
            } else {
                RespostaUtil.erro(resp, 404, "Paciente não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao atualizar paciente.");
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
                RespostaUtil.ok(resp, new OkJson("Paciente excluído com sucesso."));
            } else {
                RespostaUtil.erro(resp, 404, "Paciente não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            RespostaUtil.erro(resp, 500, "Erro ao excluir paciente.");
        }
    }

    // ── Utilitários ───────────────────────────────────────────────────────────
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