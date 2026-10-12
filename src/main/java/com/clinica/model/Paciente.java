package com.clinica.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Modelo de Paciente — espelha a tabela `pacientes`.
 */
public class Paciente {

    private int       id;
    private String    nome;
    private String    cpf;
    private String    email;
    private String    telefone;
    private String    dataNascimento;   // yyyy-MM-dd (String para compatibilidade JSON/JS)
    private String    sexo;
    private String    senha;            // somente para entrada; nunca devolvido ao frontend
    private Timestamp dataCadastro;
    private boolean   ativo;

    // ── Construtores ──────────────────────────────────────────────────────────

    public Paciente() {}

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }

    public String getNome()                { return nome; }
    public void setNome(String nome)       { this.nome = nome; }

    public String getCpf()                 { return cpf; }
    public void setCpf(String cpf)         { this.cpf = cpf; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getTelefone()            { return telefone; }
    public void setTelefone(String t)      { this.telefone = t; }

    public String getDataNascimento()      { return dataNascimento; }
    public void setDataNascimento(String d){ this.dataNascimento = d; }

    public String getSexo()                { return sexo; }
    public void setSexo(String sexo)       { this.sexo = sexo; }

    public String getSenha()               { return senha; }
    public void setSenha(String senha)     { this.senha = senha; }

    public Timestamp getDataCadastro()     { return dataCadastro; }
    public void setDataCadastro(Timestamp t){ this.dataCadastro = t; }

    public boolean isAtivo()               { return ativo; }
    public void setAtivo(boolean ativo)    { this.ativo = ativo; }
}