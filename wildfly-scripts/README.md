# Scripts de Configuration WildFly

Ce dossier contient des scripts pour automatiser la configuration de WildFly.

## Fichiers

### 1. `setup-wildfly.bat` (RECOMMANDÉ)
**Script automatique complet** qui fait tout en une seule commande :
- Configure le driver PostgreSQL
- Configure le DataSource
- Teste la connexion
- Compile le projet
- Déploie l'application

### 2. `configure-driver.cli`
Script JBoss CLI pour configurer uniquement le driver PostgreSQL.

### 3. `configure-datasource.cli`
Script JBoss CLI pour configurer uniquement le DataSource.

---

## Utilisation Rapide (Méthode Automatique)

### Étape 1 : Prérequis
1. **Téléchargez le driver PostgreSQL** :
   - URL : https://jdbc.postgresql.org/download/postgresql-42.7.3.jar
   - Sauvegardez-le dans `C:\temp\postgresql-42.7.3.jar`

2. **Créez la base de données** :
   ```sql
   CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
   ```

3. **Démarrez WildFly** :
   ```cmd
   C:\wildfly\bin\standalone.bat
   ```

### Étape 2 : Configurer le script
Ouvrez `setup-wildfly.bat` et modifiez ces variables :
```batch
set WILDFLY_HOME=C:\wildfly
set POSTGRES_JAR=C:\temp\postgresql-42.7.3.jar
set DB_USER=postgres
set DB_PASSWORD=postgres
```

### Étape 3 : Exécuter
Ouvrez un **nouveau terminal** dans le dossier du projet et lancez :
```cmd
wildfly-scripts\setup-wildfly.bat
```

Le script va :
1. ✅ Vérifier les prérequis
2. ✅ Configurer le driver PostgreSQL
3. ✅ Configurer le DataSource BankDS
4. ✅ Tester la connexion à la base
5. ✅ Compiler le projet (mvn clean install)
6. ✅ Déployer Bank.ear dans WildFly

### Étape 4 : Vérifier
Ouvrez votre navigateur :
```
http://localhost:8080/centralizer
```

---

## Utilisation Manuelle (Méthode CLI)

Si vous préférez exécuter les étapes une par une :

### 1. Configurer le driver
```cmd
cd C:\wildfly\bin
jboss-cli.bat --connect --file=D:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\wildfly-scripts\configure-driver.cli
```

### 2. Configurer le datasource
```cmd
jboss-cli.bat --connect --file=D:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\wildfly-scripts\configure-datasource.cli
```

### 3. Compiler et déployer
```cmd
cd D:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
mvn clean install
copy Bank-ear\target\Bank.ear C:\wildfly\standalone\deployments\
```

---

## Dépannage

### Erreur : "WildFly non trouvé"
- Vérifiez que WildFly est installé
- Modifiez `WILDFLY_HOME` dans le script

### Erreur : "Driver PostgreSQL non trouvé"
- Téléchargez le JAR depuis https://jdbc.postgresql.org/download/
- Modifiez `POSTGRES_JAR` dans le script

### Erreur : "Connexion à la base échouée"
- Vérifiez que PostgreSQL est démarré
- Vérifiez que la base `s5-bank` existe
- Vérifiez les credentials (user/password)

### Erreur : "Module already exists"
- C'est normal si vous relancez le script
- Le script continue automatiquement

---

## Commandes Utiles

### Voir les DataSources configurés
```cmd
jboss-cli.bat --connect --command="/subsystem=datasources:read-resource"
```

### Supprimer le DataSource
```cmd
jboss-cli.bat --connect --command="data-source remove --name=BankDS"
```

### Redéployer l'application
```cmd
mvn clean install
copy Bank-ear\target\Bank.ear C:\wildfly\standalone\deployments\
```

### Voir les logs WildFly
```
C:\wildfly\standalone\log\server.log
```
