package com.clinica.model;

import java.sql.Timestamp;

/**
 * Modelo de Agendamento — espelha a tabela `agendamentos`.
 * Os campos `especialidade` e `medico` são preenchidos via JOIN nas queries.
 */
public class Agendamento {

    private int       id;
    private int       pacienteId;
    private Integer   medicoId;
    private int       especialidadeId;
    private String    data;          // yyyy-MM-dd
    private String    hora;          // HH:mm
    private String    descricao;
    private String    status;
    private Timestamp dataAgendamento;

    // Campos extras vindos de JOIN
    private String    paciente;
    private String    medico;
    private String    especialidade;

    public Agendamento() {}

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getPacienteId()               { return pacienteId; }
    public void setPacienteId(int p)         { this.pacienteId = p; }

    public Integer getMedicoId()             { return medicoId; }
    public void setMedicoId(Integer m)       { this.medicoId = m; }

    public int getEspecialidadeId()          { return especialidadeId; }
    public void setEspecialidadeId(int e)    { this.especialidadeId = e; }

    public String getData()                  { return data; }
    public void setData(String data)         { this.data = data; }

    public String getHora()                  { return hora; }
    public void setHora(String hora)         { this.hora = hora; }

    public String getDescricao()             { return descricao; }
    public void setDescricao(String d)       { this.descricao = d; }

    public String getStatus()                { return status; }
    public void setStatus(String status)     { this.status = status; }

    public Timestamp getDataAgendamento()    { return dataAgendamento; }
    public void setDataAgendamento(Timestamp t){ this.dataAgendamento = t; }

    public String getPaciente()              { return paciente; }
    public void setPaciente(String p)        { this.paciente = p; }

    public String getMedico()                { return medico; }
    public void setMedico(String m)          { this.medico = m; }

    public String getEspecialidade()         { return especialidade; }
    public void setEspecialidade(String e)   { this.especialidade = e; }
}