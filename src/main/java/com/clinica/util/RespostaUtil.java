package com.clinica.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utilitário para enviar respostas JSON padronizadas nos Servlets.
 */
public class RespostaUtil {

    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .serializeNulls()
            .create();

    /**
     * Configura o response para JSON e retorna o objeto serializado.
     */
    public static void enviarJson(HttpServletResponse resp, int status, Object objeto)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = resp.getWriter();
        out.print(GSON.toJson(objeto));
        out.flush();
    }

    /** Atalho para 200 OK */
    public static void ok(HttpServletResponse resp, Object objeto) throws IOException {
        enviarJson(resp, 200, objeto);
    }

    /** Atalho para 201 Created */
    public static void criado(HttpServletResponse resp, Object objeto) throws IOException {
        enviarJson(resp, 201, objeto);
    }

    /** Atalho para erros (4xx / 5xx) */
    public static void erro(HttpServletResponse resp, int status, String mensagem)
            throws IOException {
        enviarJson(resp, status, new ErroJson(mensagem));
    }

    // Objeto interno para padronizar erro
    private static class ErroJson {
        String erro;
        ErroJson(String m) { this.erro = m; }
    }
}