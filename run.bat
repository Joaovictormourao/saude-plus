@echo off
REM Clínica Saúde+ - Script de Execução para Windows

setlocal enabledelayedexpansion

color 0A
cls

echo.
echo ╔═══════════════════════════════════════════════════════╗
echo ║       🏥 Clínica Saúde+ - Script de Execução        ║
echo ║                   Versão 1.0.0                       ║
echo ╚═══════════════════════════════════════════════════════╝
echo.

REM Verificar se Maven está instalado
mvn --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo ✗ Maven não encontrado!
    echo Instale Maven: https://maven.apache.org/install.html
    echo.
    pause
    exit /b 1
)

color 0A
for /f "tokens=3" %%i in ('mvn --version ^| findstr /R "Apache Maven"') do set MAVEN_VERSION=%%i
echo ✓ Maven encontrado (versão %MAVEN_VERSION%)

REM Verificar se Java está instalado
java -version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo ✗ Java não encontrado!
    echo Instale Java 17+: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

color 0A
for /f "tokens=2-4 delims=. " %%j in ('java -version 2^>^&1 ^| findstr /R "version"') do (
    set JAVA_VERSION=%%j.%%k.%%l
)
echo ✓ Java encontrado (versão %JAVA_VERSION%)

REM Verificar MySQL (opcional)
echo.
echo Verificando MySQL...
mysql -u root -e "SELECT 1" >nul 2>&1

if errorlevel 1 (
    color 0C
    echo ⚠ MySQL não está respondendo ou não está instalado
    echo Certifique-se de que MySQL está rodando e accessible como 'root'
    echo.
    color 0A
    echo Configuração de MySQL:
    echo   URL: jdbc:mysql://localhost:3306/clinica_saude_plus
    echo   Usuário: root
    echo   Senha: vazia por padrao (defina a variavel de ambiente DB_PASSWORD se necessario)
    echo.
    pause
) else (
    color 0A
    echo ✓ MySQL está rodando
)

REM Compilar o projeto
echo.
color 0E
echo Compilando projeto...
echo.

mvn clean install

if errorlevel 1 (
    color 0C
    echo ✗ Erro na compilação!
    echo.
    pause
    exit /b 1
)

color 0A
echo ✓ Compilação concluída

REM Iniciar Tomcat
echo.
color 0B
echo ════════════════════════════════════════════════════════
echo Iniciando Tomcat na porta 8080...
echo ════════════════════════════════════════════════════════
echo.
echo Acesse em: http://localhost:8080/clinica
echo.
echo Contas de teste:
echo   Admin: admin@clinicasaudeplus.com.br / admin123
echo   Medico: joao@clinica.com.br / senha123
echo   Paciente: paciente@exemplo.com / 123456789
echo.
echo Pressione Ctrl+C para parar o servidor
echo.

mvn -Dtomcat.port=8080 cargo:run

pause

