-- ============================================================
-- CLÍNICA SAÚDE+
-- Banco de Dados MySQL 8+
-- Compatível com Java + Tomcat + JDBC
-- ============================================================

DROP DATABASE IF EXISTS clinica_saude_plus;

CREATE DATABASE clinica_saude_plus
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE clinica_saude_plus;

-- ============================================================
-- TABELA PACIENTES
-- ============================================================

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(20) NOT NULL,
    senha_hash VARCHAR(64) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- TABELA ADMINISTRADORES
-- ============================================================

CREATE TABLE administradores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(64) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- TABELA ESPECIALIDADES
-- ============================================================

CREATE TABLE especialidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    icone VARCHAR(10),
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA MÉDICOS
-- ============================================================

CREATE TABLE medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    especialidade_id INT NOT NULL,
    crm VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    senha_hash VARCHAR(64) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_medico_especialidade
    FOREIGN KEY (especialidade_id)
    REFERENCES especialidades(id)
);

-- ============================================================
-- TABELA AGENDAMENTOS
-- ============================================================

CREATE TABLE agendamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT,
    especialidade_id INT NOT NULL,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    descricao TEXT,
    status VARCHAR(50) DEFAULT 'Agendado',
    data_agendamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_agendamento_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_agendamento_medico
        FOREIGN KEY (medico_id)
        REFERENCES medicos(id),

    CONSTRAINT fk_agendamento_especialidade
        FOREIGN KEY (especialidade_id)
        REFERENCES especialidades(id)
);

-- ============================================================
-- TABELA RELATÓRIOS / MENSAGENS MÉDICAS
-- ============================================================

CREATE TABLE mensagens_relatorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    agendamento_id INT NOT NULL,
    medico_id INT NOT NULL,
    paciente_id INT NOT NULL,
    tipo VARCHAR(50) DEFAULT 'relatorio',
    conteudo TEXT NOT NULL,
    data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lida BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_relatorio_agendamento
        FOREIGN KEY (agendamento_id)
        REFERENCES agendamentos(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_relatorio_medico
        FOREIGN KEY (medico_id)
        REFERENCES medicos(id),

    CONSTRAINT fk_relatorio_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes(id)
);

-- ============================================================
-- TABELA CONTATO
-- ============================================================

CREATE TABLE mensagens_contato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    assunto VARCHAR(255) NOT NULL,
    mensagem TEXT NOT NULL,
    data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lida BOOLEAN DEFAULT FALSE
);

-- ============================================================
-- PROCEDURE PARA CADASTRO DE PACIENTES
-- ============================================================

DELIMITER $$

CREATE PROCEDURE inserir_paciente(
    IN p_nome VARCHAR(255),
    IN p_cpf VARCHAR(14),
    IN p_email VARCHAR(255),
    IN p_telefone VARCHAR(20),
    IN p_data DATE,
    IN p_sexo VARCHAR(20),
    IN p_senha VARCHAR(100)
)
BEGIN

    INSERT INTO pacientes(
        nome,
        cpf,
        email,
        telefone,
        data_nascimento,
        sexo,
        senha_hash
    )
    VALUES(
        p_nome,
        p_cpf,
        p_email,
        p_telefone,
        p_data,
        p_sexo,
        SHA2(p_senha,256)
    );

END$$

DELIMITER ;

-- ============================================================
-- ESPECIALIDADES
-- ============================================================

INSERT INTO especialidades(nome,descricao,icone) VALUES
('Clínica Geral','Atendimento geral e triagem','GP'),
('Pediatria','Cuidados para crianças','PE'),
('Cardiologia','Tratamento cardíaco','CA'),
('Neurologia','Sistema nervoso','NE'),
('Ginecologia','Saúde da mulher','GI'),
('Ortopedia','Ossos e articulações','OR'),
('Oftalmologia','Visão','OF'),
('Odontologia','Saúde bucal','OD'),
('Pneumologia','Sistema respiratório','PN'),
('Genética','Diagnóstico genético','GE');

-- ============================================================
-- ADMINISTRADOR
-- Login: admin@clinicasaudeplus.com.br
-- Senha: admin123
-- ============================================================

INSERT INTO administradores(
nome,email,senha_hash,ativo
)
VALUES(
'Admin Master',
'admin@clinicasaudeplus.com.br',
SHA2('admin123',256),
1
);

-- ============================================================
-- MÉDICOS
-- Senha padrão: senha123
-- ============================================================

INSERT INTO medicos
(nome,especialidade_id,crm,email,telefone,senha_hash,ativo)
VALUES
('Dr. João Silva',1,'123456/DF','joao@clinica.com.br','61999990001',SHA2('senha123',256),1),
('Dra. Maria Santos',2,'123457/DF','maria@clinica.com.br','61999990002',SHA2('senha123',256),1),
('Dr. Carlos Oliveira',3,'123458/DF','carlos@clinica.com.br','61999990003',SHA2('senha123',256),1),
('Dra. Ana Costa',5,'123459/DF','ana@clinica.com.br','61999990004',SHA2('senha123',256),1),
('Dr. Pedro Ferreira',4,'123460/DF','pedro@clinica.com.br','61999990005',SHA2('senha123',256),1);

-- ============================================================
-- PACIENTE DE TESTE (dados fictícios)
-- Login: paciente@exemplo.com
-- Senha: 123456789
-- ============================================================

INSERT INTO pacientes(
nome,
cpf,
email,
telefone,
data_nascimento,
sexo,
senha_hash
)
VALUES(
'Paciente Teste',
'12345678909',
'paciente@exemplo.com',
'61999990000',
'1990-01-01',
'Masculino',
SHA2('123456789',256)
);

-- ============================================================
-- CONSULTAS
-- ============================================================

SELECT * FROM administradores;
SELECT * FROM pacientes;
SELECT * FROM medicos;
SELECT * FROM especialidades;
SELECT * FROM agendamentos;
SELECT * FROM mensagens_relatorio;
SELECT * FROM mensagens_contato;
