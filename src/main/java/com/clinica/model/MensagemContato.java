package com.clinica.model;

import java.sql.Timestamp;

/**
 * Modelo de Mensagem de Contato — espelha a tabela `mensagens_contato`.
 */
public class MensagemContato {

    private int       id;
    private String    nome;
    private String    email;
    private String    telefone;
    private String    assunto;
    private String    mensagem;
    private Timestamp dataEnvio;
    private boolean   lida;

    public MensagemContato() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNome()                 { return nome; }
    public void setNome(String nome)        { this.nome = nome; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getTelefone()             { return telefone; }
    public void setTelefone(String t)       { this.telefone = t; }

    public String getAssunto()              { return assunto; }
    public void setAssunto(String a)        { this.assunto = a; }

    public String getMensagem()             { return mensagem; }
    public void setMensagem(String m)       { this.mensagem = m; }

    public Timestamp getDataEnvio()         { return dataEnvio; }
    public void setDataEnvio(Timestamp t)   { this.dataEnvio = t; }

    public boolean isLida()                 { return lida; }
    public void setLida(boolean lida)       { this.lida = lida; }
}