@echo off
echo ========================================
echo VERIFICATION DES FICHIERS HTML
echo ========================================
echo.

echo Verification des fichiers dans centralizer/src/main/webapp/modules/
echo.

echo [Current Account]
if exist "centralizer\src\main\webapp\modules\current\list.html" (
    echo [OK] current/list.html
) else (
    echo [MANQUANT] current/list.html
)

echo.
echo [Deposit Account]
if exist "centralizer\src\main\webapp\modules\deposit\list.html" (
    echo [OK] deposit/list.html
) else (
    echo [MANQUANT] deposit/list.html
)

if exist "centralizer\src\main\webapp\modules\deposit\new.html" (
    echo [OK] deposit/new.html
) else (
    echo [MANQUANT] deposit/new.html
)

if exist "centralizer\src\main\webapp\modules\deposit\deposit.html" (
    echo [OK] deposit/deposit.html
) else (
    echo [MANQUANT] deposit/deposit.html
)

if exist "centralizer\src\main\webapp\modules\deposit\withdraw.html" (
    echo [OK] deposit/withdraw.html
) else (
    echo [MANQUANT] deposit/withdraw.html
)

if exist "centralizer\src\main\webapp\modules\deposit\transactions.html" (
    echo [OK] deposit/transactions.html
) else (
    echo [MANQUANT] deposit/transactions.html
)

echo.
echo [Loan]
if exist "centralizer\src\main\webapp\modules\loan\list.html" (
    echo [OK] loan/list.html
) else (
    echo [MANQUANT] loan/list.html
)

echo.
echo ========================================
echo VERIFICATION DU BUILD
echo ========================================
echo.

if exist "Bank-ear\target\Bank.ear" (
    echo [OK] Bank.ear existe
    dir "Bank-ear\target\Bank.ear" | findstr Bank.ear
) else (
    echo [MANQUANT] Bank.ear n'existe pas
    echo Vous devez compiler le projet avec: mvn clean package
)

echo.
echo ========================================
echo VERIFICATION DU DEPLOIEMENT
echo ========================================
echo.

if exist "D:\wildfly\standalone\deployments\Bank.ear" (
    echo [OK] Bank.ear copie dans WildFly
    dir "D:\wildfly\standalone\deployments\Bank.ear" | findstr Bank.ear
) else (
    echo [MANQUANT] Bank.ear n'est pas dans WildFly
    echo Vous devez copier le fichier avec redeploy-bank.bat
)

if exist "D:\wildfly\standalone\deployments\Bank.ear.deployed" (
    echo [OK] Bank.ear est deploye (fichier .deployed existe)
) else (
    if exist "D:\wildfly\standalone\deployments\Bank.ear.failed" (
        echo [ERREUR] Le deploiement a echoue (fichier .failed existe)
        echo Consultez les logs WildFly
    ) else (
        echo [EN COURS] Bank.ear est en cours de deploiement
        echo Attendez quelques secondes et relancez ce script
    )
)

echo.
echo ========================================
echo RESUME
echo ========================================
echo.
echo Si tous les fichiers sont [OK], vous pouvez tester l'application :
echo http://localhost:8080/centralizer
echo.
echo Si des fichiers sont [MANQUANT], executez :
echo redeploy-bank.bat
echo.
pause
