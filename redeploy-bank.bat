@echo off
echo ========================================
echo REDEPLOY BANK.EAR TO WILDFLY
echo ========================================
echo.

echo [1/4] Cleaning and building ALL modules...
echo This will compile: customer, current, loan, centralizer, and Bank-ear
echo.
call mvn clean package
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo.
echo [2/4] Checking if Bank.ear was created...
if not exist "Bank-ear\target\Bank.ear" (
    echo ERROR: Bank.ear not found in Bank-ear\target\
    echo Make sure the build completed successfully
    pause
    exit /b 1
)

echo Bank.ear found! Size:
dir "Bank-ear\target\Bank.ear" | findstr Bank.ear

echo.
echo [3/4] Copying Bank.ear to WildFly deployments...
copy /Y "Bank-ear\target\Bank.ear" "D:\wildfly\standalone\deployments\"
if %errorlevel% neq 0 (
    echo ERROR: Failed to copy Bank.ear file!
    echo Make sure WildFly is installed at D:\wildfly\
    pause
    exit /b 1
)

echo.
echo [4/4] Creating deployment marker...
echo. > "D:\wildfly\standalone\deployments\Bank.ear.dodeploy"

echo.
echo ========================================
echo SUCCESS! Bank.ear redeployed
echo ========================================
echo.
echo The application should be available at:
echo http://localhost:8080/centralizer
echo.
echo Wait 10-15 seconds for WildFly to deploy the EAR...
echo (EAR deployment takes longer than WAR)
echo.
echo Check WildFly console for:
echo "Deployed Bank.ear"
echo.
pause
