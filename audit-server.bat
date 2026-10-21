@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
set "APP_JAR=target\HSO_Re_2-1.0-jar-with-dependencies.jar"
rem Use an ASCII output path because javac cannot create class files below
rem this project's Vietnamese directory name on the current Windows locale.
set "TEST_OUT=%TEMP%\hso-server-tests"

if not exist "%APP_JAR%" (
  echo [LOI] Khong tim thay %APP_JAR%
  pause
  exit /b 1
)
if not exist "%TEST_OUT%" mkdir "%TEST_OUT%"

javac -encoding UTF-8 -cp "%APP_JAR%" -d "%TEST_OUT%" "src\test\java\regression\DatabaseFeatureAudit.java"
if errorlevel 1 (
  echo [LOI] Khong bien dich duoc bo kiem tra.
  pause
  exit /b 1
)

java -ea -cp "%APP_JAR%;%TEST_OUT%" regression.DatabaseFeatureAudit
if errorlevel 1 (
  echo [LOI] Du lieu server chua dat yeu cau. Xem loi phia tren.
  pause
  exit /b 1
)

echo [OK] Skill, map va du lieu tinh nang da dat.
pause
