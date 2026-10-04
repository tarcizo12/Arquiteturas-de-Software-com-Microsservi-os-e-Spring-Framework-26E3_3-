@echo off
setlocal
cd /d "%~dp0"

echo ==========================================
echo  RESET TOTAL DOS BANCOS (somente testes)
echo ==========================================
echo Isso vai APAGAR containers, volumes (todos os dados) e imagens
echo do docker-compose (mysql-dev e mysql-prod).
echo.
set /p confirma=Tem certeza? (S/N): 
if /i not "%confirma%"=="S" (
    echo Cancelado.
    pause
    exit /b 0
)

docker info >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao esta rodando.
    pause
    exit /b 1
)

echo.
echo Removendo containers, volumes, imagens e orfaos...
docker compose down --volumes --rmi all --remove-orphans
if errorlevel 1 (
    echo [ERRO] Falha ao remover recursos.
    pause
    exit /b 1
)

echo.
echo Tudo removido. Use subir-banco.bat para recriar do zero.
pause
exit /b 0