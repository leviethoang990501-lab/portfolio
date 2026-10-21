@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build-local.ps1"
if errorlevel 1 (
    echo [LOI] Build that bai.
    pause
    exit /b 1
)
echo [OK] Build server thanh cong.
pause
