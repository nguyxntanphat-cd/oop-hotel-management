@echo off
REM Biên dịch và chạy chương trình (Windows). Chạy từ thư mục gốc repo: scripts\run.bat
chcp 65001 > nul
cd /d "%~dp0.."
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > out\sources.txt
javac -encoding UTF-8 -d out @out\sources.txt
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp out hotel.Main
