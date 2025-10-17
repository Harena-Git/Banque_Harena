# 📑 Index de la Documentation WildFly

Bienvenue dans la documentation de déploiement de Bank Application sur WildFly !

---

## 🚀 Par où commencer ?

### Pour un déploiement rapide (recommandé)
1. **Lisez** : `QUICK-START.txt` (guide visuel rapide)
2. **Téléchargez** : Voir `DOWNLOAD-LINKS.md`
3. **Exécutez** : `setup-wildfly.bat`

### Pour comprendre en détail
1. **Lisez** : `../DEPLOYMENT-CHECKLIST.md` (checklist complète)
2. **Lisez** : `../WILDFLY-SETUP-GUIDE.md` (guide détaillé)
3. **Configurez** : Suivez les étapes manuellement

---

## 📚 Documentation Disponible

### 📋 Guides Principaux

| Fichier | Description | Niveau |
|---------|-------------|--------|
| **QUICK-START.txt** | Guide visuel rapide (5 étapes) | ⭐ Débutant |
| **DEPLOYMENT-CHECKLIST.md** | Checklist complète avec cases à cocher | ⭐⭐ Intermédiaire |
| **WILDFLY-SETUP-GUIDE.md** | Guide détaillé étape par étape | ⭐⭐⭐ Complet |

### 🔧 Scripts d'Automatisation

| Fichier | Description | Usage |
|---------|-------------|-------|
| **setup-wildfly.bat** | Script automatique complet | Exécuter directement |
| **configure-driver.cli** | Configuration du driver seul | Via JBoss CLI |
| **configure-datasource.cli** | Configuration du datasource seul | Via JBoss CLI |
| **create-database.sql** | Création de la base PostgreSQL | Via psql/pgAdmin |

### 📖 Documentation Technique

| Fichier | Description | Public |
|---------|-------------|--------|
| **PERSISTENCE-EXPLAINED.md** | Explication des fichiers persistence.xml | Développeurs |
| **DOWNLOAD-LINKS.md** | Liens de téléchargement et ressources | Tous |
| **README.md** | Vue d'ensemble des scripts | Tous |

---

## 🎯 Guides par Scénario

### Scénario 1 : Premier déploiement
```
1. DOWNLOAD-LINKS.md        → Télécharger les prérequis
2. QUICK-START.txt          → Vue d'ensemble rapide
3. setup-wildfly.bat        → Exécuter le script
4. DEPLOYMENT-CHECKLIST.md  → Vérifier que tout fonctionne
```

### Scénario 2 : Déploiement manuel
```
1. WILDFLY-SETUP-GUIDE.md   → Lire le guide complet
2. create-database.sql      → Créer la base de données
3. configure-driver.cli     → Configurer le driver
4. configure-datasource.cli → Configurer le datasource
5. mvn clean install        → Compiler le projet
6. Copier Bank.ear          → Déployer l'application
```

### Scénario 3 : Comprendre la configuration
```
1. PERSISTENCE-EXPLAINED.md → Comprendre persistence.xml
2. WILDFLY-SETUP-GUIDE.md   → Comprendre WildFly
3. DOWNLOAD-LINKS.md        → Documentation officielle
```

### Scénario 4 : Dépannage
```
1. DEPLOYMENT-CHECKLIST.md  → Section "Dépannage"
2. WILDFLY-SETUP-GUIDE.md   → Section "Dépannage"
3. Logs WildFly             → C:\wildfly\standalone\log\server.log
```

---

## 📂 Structure des Fichiers

```
Banque_Harena/
│
├── README.md                          # Vue d'ensemble du projet
├── DEPLOYMENT-CHECKLIST.md            # ⭐ Checklist de déploiement
├── WILDFLY-SETUP-GUIDE.md             # ⭐ Guide complet
│
├── wildfly-scripts/                   # Scripts de configuration
│   ├── INDEX.md                       # ⭐ Ce fichier
│   ├── README.md                      # Vue d'ensemble des scripts
│   ├── QUICK-START.txt                # ⭐ Démarrage rapide
│   ├── DOWNLOAD-LINKS.md              # Liens de téléchargement
│   ├── PERSISTENCE-EXPLAINED.md       # Explication technique
│   │
│   ├── setup-wildfly.bat              # ⭐ Script automatique
│   ├── configure-driver.cli           # Script driver
│   ├── configure-datasource.cli       # Script datasource
│   └── create-database.sql            # Script base de données
│
├── pom.xml                            # Maven parent
├── Bank-ear/                          # Module EAR
├── current/                           # Module current accounts
├── loan/                              # Module loans
├── customer/                          # Module customers
└── centralizer/                       # Module web
```

---

## 🔍 Recherche Rapide

### Je veux...

#### ...déployer rapidement
→ `QUICK-START.txt` + `setup-wildfly.bat`

#### ...comprendre la configuration
→ `WILDFLY-SETUP-GUIDE.md` + `PERSISTENCE-EXPLAINED.md`

#### ...télécharger les logiciels
→ `DOWNLOAD-LINKS.md`

#### ...résoudre un problème
→ `DEPLOYMENT-CHECKLIST.md` (section Dépannage)

#### ...configurer manuellement
→ `WILDFLY-SETUP-GUIDE.md` (section Déploiement Manuel)

#### ...comprendre persistence.xml
→ `PERSISTENCE-EXPLAINED.md`

#### ...automatiser le déploiement
→ `setup-wildfly.bat`

#### ...créer la base de données
→ `create-database.sql`

---

## ⚡ Commandes Rapides

### Déploiement automatique
```cmd
wildfly-scripts\setup-wildfly.bat
```

### Déploiement manuel
```cmd
# 1. Démarrer WildFly
cd C:\wildfly\bin
standalone.bat

# 2. Configurer (nouveau terminal)
jboss-cli.bat --connect --file=wildfly-scripts\configure-driver.cli
jboss-cli.bat --connect --file=wildfly-scripts\configure-datasource.cli

# 3. Compiler et déployer
mvn clean install
copy Bank-ear\target\Bank.ear C:\wildfly\standalone\deployments\
```

### Vérification
```cmd
# Tester la connexion DB
jboss-cli.bat --connect --command="/subsystem=datasources/data-source=BankDS:test-connection-in-pool"

# Voir les logs
type C:\wildfly\standalone\log\server.log
```

---

## 🎓 Niveau de Difficulté

| Document | Difficulté | Temps de Lecture |
|----------|-----------|------------------|
| QUICK-START.txt | ⭐ Facile | 5 min |
| README.md | ⭐ Facile | 5 min |
| DEPLOYMENT-CHECKLIST.md | ⭐⭐ Moyen | 10 min |
| WILDFLY-SETUP-GUIDE.md | ⭐⭐⭐ Avancé | 20 min |
| PERSISTENCE-EXPLAINED.md | ⭐⭐⭐ Avancé | 15 min |
| DOWNLOAD-LINKS.md | ⭐ Facile | 10 min |

---

## 📞 Besoin d'Aide ?

### Ordre de consultation recommandé :
1. Consultez la section **Dépannage** dans `DEPLOYMENT-CHECKLIST.md`
2. Consultez la section **Dépannage** dans `WILDFLY-SETUP-GUIDE.md`
3. Vérifiez les logs : `C:\wildfly\standalone\log\server.log`
4. Consultez la documentation officielle dans `DOWNLOAD-LINKS.md`

### Erreurs Courantes :
- **"Driver not found"** → `WILDFLY-SETUP-GUIDE.md` (Étape 3)
- **"Connection refused"** → `DEPLOYMENT-CHECKLIST.md` (Dépannage)
- **"JNDI not found"** → `PERSISTENCE-EXPLAINED.md`
- **"Build failure"** → Vérifier Java et Maven

---

## ✅ Checklist Rapide

Avant de commencer :
- [ ] Java JDK 11+ installé
- [ ] Maven installé
- [ ] PostgreSQL installé et démarré
- [ ] WildFly téléchargé et extrait
- [ ] Driver PostgreSQL téléchargé
- [ ] Base de données `s5-bank` créée

---

## 🌟 Recommandations

### Pour les débutants
1. Commencez par `QUICK-START.txt`
2. Utilisez `setup-wildfly.bat`
3. Consultez `DEPLOYMENT-CHECKLIST.md` en cas de problème

### Pour les utilisateurs avancés
1. Lisez `WILDFLY-SETUP-GUIDE.md`
2. Configurez manuellement avec les scripts CLI
3. Consultez `PERSISTENCE-EXPLAINED.md` pour optimiser

### Pour les développeurs
1. Étudiez `PERSISTENCE-EXPLAINED.md`
2. Consultez `DOWNLOAD-LINKS.md` pour la documentation
3. Personnalisez les scripts selon vos besoins

---

**Bon déploiement ! 🚀**

*Pour toute question, consultez d'abord la documentation appropriée ci-dessus.*
