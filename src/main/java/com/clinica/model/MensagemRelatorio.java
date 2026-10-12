package com.clinica.model;

import java.sql.Timestamp;

/**
 * Modelo de Mensagem/Relatório Médico — espelha a tabela `mensagens_relatorio`.
 */
public class MensagemRelatorio {

    private int       id;
    private int       agendamentoId;
    private int       medicoId;
    private int       pacienteId;
    private String    tipo;
    private String    conteudo;
    private Timestamp dataEnvio;
    private boolean   lida;

    // Extras (JOIN)
    private String    nomeMedico;
    private String    nomePaciente;

    public MensagemRelatorio() {}

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getAgendamentoId()            { return agendamentoId; }
    public void setAgendamentoId(int a)      { this.agendamentoId = a; }

    public int getMedicoId()                 { return medicoId; }
    public void setMedicoId(int m)           { this.medicoId = m; }

    public int getPacienteId()               { return pacienteId; }
    public void setPacienteId(int p)         { this.pacienteId = p; }

    public String getTipo()                  { return tipo; }
    public void setTipo(String tipo)         { this.tipo = tipo; }

    public String getConteudo()              { return conteudo; }
    public void setConteudo(String c)        { this.conteudo = c; }

    public Timestamp getDataEnvio()          { return dataEnvio; }
    public void setDataEnvio(Timestamp t)    { this.dataEnvio = t; }

    public boolean isLida()                  { return lida; }
    public void setLida(boolean lida)        { this.lida = lida; }

    public String getNomeMedico()            { return nomeMedico; }
    public void setNomeMedico(String n)      { this.nomeMedico = n; }

    public String getNomePaciente()          { return nomePaciente; }
    public void setNomePaciente(String n)    { this.nomePaciente = n; }
}