/**
 * Arquivo de configuração centralizada para URLs da API
 * Evita hardcoding de URLs em vários lugares
 */

// Detectar automaticamente a URL base
const getBaseURL = () => {
    const protocol = window.location.protocol;
    const host = window.location.host;
    const pathname = window.location.pathname;
    const contexto = pathname.split('/').filter(Boolean)[0] || 'clinica';

    return `${protocol}//${host}/${contexto}`;
};

const API_BASE_URL = getBaseURL();

// URLs dos endpoints - DEVEM CORRESPONDER COM web.xml
const API_ENDPOINTS = {
    // Especialidades
    ESPECIALIDADES: `${API_BASE_URL}/api/especialidade`,
    
    // Médicos
    MEDICOS: `${API_BASE_URL}/api/medico`,
    
    // Agendamentos
    AGENDAMENTOS: `${API_BASE_URL}/api/agendamento`,
    
    // Pacientes
    PACIENTES: `${API_BASE_URL}/api/paciente`,
    
    // Administradores
    ADMINISTRADORES: `${API_BASE_URL}/api/admin`,
    
    // Mensagens
    MENSAGENS: `${API_BASE_URL}/api/mensagem`
};

// Função auxiliar para fazer requisições
async function fazerRequisicao(url, opcoes = {}) {
    const defaultHeaders = {
        'Content-Type': 'application/json'
    };
    
    const opcoesMerge = {
        ...opcoes,
        headers: { ...defaultHeaders, ...opcoes.headers }
    };
    
    try {
        const response = await fetch(url, opcoesMerge);
        
        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Erro na requisição:', error);
        throw error;
    }
}

console.log('Config carregada. Base URL:', API_BASE_URL);
