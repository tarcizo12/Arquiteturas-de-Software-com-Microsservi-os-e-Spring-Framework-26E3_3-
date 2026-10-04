@echo off
setlocal
cd /d "%~dp0"

docker info >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao esta rodando. Abra o Docker Desktop e tente novamente.
    pause
    exit /b 1
)

call :principal

echo.
set /p salvar=Salvar este resumo em resumo-bancos.txt? (S/N): 
if /i not "%salvar%"=="S" goto :fim
call :principal > resumo-bancos.txt 2>&1
echo Salvo em %~dp0resumo-bancos.txt

:fim
pause
exit /b 0


:principal
echo ==========================================================
echo  RESUMO DOS BANCOS - %date% %time%
echo ==========================================================
call :container estoque-mysql-dev root_dev DEV 3306
call :container estoque-mysql-prod root_prod PROD 3307
exit /b 0


:container
set "ctn=%~1"
set "pw=%~2"
set "amb=%~3"
set "porta=%~4"
echo.
echo ##########################################################
echo  AMBIENTE %amb%  -  container %ctn%  -  porta %porta%
echo ##########################################################
set "running="
for /f "usebackq delims=" %%r in (`docker inspect --format "{{.State.Running}}" %ctn% 2^>nul`) do set "running=%%r"
if not "%running%"=="true" echo [AVISO] Container nao esta rodando. Use subir-banco.bat.
if not "%running%"=="true" exit /b 0
call :banco estoque_principal
call :banco movimentacao_db
exit /b 0


:banco
set "db=%~1"
echo.
echo ----------------------------------------------------------
echo  BANCO: %db%
echo ----------------------------------------------------------
set "existe="
for /f "usebackq delims=" %%n in (`docker exec %ctn% mysql -uroot -p%pw% -N -B -e "SELECT COUNT(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='%db%'" 2^>nul`) do set "existe=%%n"
if not "%existe%"=="1" echo [AVISO] Banco nao existe.
if not "%existe%"=="1" exit /b 0

set "qtd=0"
for /f "usebackq delims=" %%n in (`docker exec %ctn% mysql -uroot -p%pw% -N -B -e "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='%db%' AND TABLE_TYPE='BASE TABLE'" 2^>nul`) do set "qtd=%%n"
echo Tabelas encontradas: %qtd%
if "%qtd%"=="0" echo [INFO] Banco existe, mas ainda nao tem tabelas. As aplicacoes criam ao iniciar.
if "%qtd%"=="0" exit /b 0

echo.
echo Tabelas e linhas aproximadas:
docker exec %ctn% mysql -uroot -p%pw% -t -e "SELECT TABLE_NAME AS tabela, TABLE_ROWS AS linhas_aprox, ENGINE AS engine FROM information_schema.TABLES WHERE TABLE_SCHEMA='%db%' AND TABLE_TYPE='BASE TABLE' ORDER BY TABLE_NAME" 2>nul

echo.
echo Campos por tabela:
docker exec %ctn% mysql -uroot -p%pw% -t -e "SELECT TABLE_NAME AS tabela, COLUMN_NAME AS campo, COLUMN_TYPE AS tipo, IS_NULLABLE AS aceita_nulo, COLUMN_KEY AS chave, COLUMN_DEFAULT AS padrao, EXTRA AS extra FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%db%' ORDER BY TABLE_NAME, ORDINAL_POSITION" 2>nul

echo.
echo Chaves estrangeiras:
docker exec %ctn% mysql -uroot -p%pw% -t -e "SELECT TABLE_NAME AS tabela, COLUMN_NAME AS campo, REFERENCED_TABLE_NAME AS referencia, REFERENCED_COLUMN_NAME AS campo_ref FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA='%db%' AND REFERENCED_TABLE_NAME IS NOT NULL ORDER BY TABLE_NAME" 2>nul
exit /b 0