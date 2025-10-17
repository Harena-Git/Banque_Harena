# 📥 Liens de Téléchargement et Ressources

## 🔧 Logiciels Requis

### 1. Java Development Kit (JDK)

**Version recommandée** : JDK 11 ou supérieur

#### OpenJDK (Gratuit)
- **Adoptium (Eclipse Temurin)** : https://adoptium.net/
  - Télécharger : JDK 11 LTS ou JDK 17 LTS
  - Windows x64 : `.msi` installer

#### Oracle JDK
- **Oracle JDK** : https://www.oracle.com/java/technologies/downloads/
  - Nécessite un compte Oracle

**Vérification après installation :**
```cmd
java -version
javac -version
```

---

### 2. Apache Maven

**Version recommandée** : Maven 3.8+

#### Téléchargement
- **Site officiel** : https://maven.apache.org/download.cgi
- **Fichier** : `apache-maven-3.9.6-bin.zip` (ou version plus récente)

#### Installation Windows
1. Extraire le ZIP dans `C:\Program Files\Apache\maven`
2. Ajouter au PATH : `C:\Program Files\Apache\maven\bin`
3. Créer variable `MAVEN_HOME` : `C:\Program Files\Apache\maven`

**Vérification :**
```cmd
mvn -version
```

---

### 3. PostgreSQL

**Version recommandée** : PostgreSQL 14+ ou 15+

#### Téléchargement
- **Site officiel** : https://www.postgresql.org/download/windows/
- **Installateur EDB** : https://www.enterprisedb.com/downloads/postgres-postgresql-downloads
  - Télécharger : PostgreSQL 15.x Windows x86-64

#### Installation
1. Exécuter l'installateur
2. Définir un mot de passe pour l'utilisateur `postgres`
3. Port par défaut : `5432`
4. Installer pgAdmin 4 (inclus)

**Vérification :**
```cmd
psql --version
```

---

### 4. WildFly Application Server

**Version recommandée** : WildFly 27+ ou WildFly 30+

#### Téléchargement
- **Site officiel** : https://www.wildfly.org/downloads/
- **Version stable** : https://github.com/wildfly/wildfly/releases
  - Télécharger : `wildfly-30.0.1.Final.zip` (ou version plus récente)

#### Installation Windows
1. Extraire le ZIP dans `C:\wildfly`
2. Pas besoin de configuration PATH

**Structure :**
```
C:\wildfly\
├── bin\
│   ├── standalone.bat
│   └── jboss-cli.bat
├── standalone\
│   ├── deployments\
│   └── configuration\
└── modules\
```

**Démarrage :**
```cmd
cd C:\wildfly\bin
standalone.bat
```

---

### 5. PostgreSQL JDBC Driver

**Version recommandée** : 42.7.3 (ou la plus récente)

#### Téléchargement Direct
- **Site officiel** : https://jdbc.postgresql.org/download/
- **Lien direct** : https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

#### Versions disponibles
| Version | Lien |
|---------|------|
| **42.7.3** | https://jdbc.postgresql.org/download/postgresql-42.7.3.jar |
| **42.7.2** | https://jdbc.postgresql.org/download/postgresql-42.7.2.jar |
| **42.6.0** | https://jdbc.postgresql.org/download/postgresql-42.6.0.jar |

**Où sauvegarder :**
- Créer le dossier : `C:\temp\`
- Sauvegarder : `C:\temp\postgresql-42.7.3.jar`

---

## 🛠️ Outils Optionnels (Recommandés)

### 1. pgAdmin 4 (Interface PostgreSQL)

**Inclus avec PostgreSQL** ou téléchargement séparé :
- **Site officiel** : https://www.pgadmin.org/download/

### 2. DBeaver (Client SQL Universel)

Alternative à pgAdmin, plus moderne :
- **Site officiel** : https://dbeaver.io/download/
- **Version** : Community Edition (gratuite)

### 3. IntelliJ IDEA (IDE Java)

Pour le développement Java :
- **Community Edition** (gratuite) : https://www.jetbrains.com/idea/download/
- **Ultimate Edition** (payante, gratuite pour étudiants) : https://www.jetbrains.com/student/

### 4. Visual Studio Code

Éditeur léger avec extensions Java :
- **Site officiel** : https://code.visualstudio.com/
- **Extensions** :
  - Extension Pack for Java
  - Maven for Java

---

## 📚 Documentation Officielle

### Java EE / Jakarta EE
- **Jakarta EE** : https://jakarta.ee/
- **JPA Specification** : https://jakarta.ee/specifications/persistence/
- **EJB Specification** : https://jakarta.ee/specifications/enterprise-beans/

### WildFly
- **Documentation** : https://docs.wildfly.org/
- **Getting Started** : https://docs.wildfly.org/30/Getting_Started_Guide.html
- **Admin Guide** : https://docs.wildfly.org/30/Admin_Guide.html

### PostgreSQL
- **Documentation** : https://www.postgresql.org/docs/
- **JDBC Driver** : https://jdbc.postgresql.org/documentation/

### Hibernate
- **Documentation** : https://hibernate.org/orm/documentation/
- **Getting Started** : https://hibernate.org/orm/documentation/getting-started/

### Maven
- **Documentation** : https://maven.apache.org/guides/
- **POM Reference** : https://maven.apache.org/pom.html

---

## 🎓 Tutoriels et Guides

### WildFly + PostgreSQL
- **WildFly DataSource** : https://docs.wildfly.org/30/Admin_Guide.html#DataSource
- **PostgreSQL on WildFly** : https://www.mastertheboss.com/jbossas/jboss-datasource/how-to-configure-a-postgresql-datasource-with-wildfly/

### JPA / Hibernate
- **JPA Tutorial** : https://www.baeldung.com/jpa-hibernate-guide
- **Hibernate Getting Started** : https://hibernate.org/orm/documentation/getting-started/

### Maven Multi-Module
- **Maven Multi-Module** : https://maven.apache.org/guides/mini/guide-multiple-modules.html
- **Maven EAR Plugin** : https://maven.apache.org/plugins/maven-ear-plugin/

---

## 🔍 Vérification des Versions

Après installation, vérifiez que tout est correctement installé :

```cmd
REM Java
java -version
javac -version

REM Maven
mvn -version

REM PostgreSQL
psql --version

REM Variables d'environnement
echo %JAVA_HOME%
echo %MAVEN_HOME%
echo %PATH%
```

**Résultat attendu :**
```
java version "11.0.x" ou supérieur
Apache Maven 3.8.x ou supérieur
psql (PostgreSQL) 14.x ou supérieur
```

---

## 📦 Checklist de Téléchargement

Avant de commencer le déploiement, assurez-vous d'avoir :

- [ ] **JDK 11+** installé et configuré
- [ ] **Maven 3.8+** installé et dans le PATH
- [ ] **PostgreSQL 14+** installé et démarré
- [ ] **WildFly 27+** extrait dans `C:\wildfly`
- [ ] **postgresql-42.7.3.jar** téléchargé dans `C:\temp\`
- [ ] **pgAdmin** ou **DBeaver** installé (optionnel)

---

## 🆘 Support et Aide

### Forums et Communautés
- **Stack Overflow** : https://stackoverflow.com/
  - Tags : `wildfly`, `jpa`, `postgresql`, `maven`
- **WildFly Forum** : https://groups.google.com/g/wildfly
- **PostgreSQL Mailing Lists** : https://www.postgresql.org/list/

### Documentation du Projet
- **Guide de déploiement** : `WILDFLY-SETUP-GUIDE.md`
- **Checklist** : `DEPLOYMENT-CHECKLIST.md`
- **Scripts** : Dossier `wildfly-scripts/`

---

## 📝 Notes Importantes

### Compatibilité des Versions

| Composant | Version Minimale | Version Recommandée |
|-----------|------------------|---------------------|
| Java | JDK 11 | JDK 17 LTS |
| Maven | 3.6.0 | 3.9.6 |
| PostgreSQL | 12 | 15 |
| WildFly | 26 | 30 |
| JDBC Driver | 42.5.0 | 42.7.3 |

### Taille des Téléchargements

| Fichier | Taille Approximative |
|---------|---------------------|
| JDK 17 | ~170 MB |
| Maven | ~9 MB |
| PostgreSQL | ~250 MB |
| WildFly | ~200 MB |
| JDBC Driver | ~1 MB |

**Total** : ~630 MB

---

**Bon téléchargement ! 📥**
