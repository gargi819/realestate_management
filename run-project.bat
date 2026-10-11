@echo off
title Real Estate Management System Launcher
echo ========================================================
echo      Real Estate Management System - One-Click Launcher
echo ========================================================

REM 1. Ensure MySQL is running
echo [1/3] Checking MySQL Server on Port 3306...
netstat -ano | findstr :3306 >nul
if %errorlevel% neq 0 (
    echo Starting MySQL via XAMPP...
    start "" /b "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini"
    timeout /t 3 /nobreak >nul
) else (
    echo MySQL is running on port 3306.
)

REM 2. Start Spring Boot Backend
echo [2/3] Launching Spring Boot Backend on http://localhost:8080...
start "Backend" "%~dp0start-backend.bat"

REM 3. Start Angular Frontend
echo [3/3] Launching Angular Frontend on http://localhost:4200...
start "Frontend" "%~dp0start-frontend.bat"

echo ========================================================
echo Both servers are starting in separate windows.
echo   - Backend:  http://localhost:8080
echo   - Frontend: http://localhost:4200
echo Opening browser in 6 seconds...
echo ========================================================
timeout /t 6 /nobreak >nul
start http://localhost:4200
