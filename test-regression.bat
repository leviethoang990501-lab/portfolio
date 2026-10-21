@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
set "APP_JAR=target\HSO_Re_2-1.0-jar-with-dependencies.jar"
rem javac on this machine cannot write class files below a path containing
rem Vietnamese characters. Keep generated tests in the ASCII temp directory.
set "TEST_OUT=%TEMP%\hso-server-tests"

if not exist "%APP_JAR%" (
  echo [LOI] Khong tim thay %APP_JAR%
  pause
  exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
  echo [LOI] Can JDK co lenh javac de chay regression test.
  pause
  exit /b 1
)

if not exist "%TEST_OUT%" mkdir "%TEST_OUT%"
javac -encoding UTF-8 -cp "%APP_JAR%" -d "%TEST_OUT%" "src\test\java\regression\CoreRegressionTest.java"
if errorlevel 1 (
  echo [LOI] Bien dich regression test that bai.
  pause
  exit /b 1
)

java -ea -cp "%APP_JAR%;%TEST_OUT%" regression.CoreRegressionTest
if errorlevel 1 (
  echo [LOI] Regression test that bai.
  pause
  exit /b 1
)

echo [OK] Tat ca regression test da dat.
pause
