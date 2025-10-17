@echo off
REM ============================================================================
REM Script de configuration automatique de WildFly pour Bank Application
REM ============================================================================
echo.
echo ========================================
echo Configuration WildFly pour Bank App
echo ========================================
echo.

REM ============================================================================
REM CONFIGURATION - MODIFIEZ CES VARIABLES SELON VOTRE ENVIRONNEMENT
REM ============================================================================

REM Chemin vers WildFly
set WILDFLY_HOME=C:\wildfly

REM Chemin vers le fichier JAR PostgreSQL (à télécharger depuis https://jdbc.postgresql.org/download/)
set POSTGRES_JAR=C:\temp\postgresql-42.7.3.jar

REM Paramètres de connexion PostgreSQL
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=s5-bank
set DB_USER=postgres
set DB_PASSWORD=postgres

REM ============================================================================
REM VÉRIFICATIONS
REM ============================================================================

echo [1/6] Verification des prerequis...
echo.

if not exist "%WILDFLY_HOME%\bin\jboss-cli.bat" (
    echo ERREUR: WildFly non trouve a %WILDFLY_HOME%
    echo Modifiez la variable WILDFLY_HOME dans ce script.
    pause
    exit /b 1
)

if not exist "%POSTGRES_JAR%" (
    echo ERREUR: Driver PostgreSQL non trouve a %POSTGRES_JAR%
    echo.
    echo Telechargez le driver depuis: https://jdbc.postgresql.org/download/
    echo Puis modifiez la variable POSTGRES_JAR dans ce script.
    pause
    exit /b 1
)

echo OK - WildFly trouve
echo OK - Driver PostgreSQL trouve
echo.

REM ============================================================================
REM CONFIGURATION DU DRIVER
REM ============================================================================

echo [2/6] Configuration du driver PostgreSQL...
echo.

"%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="module add --name=org.postgresql --resources=%POSTGRES_JAR% --dependencies=javax.api,javax.transaction.api" 2>nul

if errorlevel 1 (
    echo INFO: Le module existe peut-etre deja, on continue...
)

"%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)" 2>nul

if errorlevel 1 (
    echo INFO: Le driver existe peut-etre deja, on continue...
)

echo OK - Driver configure
echo.

REM ============================================================================
REM CONFIGURATION DU DATASOURCE
REM ============================================================================

echo [3/6] Configuration du DataSource BankDS...
echo.

set CONNECTION_URL=jdbc:postgresql://%DB_HOST%:%DB_PORT%/%DB_NAME%

"%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="data-source add --name=BankDS --jndi-name=java:/BankDS --driver-name=postgresql --connection-url=%CONNECTION_URL% --user-name=%DB_USER% --password=%DB_PASSWORD% --use-ccm=true --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true --driver-class=org.postgresql.Driver --jta=true --use-java-context=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter" 2>nul

if errorlevel 1 (
    echo INFO: Le DataSource existe peut-etre deja, tentative de mise a jour...
    "%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="data-source remove --name=BankDS"
    "%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="data-source add --name=BankDS --jndi-name=java:/BankDS --driver-name=postgresql --connection-url=%CONNECTION_URL% --user-name=%DB_USER% --password=%DB_PASSWORD% --use-ccm=true --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true --driver-class=org.postgresql.Driver --jta=true --use-java-context=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"
)

echo OK - DataSource configure
echo.

REM ============================================================================
REM TEST DE CONNEXION
REM ============================================================================

echo [4/6] Test de la connexion a la base de donnees...
echo.

"%WILDFLY_HOME%\bin\jboss-cli.bat" --connect --command="/subsystem=datasources/data-source=BankDS:test-connection-in-pool"

if errorlevel 1 (
    echo.
    echo ERREUR: Impossible de se connecter a la base de donnees !
    echo.
    echo Verifiez que:
    echo - PostgreSQL est demarre
    echo - La base de donnees '%DB_NAME%' existe
    echo - Les credentials sont corrects (user: %DB_USER%)
    echo - Le port %DB_PORT% est accessible
    echo.
    pause
    exit /b 1
)

echo OK - Connexion reussie
echo.

REM ============================================================================
REM COMPILATION DU PROJET
REM ============================================================================

echo [5/6] Compilation du projet...
echo.

call mvn clean install

if errorlevel 1 (
    echo.
    echo ERREUR: La compilation a echoue !
    pause
    exit /b 1
)

echo OK - Compilation reussie
echo.

REM ============================================================================
REM DÉPLOIEMENT
REM ============================================================================

echo [6/6] Deploiement de l'application...
echo.

if not exist "Bank-ear\target\Bank.ear" (
    echo ERREUR: Le fichier Bank.ear n'a pas ete trouve !
    pause
    exit /b 1
)

copy /Y "Bank-ear\target\Bank.ear" "%WILDFLY_HOME%\standalone\deployments\"

if errorlevel 1 (
    echo ERREUR: Impossible de copier le fichier EAR !
    pause
    exit /b 1
)

echo OK - Application deployee
echo.

REM ============================================================================
REM RÉSUMÉ
REM ============================================================================

echo ========================================
echo Configuration terminee avec succes !
echo ========================================
echo.
echo Application deployee: Bank.ear
echo DataSource JNDI: java:/BankDS
echo Base de donnees: %DB_NAME%
echo.
echo Acces a l'application:
echo   http://localhost:8080/centralizer
echo.
echo Consultez les logs WildFly pour verifier le deploiement:
echo   %WILDFLY_HOME%\standalone\log\server.log
echo.
pause
