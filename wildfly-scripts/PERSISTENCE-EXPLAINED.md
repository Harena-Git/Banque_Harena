# 📚 Explication des Fichiers persistence.xml

## Vue d'ensemble

Chaque module Java (current, loan, customer) possède un fichier `persistence.xml` qui configure la connexion à la base de données via JPA/Hibernate.

---

## 🔍 Anatomie d'un fichier persistence.xml

### Exemple : current/src/main/resources/META-INF/persistence.xml

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_2.xsd"
             version="3.2">
    <persistence-unit name="default" transaction-type="JTA">
        <jta-data-source>java:/BankDS</jta-data-source>
        <class>org.bank.current.entity.CurrentAccount</class>
        <class>org.bank.current.entity.CurrentTransaction</class>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.hbm2dll.auto" value="update"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## 📖 Explication ligne par ligne

### 1. **Persistence Unit**
```xml
<persistence-unit name="default" transaction-type="JTA">
```
- **name="default"** : Nom de l'unité de persistance
- **transaction-type="JTA"** : Utilise les transactions gérées par WildFly (recommandé pour EJB)

### 2. **DataSource JNDI** ⭐ IMPORTANT
```xml
<jta-data-source>java:/BankDS</jta-data-source>
```
- **java:/BankDS** : C'est le JNDI du DataSource configuré dans WildFly
- ⚠️ Ce nom DOIT correspondre exactement au DataSource créé dans WildFly
- C'est grâce à ce JNDI que l'application se connecte à PostgreSQL

### 3. **Classes d'entités**
```xml
<class>org.bank.current.entity.CurrentAccount</class>
<class>org.bank.current.entity.CurrentTransaction</class>
```
- Liste des entités JPA gérées par ce module
- Hibernate créera automatiquement les tables correspondantes

### 4. **Propriétés Hibernate**

#### a) Génération du schéma
```xml
<property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
```
**Valeurs possibles :**
- `drop-and-create` : Supprime et recrée les tables à chaque déploiement (⚠️ PERTE DE DONNÉES)
- `create` : Crée les tables si elles n'existent pas
- `none` : Ne fait rien (pour production)

**Recommandation :**
- **Développement** : `drop-and-create` ou `create`
- **Production** : `none` (gérer les migrations avec Flyway/Liquibase)

#### b) Auto DDL
```xml
<property name="hibernate.hbm2dll.auto" value="update"/>
```
**Valeurs possibles :**
- `create` : Crée le schéma au démarrage
- `update` : Met à jour le schéma existant (⚠️ peut causer des problèmes)
- `validate` : Vérifie que le schéma correspond aux entités
- `none` : Aucune action

**Recommandation :**
- **Développement** : `update` ou `create`
- **Production** : `validate` ou `none`

#### c) Affichage SQL
```xml
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
```
- **show_sql** : Affiche les requêtes SQL dans les logs
- **format_sql** : Formate les requêtes pour une meilleure lisibilité

**Recommandation :**
- **Développement** : `true` (pour déboguer)
- **Production** : `false` (pour les performances)

#### d) Dialecte
```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
```
- Indique à Hibernate d'utiliser la syntaxe SQL de PostgreSQL
- ⚠️ Ne pas modifier si vous utilisez PostgreSQL

---

## 🔗 Lien avec la Configuration WildFly

### Chaîne de connexion complète :

```
persistence.xml (java:/BankDS)
        ↓
WildFly DataSource (BankDS)
        ↓
PostgreSQL Driver (org.postgresql)
        ↓
Base de données PostgreSQL (s5-bank)
```

### Étapes de configuration :

1. **Dans WildFly** : Créer le DataSource `BankDS` avec le JNDI `java:/BankDS`
2. **Dans persistence.xml** : Référencer ce JNDI : `<jta-data-source>java:/BankDS</jta-data-source>`
3. **Au déploiement** : WildFly injecte automatiquement la connexion

---

## 📊 Comparaison des modules

| Module | JNDI | Entités | Action Schéma |
|--------|------|---------|---------------|
| **current** | `java:/BankDS` | CurrentAccount, CurrentTransaction | `drop-and-create` |
| **loan** | `java:/BankDS` | Loan, LoanPayment | `create` |
| **customer** | `java:/BankDS` | Customer | `create` |

**Note** : Tous utilisent le même DataSource (`java:/BankDS`), donc la même base de données.

---

## ⚠️ Points d'attention

### 1. **Conflit de schéma**
Si `current` utilise `drop-and-create`, il supprimera TOUTES les tables à chaque redéploiement, y compris celles des autres modules !

**Solution** : Utiliser `create` ou `update` pour tous les modules en développement.

### 2. **JNDI incorrect**
Si le JNDI ne correspond pas au DataSource WildFly, vous aurez :
```
ERROR: JNDI name java:/BankDS not found
```

**Solution** : Vérifier que le DataSource est bien créé dans WildFly avec le bon JNDI.

### 3. **Dialecte incorrect**
Si vous utilisez un autre dialecte que PostgreSQL :
```
ERROR: Syntax error in SQL statement
```

**Solution** : Utiliser `org.hibernate.dialect.PostgreSQLDialect`.

---

## 🛠️ Configuration recommandée pour la production

```xml
<persistence-unit name="default" transaction-type="JTA">
    <jta-data-source>java:/BankDS</jta-data-source>
    <class>org.bank.current.entity.CurrentAccount</class>
    <class>org.bank.current.entity.CurrentTransaction</class>
    <properties>
        <!-- NE PAS générer automatiquement le schéma -->
        <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
        
        <!-- Valider que le schéma correspond aux entités -->
        <property name="hibernate.hbm2dll.auto" value="validate"/>
        
        <!-- Désactiver l'affichage SQL -->
        <property name="hibernate.show_sql" value="false"/>
        <property name="hibernate.format_sql" value="false"/>
        
        <!-- Dialecte PostgreSQL -->
        <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
    </properties>
</persistence-unit>
```

---

## 🔍 Vérification

### Après déploiement, vérifier les tables créées :

```sql
-- Se connecter à la base
\c s5-bank

-- Lister les tables
\dt

-- Résultat attendu :
-- customers
-- current_accounts
-- current_transactions
-- loans
-- loan_payments
```

### Vérifier les logs WildFly :

Chercher dans `C:\wildfly\standalone\log\server.log` :

```
INFO  [org.hibernate.tool.hbm2ddl.SchemaExport] (ServerService Thread Pool) HHH000227: Running hbm2ddl schema export
INFO  [org.hibernate.tool.hbm2ddl.SchemaExport] (ServerService Thread Pool) HHH000230: Schema export complete
```

---

## 📝 Résumé

| Élément | Valeur | Rôle |
|---------|--------|------|
| **JNDI** | `java:/BankDS` | Référence au DataSource WildFly |
| **DataSource** | `BankDS` | Configuration de connexion dans WildFly |
| **Driver** | `org.postgresql` | Module PostgreSQL dans WildFly |
| **Base** | `s5-bank` | Base de données PostgreSQL |
| **Dialecte** | `PostgreSQLDialect` | Syntaxe SQL pour PostgreSQL |

---

**Pour plus d'informations :**
- Documentation JPA : https://jakarta.ee/specifications/persistence/
- Documentation Hibernate : https://hibernate.org/orm/documentation/
- Documentation WildFly : https://docs.wildfly.org/
