# Guide de Configuration WildFly pour Bank Application

## Prérequis
- WildFly installé (version 27+ recommandée)
- PostgreSQL installé et en cours d'exécution
- Base de données `s5-bank` créée
- Java 11 ou supérieur

## Vue d'ensemble
Votre application utilise le JNDI `java:/BankDS` pour se connecter à PostgreSQL.

---

## ÉTAPE 1 : Télécharger le Driver PostgreSQL

### 1.1 Télécharger le JAR
Téléchargez la dernière version du driver JDBC PostgreSQL :
- URL : https://jdbc.postgresql.org/download/
- Version recommandée : **postgresql-42.7.3.jar** (ou la plus récente)
- Lien direct : https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

### 1.2 Sauvegarder le fichier
Placez le fichier téléchargé dans un dossier temporaire, par exemple :
```
C:\temp\postgresql-42.7.3.jar
```

---

## ÉTAPE 2 : Créer la Base de Données PostgreSQL

### 2.1 Se connecter à PostgreSQL
Ouvrez **pgAdmin** ou utilisez **psql** en ligne de commande :
```bash
psql -U postgres
```

### 2.2 Créer la base de données
```sql
CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
```

### 2.3 Vérifier la création
```sql
\l
```
Vous devriez voir `s5-bank` dans la liste.

---

## ÉTAPE 3 : Configurer WildFly avec le Driver PostgreSQL

### 3.1 Démarrer WildFly (si pas encore démarré)
Ouvrez un terminal et démarrez WildFly :
```cmd
cd D:\wildfly\bin
standalone.bat
```
Laissez cette fenêtre ouverte.

### 3.2 Ouvrir JBoss CLI
Ouvrez un **NOUVEAU** terminal et lancez JBoss CLI :
```cmd
cd D:\wildfly\bin
jboss-cli.bat --connect
```

Vous devriez voir :
```
[standalone@localhost:9990 /]
```

### 3.3 Ajouter le Module PostgreSQL
Dans JBoss CLI, exécutez cette commande (remplacez le chemin par votre fichier JAR) :

```bash
module add --name=org.postgresql --resources=C:\postgresql-42.7.3.jar --dependencies=javax.api,javax.transaction.api
```

**Note** : Utilisez le chemin complet vers votre fichier JAR téléchargé.

### 3.4 Enregistrer le Driver JDBC
Toujours dans JBoss CLI :
```bash
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
```

---

## ÉTAPE 4 : Créer le DataSource

### 4.1 Ajouter le DataSource
Dans JBoss CLI, exécutez (adaptez les paramètres de connexion) :

```bash
data-source add --name=BankDS --jndi-name=java:/BankDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/s5-bank --user-name=postgres --password=votre_mot_de_passe --use-ccm=true --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true --driver-class=org.postgresql.Driver --jta=true --use-java-context=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
```

**Paramètres à adapter** :
- `--connection-url=jdbc:postgresql://localhost:5432/s5-bank` : URL de votre base
- `--user-name=postgres` : Votre utilisateur PostgreSQL
- `--password=votre_mot_de_passe` : Votre mot de passe PostgreSQL

### 4.2 Tester la connexion
```bash
/subsystem=datasources/data-source=BankDS:test-connection-in-pool
```

Vous devriez voir :
```
{
    "outcome" => "success",
    ...
}
```

### 4.3 Quitter JBoss CLI
```bash
exit
```

---

## ÉTAPE 5 : Compiler et Déployer l'Application

### 5.1 Compiler le projet
Dans le dossier du projet :
```cmd
mvn clean install
```

### 5.2 Localiser le fichier EAR
Après compilation, le fichier EAR se trouve ici :
```
Bank-ear\target\Bank.ear
```

### 5.3 Déployer manuellement
Copiez `Bank.ear` dans le dossier de déploiement WildFly :
```cmd
copy Bank-ear\target\Bank.ear C:\path\to\wildfly\standalone\deployments\
```

### 5.4 Vérifier le déploiement
Dans les logs WildFly, vous devriez voir :
```
INFO  [org.jboss.as.server] (DeploymentScanner-threads - 1) WFLYSRV0027: Starting deployment of "Bank.ear"
...
INFO  [org.jboss.as.server] (ServerService Thread Pool -- 75) WFLYSRV0010: Deployed "Bank.ear"
```

---

## ÉTAPE 6 : Vérifier l'Application

### 6.1 Accéder à l'application
Ouvrez votre navigateur :
```
http://localhost:8080/centralizer
```

### 6.2 Vérifier les tables créées
Dans PostgreSQL :
```sql
\c s5-bank
\dt
```

Vous devriez voir les tables créées automatiquement par Hibernate.

---

## Commandes Utiles

### Redéployer l'application
```cmd
# Recompiler
mvn clean install

# Copier le nouveau EAR
copy Bank-ear\target\Bank.ear C:\path\to\wildfly\standalone\deployments\
```

### Voir les DataSources configurés
```bash
jboss-cli.bat --connect
/subsystem=datasources:read-resource
```

### Supprimer le DataSource (si besoin de reconfigurer)
```bash
jboss-cli.bat --connect
data-source remove --name=BankDS
```

### Voir les logs WildFly
```
C:\path\to\wildfly\standalone\log\server.log
```

---

## Dépannage

### Erreur : "Driver not found"
- Vérifiez que le module PostgreSQL est bien créé
- Vérifiez le chemin du JAR dans la commande `module add`

### Erreur : "Connection refused"
- Vérifiez que PostgreSQL est démarré
- Vérifiez l'URL de connexion (host, port, nom de base)
- Vérifiez les credentials (user, password)

### Erreur : "JNDI name not found"
- Vérifiez que le DataSource est bien créé avec le nom `BankDS`
- Vérifiez que le JNDI est `java:/BankDS`

### Tables non créées
- Vérifiez les logs WildFly pour les erreurs Hibernate
- Vérifiez que l'utilisateur PostgreSQL a les droits CREATE TABLE

---

## Configuration Actuelle du Projet

**JNDI utilisé** : `java:/BankDS`

**Modules utilisant ce DataSource** :
- `current` (CurrentAccount, CurrentTransaction)
- `loan` (Loan, LoanPayment)
- `customer` (Customer)

**Base de données** : PostgreSQL `s5-bank`

**Hibernate** : Configuré pour créer/mettre à jour automatiquement les tables
