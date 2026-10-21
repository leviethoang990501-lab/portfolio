@echo off
setlocal EnableExtensions
chcp 65001 >nul
title Khoi dong HSO Server + Game
cd /d "%~dp0"

set "SERVER_PORT=19129"
set "WAIT_SECONDS=20"

echo ========================================
echo       KHOI DONG HSO SERVER + GAME
echo ========================================
echo.

if not exist "run-server.bat" (
    echo [LOI] Khong tim thay run-server.bat trong %CD%
    pause
    exit /b 1
)
if not exist "run-game.bat" (
    echo [LOI] Khong tim thay run-game.bat trong %CD%
    pause
    exit /b 1
)

call :port_open
if errorlevel 1 (
    echo [1/2] Dang mo server...
    start "HSO Server" cmd /k call "%CD%\run-server.bat"

    echo Dang cho server lang nghe cong %SERVER_PORT%...
    for /L %%I in (1,1,%WAIT_SECONDS%) do (
        powershell -NoProfile -Command "Start-Sleep -Seconds 1"
        call :port_open
        if not errorlevel 1 goto server_ready
    )

    echo.
    echo [LOI] Server chua mo cong %SERVER_PORT% sau %WAIT_SECONDS% giay.
    echo Kiem tra cua so HSO Server de xem loi MySQL hoac cau hinh.
    pause
    exit /b 1
) else (
    echo [1/2] Cong %SERVER_PORT% dang mo - khong khoi dong them server.
)

:server_ready
echo [2/2] Dang mo game...
call "%CD%\run-game.bat"
if errorlevel 1 exit /b %ERRORLEVEL%

echo Da gui lenh mo game.
powershell -NoProfile -Command "Start-Sleep -Seconds 2"
exit /b 0

:port_open
powershell -NoProfile -Command "$c=New-Object Net.Sockets.TcpClient; try {$c.Connect('127.0.0.1',%SERVER_PORT%); exit 0} catch {exit 1} finally {$c.Dispose()}" >nul 2>&1
exit /b %ERRORLEVEL%
