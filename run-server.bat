@echo off
setlocal
chcp 65001 >nul
title HSO Server

cd /d "%~dp0"
set "SERVER_JAR=target\HSO_Re_2-1.0-jar-with-dependencies.jar"
set "SERVER_PORT=19129"

where java >nul 2>&1
if errorlevel 1 (
    echo [LOI] Khong tim thay Java trong PATH.
    echo Hay cai Java 17 tro len, sau do mo lai file nay.
    pause
    exit /b 1
)

if not exist "hso.conf" (
    echo [LOI] Khong tim thay hso.conf trong:
    echo %CD%
    pause
    exit /b 1
)

if not exist "data\" (
    echo [LOI] Khong tim thay thu muc data trong:
    echo %CD%
    pause
    exit /b 1
)

if not exist "%SERVER_JAR%" (
    echo [LOI] Khong tim thay file server:
    echo %CD%\%SERVER_JAR%
    pause
    exit /b 1
)

call :mysql_open
if errorlevel 1 (
    if exist "C:\xampp\mysql\bin\mysqld.exe" (
        echo [DB] MySQL chua chay. Dang khoi dong MariaDB cua XAMPP...
        powershell -NoProfile -Command "Start-Process -FilePath 'C:\xampp\mysql\bin\mysqld.exe' -ArgumentList @('--defaults-file=C:\xampp\mysql\bin\my.ini','--standalone') -WorkingDirectory 'C:\xampp\mysql\bin' -WindowStyle Hidden"
        for /L %%I in (1,1,20) do (
            powershell -NoProfile -Command "Start-Sleep -Seconds 1"
            call :mysql_open
            if not errorlevel 1 goto mysql_ready
        )
    )
    echo [LOI] Khong ket noi duoc MySQL tai 127.0.0.1:3306.
    echo Hay mo MySQL trong XAMPP Control Panel roi chay lai.
    pause
    exit /b 1
)

:mysql_ready
echo [OK] MySQL da san sang.

for /f "tokens=1,* delims=:" %%A in ('findstr /b /c:"port:" "hso.conf"') do set "SERVER_PORT=%%B"
for /f "tokens=*" %%A in ("%SERVER_PORT%") do set "SERVER_PORT=%%A"
powershell -NoProfile -Command "$c=New-Object Net.Sockets.TcpClient; try {$c.Connect('127.0.0.1',%SERVER_PORT%); exit 0} catch {exit 1} finally {$c.Dispose()}" >nul 2>&1
if not errorlevel 1 (
    echo [OK] Cong %SERVER_PORT% da co server lang nghe. Khong khoi dong trung.
    exit /b 0
)

echo ========================================
echo Dang khoi dong HSO Server...
echo Thu muc: %CD%
echo JAR: %SERVER_JAR%
echo ========================================
echo.

java -server -jar "%SERVER_JAR%"
set "SERVER_EXIT_CODE=%ERRORLEVEL%"

echo.
if not "%SERVER_EXIT_CODE%"=="0" (
    echo [LOI] Server da dung voi ma loi %SERVER_EXIT_CODE%.
) else (
    echo Server da dung.
)
echo Nhan phim bat ky de dong cua so.
pause >nul
exit /b %SERVER_EXIT_CODE%

:mysql_open
powershell -NoProfile -Command "$c=New-Object Net.Sockets.TcpClient; try {$c.Connect('127.0.0.1',3306); exit 0} catch {exit 1} finally {$c.Dispose()}" >nul 2>&1
exit /b %ERRORLEVEL%
