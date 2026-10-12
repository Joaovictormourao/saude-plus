#!/bin/bash

# Clínica Saúde+ - Script de Execução
# Funciona em Linux/Mac

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}"
echo "╔═══════════════════════════════════════════════════════╗"
echo "║       🏥 Clínica Saúde+ - Script de Execução        ║"
echo "║                   Versão 1.0.0                       ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven não encontrado!${NC}"
    echo "Instale Maven: https://maven.apache.org/install.html"
    exit 1
fi

echo -e "${GREEN}✓ Maven encontrado$(mvn --version | grep 'Apache Maven' | cut -d' ' -f3)${NC}"

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java não encontrado!${NC}"
    echo "Instale Java 17+: https://adoptium.net/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K.*?(?=")')
echo -e "${GREEN}✓ Java encontrado (versão $JAVA_VERSION)${NC}"

# Verificar se MySQL está rodando
echo -e "${YELLOW}Verificando MySQL...${NC}"
if mysql -u root -e "SELECT 1" &> /dev/null; then
    echo -e "${GREEN}✓ MySQL está rodando${NC}"
    
    # Verificar se banco existe
    if mysql -u root -e "USE clinica_saude_plus" &> /dev/null; then
        echo -e "${GREEN}✓ Banco 'clinica_saude_plus' já existe${NC}"
    else
        echo -e "${YELLOW}⚠ Banco não encontrado. Criando...${NC}"
        mysql -u root < database/clinica_saude_plus.sql
        echo -e "${GREEN}✓ Banco criado com sucesso${NC}"
    fi
else
    echo -e "${RED}✗ MySQL não está rodando!${NC}"
    echo "Inicie MySQL e tente novamente."
    echo ""
    echo "No Linux:"
    echo "  sudo service mysql start"
    echo ""
    echo "No Mac:"
    echo "  mysql.server start"
    exit 1
fi

# Compilar o projeto
echo -e "${YELLOW}Compilando projeto...${NC}"
mvn clean install

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Erro na compilação!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Compilação concluída${NC}"

# Iniciar Tomcat
echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}Iniciando Tomcat na porta 8080...${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}Acesse em: http://localhost:8080/clinica${NC}"
echo ""
echo "Contas de teste:"
echo "  Admin: admin@clinicasaudeplus.com.br / admin123"
echo "  Médico: joao@clinica.com.br / senha123"
echo "  Paciente: paciente@exemplo.com / 123456789"
echo ""
echo -e "${YELLOW}Pressione Ctrl+C para parar o servidor${NC}"
echo ""

mvn -Dtomcat.port=8080 cargo:run

