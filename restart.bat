@echo off
echo Stopping existing Java processes...
taskkill /f /im java.exe 2>nul
timeout /t 3 /nobreak >nul

echo Starting Spring Boot application...
mvn spring-boot:run