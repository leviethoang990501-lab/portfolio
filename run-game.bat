@echo off
setlocal
chcp 65001 >nul
title HSO Client

rem Co the dat bien HSO_CLIENT_HOME de dung mot bo client o vi tri khac.
if not defined HSO_CLIENT_HOME set "HSO_CLIENT_HOME=C:\Users\Admin\Documents\Codex\HSO-Viet-Hoang-Client"
set "CLIENT_JAVA=%HSO_CLIENT_HOME%\runtime\jdk8u504-b01-jre\bin\java.exe"
set "CLIENT_LOG=%HSO_CLIENT_HOME%\client-startup.log"
set "CLIENT_OUTPUT=%HSO_CLIENT_HOME%\client-console.log"

if not exist "%CLIENT_JAVA%" (
    echo [LOI] Khong tim thay Java cua client:
    echo %CLIENT_JAVA%
    pause
    exit /b 1
)
if not exist "%HSO_CLIENT_HOME%\launcher\HsoLauncher.class" (
    echo [LOI] Khong tim thay launcher cua game trong:
    echo %HSO_CLIENT_HOME%\launcher
    pause
    exit /b 1
)
if not exist "%HSO_CLIENT_HOME%\KEmulator\KEmulator.jar" (
    echo [LOI] Khong tim thay KEmulator.jar trong:
    echo %HSO_CLIENT_HOME%\KEmulator
    pause
    exit /b 1
)
if not exist "%HSO_CLIENT_HOME%\hso-client.jar" (
    echo [LOI] Khong tim thay hso-client.jar trong:
    echo %HSO_CLIENT_HOME%
    pause
    exit /b 1
)

pushd "%HSO_CLIENT_HOME%"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0launch-client.ps1" -ClientHome "%HSO_CLIENT_HOME%"
set "CLIENT_EXIT_CODE=%ERRORLEVEL%"
popd

if not "%CLIENT_EXIT_CODE%"=="0" (
    echo [LOI] Khong mo duoc client, ma loi %CLIENT_EXIT_CODE%.
    echo Xem log: %CLIENT_LOG% va %CLIENT_OUTPUT%
    type "%CLIENT_LOG%"
    type "%CLIENT_OUTPUT%"
    pause
)
exit /b %CLIENT_EXIT_CODE%
