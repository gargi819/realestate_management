@echo off
title Real Estate Backend (Spring Boot)
cd /d "%~dp0"
set "PATH=%PATH%;C:\apache-maven-3.9.9\bin"
echo Starting Spring Boot Backend on http://localhost:8080...
mvn spring-boot:run
pause
