@echo off
setlocal
cd /d "%~dp0"

echo ==========================================
echo  Subindo bancos MySQL (dev e prod)
echo ==========================================

docker info >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao esta rodando. Abra o Docker Desktop e tente novamente.
    pause
    exit /b 1
)

echo.
echo [1/3] Subindo containers (nao recria se ja existirem)...
docker compose up -d
if errorlevel 1 (
    echo [ERRO] Falha ao subir os containers.
    pause
    exit /b 1
)

echo.
echo [2/3] Aguardando os bancos ficarem saudaveis...
call :aguardar estoque-mysql-dev
if errorlevel 1 goto :falha
call :aguardar estoque-mysql-prod
if errorlevel 1 goto :falha

echo.
echo [3/3] Garantindo bancos e usuarios (idempotente - IF NOT EXISTS)...
docker exec -i estoque-mysql-dev mysql -uroot -proot_dev < mysql\init-dev.sql 2>nul
if errorlevel 1 goto :falha
docker exec -i estoque-mysql-prod mysql -uroot -proot_prod < mysql\init-prod.sql 2>nul
if errorlevel 1 goto :falha

echo.
echo ==========================================
echo  Tudo pronto!
echo   DEV : localhost:3306  (estoque_principal, movimentacao_db)
echo   PROD: localhost:3307  (estoque_principal, movimentacao_db)
echo ==========================================
echo.
echo Bancos, usuarios e tabelas garantidos via mysql\init-*.sql (idempotente).
pause
exit /b 0

:aguardar
set /a tentativas=0
:loop
for /f %%s in ('docker inspect --format "{{.State.Health.Status}}" %1 2^>nul') do set status=%%s
if "%status%"=="healthy" (
    echo   %1 OK
    exit /b 0
)
set /a tentativas+=1
if %tentativas% GEQ 40 (
    echo   [ERRO] %1 nao ficou saudavel a tempo.
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :loop

:falha
exit /b 1