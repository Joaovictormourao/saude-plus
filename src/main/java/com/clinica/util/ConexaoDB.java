package com.clinica.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    
    private static final String URL = obterConfiguracao("db.url", "DB_URL",
            "jdbc:mysql://localhost:3306/clinica_saude_plus");
    private static final String USUARIO = obterConfiguracao("db.user", "DB_USER", "root");
    private static final String SENHA = obterConfiguracao("db.password", "DB_PASSWORD", "");
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar driver JDBC: " + e.getMessage());
        }
    }

    public static Connection getConexao() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Compatibilidade com DAOs existentes (chamavam obterConexao()).
     */
    public static Connection obterConexao() throws SQLException {
        return getConexao();
    }


    public static void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    private static String obterConfiguracao(String propriedade, String variavelAmbiente, String valorPadrao) {
        String valor = System.getProperty(propriedade);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variavelAmbiente);
        }
        return (valor == null || valor.isBlank()) ? valorPadrao : valor;
    }
}
