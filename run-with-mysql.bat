@echo off
echo Starting application with MySQL (Local Profile)...
echo.
echo Make sure MySQL is running on localhost:3306
echo Database: chi_hoi_kong_brech
echo.

set SPRING_PROFILES_ACTIVE=local

echo Running: mvn spring-boot:run
mvn spring-boot:run

pause