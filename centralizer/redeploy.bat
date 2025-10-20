@echo off
echo ========================================
echo REDEPLOY CENTRALIZER TO WILDFLY
echo ========================================
echo.

echo [1/3] Cleaning and building project...
call mvn clean package
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Copying WAR to WildFly deployments...
copy /Y target\centralizer.war C:\wildfly\standalone\deployments\
if %errorlevel% neq 0 (
    echo ERROR: Failed to copy WAR file!
    echo Make sure WildFly is installed at C:\wildfly\
    pause
    exit /b 1
)

echo.
echo [3/3] Creating deployment marker...
echo. > C:\wildfly\standalone\deployments\centralizer.war.dodeploy

echo.
echo ========================================
echo SUCCESS! Centralizer redeployed
echo ========================================
echo.
echo The application should be available at:
echo http://localhost:8080/centralizer
echo.
echo Wait a few seconds for WildFly to deploy...
echo.
pause
