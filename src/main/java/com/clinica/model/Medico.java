package com.clinica.model;

import java.sql.Timestamp;

/**
 * Modelo de Médico — espelha a tabela `medicos`.
 */
public class Medico {

    private int       id;
    private String    nome;
    private int       especialidadeId;
    private String    especialidade;   // campo extra: nome da especialidade (JOIN)
    private String    crm;
    private String    email;
    private String    telefone;
    private String    senha;
    private Timestamp dataCadastro;
    private boolean   ativo;

    public Medico() {}

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getNome()                  { return nome; }
    public void setNome(String nome)         { this.nome = nome; }

    public int getEspecialidadeId()          { return especialidadeId; }
    public void setEspecialidadeId(int e)    { this.especialidadeId = e; }

    public String getEspecialidade()         { return especialidade; }
    public void setEspecialidade(String e)   { this.especialidade = e; }

    public String getCrm()                   { return crm; }
    public void setCrm(String crm)           { this.crm = crm; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getTelefone()              { return telefone; }
    public void setTelefone(String t)        { this.telefone = t; }

    public String getSenha()                 { return senha; }
    public void setSenha(String senha)       { this.senha = senha; }

    public Timestamp getDataCadastro()       { return dataCadastro; }
    public void setDataCadastro(Timestamp t) { this.dataCadastro = t; }

    public boolean isAtivo()                 { return ativo; }
    public void setAtivo(boolean ativo)      { this.ativo = ativo; }
}