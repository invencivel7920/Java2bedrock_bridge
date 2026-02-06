@echo off
REM ================================================
REM GitHub Actions Workflow Trigger Script (Windows)
REM ================================================
REM Usage: trigger-workflow.bat [gradle|appimage|both|list]

setlocal enabledelayedexpansion

color 0A
cls

echo.
echo ===============================================
echo   GitHub Actions Workflow Trigger
echo ===============================================
echo.

REM Check if gh CLI is installed
gh --version >nul 2>&1
if errorlevel 1 (
    echo.
    color 0C
    echo ERROR: GitHub CLI not found.
    echo.
    echo Install GitHub CLI:
    echo   1. Download from: https://github.com/cli/cli/releases
    echo   2. Or use: choco install gh
    echo   3. Then: gh auth login
    echo.
    pause
    exit /b 1
)

color 0A
echo [OK] GitHub CLI found
echo.

REM Check authentication
gh auth status >nul 2>&1
if errorlevel 1 (
    color 0C
    echo ERROR: Not authenticated with GitHub.
    echo Run: gh auth login
    color 0A
    echo.
    pause
    exit /b 1
)

echo [OK] Authenticated with GitHub
echo.

REM Get user input
set WORKFLOW=%1

if "%WORKFLOW%"=="" (
    set WORKFLOW=both
)

REM Handle user choice
if /i "%WORKFLOW%"=="gradle" (
    echo [ACTION] Triggering: Gradle Build (Ubuntu/Windows/macOS)
    echo.
    gh workflow run gradle-build.yml -r main
    if errorlevel 1 (
        color 0C
        echo ERROR: Failed to trigger workflow
        color 0A
        pause
        exit /b 1
    )
    color 0A
    echo [OK] Workflow triggered successfully!
    
) else if /i "%WORKFLOW%"=="appimage" (
    echo [ACTION] Triggering: AppImage Release (Linux/Windows/macOS)
    echo.
    gh workflow run appimage-release.yml -r main
    if errorlevel 1 (
        color 0C
        echo ERROR: Failed to trigger workflow
        color 0A
        pause
        exit /b 1
    )
    color 0A
    echo [OK] Workflow triggered successfully!
    
) else if /i "%WORKFLOW%"=="both" (
    echo [ACTION] Triggering: Gradle Build
    echo.
    gh workflow run gradle-build.yml -r main
    if errorlevel 1 (
        color 0C
        echo ERROR: Failed to trigger gradle workflow
        color 0A
        pause
        exit /b 1
    )
    color 0A
    echo [OK] Gradle workflow triggered
    echo.
    timeout /t 2
    echo.
    echo [ACTION] Triggering: AppImage Release
    echo.
    gh workflow run appimage-release.yml -r main
    if errorlevel 1 (
        color 0C
        echo ERROR: Failed to trigger appimage workflow
        color 0A
        pause
        exit /b 1
    )
    color 0A
    echo [OK] AppImage workflow triggered
    
) else if /i "%WORKFLOW%"=="list" (
    echo [ACTION] Recent workflow runs:
    echo.
    gh run list -L 10
    
) else (
    color 0C
    echo ERROR: Unknown option: %WORKFLOW%
    color 0A
    echo Usage: trigger-workflow.bat [gradle^|appimage^|both^|list]
    echo.
    pause
    exit /b 1
)

echo.
echo ===============================================
echo   Monitor Progress:
echo   - Web: https://github.com/invencivel7920/Java2bedrock_bridge/actions
echo   - CLI: gh run list
echo   - CLI: gh run watch
echo   - CLI: gh run download ^<run-id^> -n appimage-linux
echo ===============================================
echo.
pause
