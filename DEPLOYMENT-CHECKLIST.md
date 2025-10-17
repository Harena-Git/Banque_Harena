# ✅ Checklist de Déploiement Bank Application

## 📋 Prérequis

- [ ] **Java JDK 11+** installé
  - Vérifier : `java -version`
  
- [ ] **Maven** installé
  - Vérifier : `mvn -version`
  
- [ ] **PostgreSQL** installé et démarré
  - Vérifier : Ouvrir pgAdmin ou `psql -U postgres`
  
- [ ] **WildFly** installé (version 27+ recommandée)
  - Télécharger : https://www.wildfly.org/downloads/

---

## 🚀 Déploiement Rapide (Méthode Automatique)

### Étape 1 : Télécharger le Driver PostgreSQL
- [ ] Aller sur https://jdbc.postgresql.org/download/
- [ ] Télécharger **postgresql-42.7.3.jar** (ou version plus récente)
- [ ] Sauvegarder dans `C:\temp\postgresql-42.7.3.jar`

### Étape 2 : Créer la Base de Données
- [ ] Ouvrir **pgAdmin** ou **psql**
- [ ] Exécuter le script `wildfly-scripts\create-database.sql`
  
  **OU** en ligne de commande :
  ```sql
  CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
  ```

### Étape 3 : Démarrer WildFly
- [ ] Ouvrir un terminal
- [ ] Exécuter :
  ```cmd
  cd C:\wildfly\bin
  standalone.bat
  ```
- [ ] Attendre le message : `WildFly ... started in ... ms`
- [ ] **Laisser cette fenêtre ouverte**

### Étape 4 : Configurer le Script
- [ ] Ouvrir `wildfly-scripts\setup-wildfly.bat`
- [ ] Modifier ces lignes selon votre configuration :
  ```batch
  set WILDFLY_HOME=C:\wildfly
  set POSTGRES_JAR=C:\temp\postgresql-42.7.3.jar
  set DB_USER=postgres
  set DB_PASSWORD=postgres
  ```
- [ ] Sauvegarder le fichier

### Étape 5 : Exécuter le Script
- [ ] Ouvrir un **NOUVEAU** terminal
- [ ] Aller dans le dossier du projet :
  ```cmd
  cd D:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
  ```
- [ ] Exécuter :
  ```cmd
  wildfly-scripts\setup-wildfly.bat
  ```
- [ ] Attendre la fin (environ 1-2 minutes)

### Étape 6 : Vérifier le Déploiement
- [ ] Ouvrir le navigateur
- [ ] Aller sur : http://localhost:8080/centralizer
- [ ] Vérifier que l'application se charge

### Étape 7 : Vérifier les Tables
- [ ] Ouvrir **pgAdmin** ou **psql**
- [ ] Se connecter à la base `s5-bank`
- [ ] Exécuter :
  ```sql
  \c s5-bank
  \dt
  ```
- [ ] Vérifier que les tables sont créées :
  - `customers`
  - `current_accounts`
  - `current_transactions`
  - `loans`
  - `loan_payments`

---

## 🔧 Déploiement Manuel (Étape par Étape)

Si le script automatique ne fonctionne pas, suivez ces étapes :

### 1. Télécharger le Driver
- [ ] Télécharger postgresql-42.7.3.jar
- [ ] Placer dans `C:\temp\`

### 2. Créer la Base de Données
- [ ] Ouvrir psql :
  ```cmd
  psql -U postgres
  ```
- [ ] Créer la base :
  ```sql
  CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
  \q
  ```

### 3. Démarrer WildFly
- [ ] Terminal 1 :
  ```cmd
  cd C:\wildfly\bin
  standalone.bat
  ```

### 4. Configurer le Driver PostgreSQL
- [ ] Terminal 2 :
  ```cmd
  cd C:\wildfly\bin
  jboss-cli.bat --connect
  ```
- [ ] Dans JBoss CLI :
  ```bash
  module add --name=org.postgresql --resources=C:\temp\postgresql-42.7.3.jar --dependencies=javax.api,javax.transaction.api
  ```
- [ ] Puis :
  ```bash
  /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
  ```

### 5. Configurer le DataSource
- [ ] Dans JBoss CLI (remplacer `MOT_DE_PASSE`) :
  ```bash
  data-source add --name=BankDS --jndi-name=java:/BankDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/s5-bank --user-name=postgres --password=MOT_DE_PASSE --use-ccm=true --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true --driver-class=org.postgresql.Driver --jta=true --use-java-context=true --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
  ```

### 6. Tester la Connexion
- [ ] Dans JBoss CLI :
  ```bash
  /subsystem=datasources/data-source=BankDS:test-connection-in-pool
  ```
- [ ] Vérifier : `"outcome" => "success"`
- [ ] Quitter :
  ```bash
  exit
  ```

### 7. Compiler le Projet
- [ ] Terminal 3 :
  ```cmd
  cd D:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
  mvn clean install
  ```

### 8. Déployer l'Application
- [ ] Copier le fichier EAR :
  ```cmd
  copy Bank-ear\target\Bank.ear C:\wildfly\standalone\deployments\
  ```

### 9. Vérifier le Déploiement
- [ ] Consulter les logs WildFly (Terminal 1)
- [ ] Chercher : `Deployed "Bank.ear"`
- [ ] Ouvrir : http://localhost:8080/centralizer

---

## 🐛 Dépannage

### ❌ Erreur : "Driver not found"
**Solution :**
- Vérifier que le JAR PostgreSQL existe
- Vérifier le chemin dans la commande `module add`
- Redémarrer WildFly

### ❌ Erreur : "Connection refused"
**Solution :**
- Vérifier que PostgreSQL est démarré
- Vérifier le port (par défaut 5432)
- Vérifier les credentials (user/password)

### ❌ Erreur : "Database does not exist"
**Solution :**
- Créer la base de données :
  ```sql
  CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
  ```

### ❌ Erreur : "JNDI name not found"
**Solution :**
- Vérifier que le DataSource est créé :
  ```bash
  jboss-cli.bat --connect --command="/subsystem=datasources:read-resource"
  ```
- Vérifier le JNDI : `java:/BankDS`

### ❌ Application ne se charge pas
**Solution :**
- Consulter les logs : `C:\wildfly\standalone\log\server.log`
- Chercher les erreurs avec `ERROR` ou `FAILED`
- Vérifier que le fichier `Bank.ear` est bien dans `deployments\`

### ❌ Tables non créées
**Solution :**
- Vérifier les logs Hibernate dans WildFly
- Vérifier que l'utilisateur PostgreSQL a les droits CREATE TABLE
- Vérifier les fichiers `persistence.xml`

---

## 📚 Ressources

### Fichiers Importants
- **Guide complet** : `WILDFLY-SETUP-GUIDE.md`
- **Scripts** : Dossier `wildfly-scripts/`
- **Configuration** : `persistence.xml` dans chaque module

### Commandes Utiles
```cmd
# Recompiler et redéployer
mvn clean install
copy Bank-ear\target\Bank.ear C:\wildfly\standalone\deployments\

# Voir les DataSources
jboss-cli.bat --connect --command="/subsystem=datasources:read-resource"

# Voir les logs
type C:\wildfly\standalone\log\server.log

# Tester la connexion DB
jboss-cli.bat --connect --command="/subsystem=datasources/data-source=BankDS:test-connection-in-pool"
```

### URLs
- **Application** : http://localhost:8080/centralizer
- **Console WildFly** : http://localhost:9990
- **Driver PostgreSQL** : https://jdbc.postgresql.org/download/

---

## ✨ Configuration Actuelle

| Paramètre | Valeur |
|-----------|--------|
| **JNDI** | `java:/BankDS` |
| **Base de données** | `s5-bank` |
| **Driver** | PostgreSQL JDBC |
| **Port PostgreSQL** | 5432 |
| **Port WildFly** | 8080 |
| **Context Root** | `/centralizer` |

---

## 📝 Notes

- Les tables sont créées **automatiquement** par Hibernate au premier déploiement
- Le DataSource `java:/BankDS` est utilisé par tous les modules (current, loan, customer)
- Le fichier EAR final est dans `Bank-ear/target/Bank.ear`
- Pour redéployer, il suffit de copier le nouveau EAR dans `deployments/`

---

**Bon déploiement ! 🚀**
