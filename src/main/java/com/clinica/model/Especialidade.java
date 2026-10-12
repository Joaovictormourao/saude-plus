package com.clinica.model;

import java.sql.Timestamp;

/**
 * Modelo de Especialidade — espelha a tabela `especialidades`.
 */
public class Especialidade {

    private int       id;
    private String    nome;
    private String    descricao;
    private String    icone;
    private boolean   ativo;
    private Timestamp dataCriacao;

    public Especialidade() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNome()                 { return nome; }
    public void setNome(String nome)        { this.nome = nome; }

    public String getDescricao()            { return descricao; }
    public void setDescricao(String d)      { this.descricao = d; }

    public String getIcone()                { return icone; }
    public void setIcone(String icone)      { this.icone = icone; }

    public boolean isAtivo()                { return ativo; }
    public void setAtivo(boolean ativo)     { this.ativo = ativo; }

    public Timestamp getDataCriacao()       { return dataCriacao; }
    public void setDataCriacao(Timestamp t) { this.dataCriacao = t; }
}