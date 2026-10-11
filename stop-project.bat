@echo off
title Stop Real Estate Management System
echo ========================================================
echo      Stopping Real Estate Management System
echo ========================================================

echo Stopping Java / Spring Boot process...
taskkill /f /im java.exe 2>nul

echo Stopping Node.js / Angular process...
taskkill /f /im node.exe 2>nul

echo Stopping MySQL daemon...
taskkill /f /im mysqld.exe 2>nul

echo All services stopped successfully.
pause
