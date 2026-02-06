@echo off
REM Build Script para Java2Bedrock Bridge (Windows)

cls
echo.
echo ╔════════════════════════════════════════╗
echo ║  Java2Bedrock Bridge - Build Script   ║
echo ╚════════════════════════════════════════╝
echo.

REM Verificar Java
echo [1/5] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java nao encontrado! Por favor, instale Java 17+.
    pause
    exit /b 1
)
for /f tokens^=2 %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VER=%%i
echo ✓ Java %JAVA_VER% encontrada
echo.

REM Limpar build anterior
echo [2/5] Limpando build anterior...
if exist build (
    rmdir /s /q build >nul 2>&1
    echo ✓ Limpeza completa
) else (
    echo ✓ Nada para limpar
)
echo.

REM Compilar
echo [3/5] Compilando projeto...
if exist gradlew.bat (
    call gradlew.bat clean build --no-daemon -q
) else (
    echo ❌ gradlew.bat nao encontrado!
    pause
    exit /b 1
)
if errorlevel 1 (
    echo ❌ Compilacao falhou!
    pause
    exit /b 1
)
echo ✓ Compilacao completa
echo.

REM Verificar JAR
echo [4/5] Verificando artefato...
set JAR_FILE=build\libs\java2bedrock-bridge-1.0.0-alpha.jar
if exist "%JAR_FILE%" (
    for %%A in ("%JAR_FILE%") do set JAR_SIZE=%%~zA
    echo ✓ JAR criada: %JAR_FILE% (%JAR_SIZE% bytes)
) else (
    echo ❌ JAR nao foi criada!
    pause
    exit /b 1
)
echo.

REM Done
echo [5/5] Build completo!
echo.
echo ╔════════════════════════════════════════╗
echo ║  ✓ Build Sucesso!                     ║
echo ╚════════════════════════════════════════╝
echo.
echo Para executar:
echo   java -jar %JAR_FILE%
echo.
echo Ou com mais memoria:
echo   java -Xmx2G -jar %JAR_FILE%
echo.
pause
