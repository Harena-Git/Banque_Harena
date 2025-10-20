# 🚀 Déploiement Bank Application - Guide Complet

## 📌 Résumé du Problème et Solution

### ❌ Problème
Les nouvelles pages HTML ne s'affichent pas car le projet utilise une **architecture EAR** et n'a pas été recompilé depuis la racine.

### ✅ Solution
Exécuter `redeploy-bank.bat` à la racine du projet pour recompiler et redéployer le **Bank.ear** complet.

---

## 🎯 Action Immédiate

### **Double-cliquez sur ce fichier :**
```
d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\redeploy-bank.bat
```

**Ou en ligne de commande :**
```bash
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
mvn clean package
copy Bank-ear\target\Bank.ear D:\wildfly\standalone\deployments\
echo. > D:\wildfly\standalone\deployments\Bank.ear.dodeploy
```

**Temps estimé :** 2-3 minutes

---

## 📁 Structure du Projet

```
d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\
│
├── pom.xml                    ← POM parent (compile tout)
│
├── customer\                  ← Module EJB
│   ├── src\
│   └── pom.xml
│
├── current\                   ← Module EJB
│   ├── src\
│   └── pom.xml
│
├── loan\                      ← Module EJB
│   ├── src\
│   └── pom.xml
│
├── centralizer\               ← Module WAR (interface web)
│   ├── src\
│   │   └── main\
│   │       └── webapp\
│   │           ├── index.html
│   │           └── modules\
│   │               ├── current\
│   │               │   └── list.html ✅ NOUVEAU
│   │               ├── deposit\
│   │               │   ├── list.html ✅ NOUVEAU
│   │               │   ├── new.html ✅ NOUVEAU
│   │               │   ├── deposit.html ✅ NOUVEAU
│   │               │   ├── withdraw.html ✅ NOUVEAU
│   │               │   └── transactions.html ✅ NOUVEAU
│   │               └── loan\
│   │                   └── list.html ✅ NOUVEAU
│   └── pom.xml
│
└── Bank-ear\                  ← Module EAR (package final)
    ├── pom.xml
    └── target\
        └── Bank.ear ⭐ FICHIER À DÉPLOYER SUR WILDFLY
```

---

## 🔄 Processus de Build Maven

```
mvn clean package (à la racine)
        │
        ├─→ 1. Compile customer.jar
        ├─→ 2. Compile current.jar
        ├─→ 3. Compile loan.jar
        ├─→ 4. Compile centralizer.war (avec les nouveaux HTML)
        └─→ 5. Package Bank.ear (contient tout)
                    │
                    └─→ Bank-ear/target/Bank.ear
                            │
                            └─→ Copier vers D:\wildfly\standalone\deployments\
                                    │
                                    └─→ WildFly détecte et déploie
```

---

## ✅ Fichiers Créés (7 pages HTML)

| Module | Fichier | Taille | Fonctionnalité |
|--------|---------|--------|----------------|
| **Current** | `list.html` | 8 KB | Liste des comptes courants avec recherche |
| **Deposit** | `list.html` | 7 KB | Liste des comptes d'épargne |
| **Deposit** | `new.html` | 4 KB | Créer un compte d'épargne |
| **Deposit** | `deposit.html` | 5 KB | Effectuer un dépôt |
| **Deposit** | `withdraw.html` | 7 KB | Effectuer un retrait (max 3/mois) |
| **Deposit** | `transactions.html` | 6 KB | Historique des transactions |
| **Loan** | `list.html` | 9 KB | Liste des prêts avec filtres |

**Total : 7 fichiers, 46 KB de code**

---

## 🛠️ Scripts d'Aide Créés

| Fichier | Description |
|---------|-------------|
| `redeploy-bank.bat` ⭐ | Script automatique de redéploiement |
| `verify-files.bat` | Vérifie que tous les fichiers sont présents |
| `SOLUTION-EAR.md` | Explication détaillée du problème EAR |
| `LIRE-MOI-REDEPLOY.txt` | Instructions simples en français |
| `REDEPLOY-GUIDE.md` | Guide complet avec dépannage |
| `README-DEPLOIEMENT.md` | Ce fichier |

---

## 📋 Checklist de Déploiement

### Avant de commencer :
- [ ] WildFly est démarré (`D:\wildfly\bin\standalone.bat`)
- [ ] PostgreSQL est démarré
- [ ] Maven est installé (`mvn -version`)
- [ ] Java JDK 21 est installé (`java -version`)

### Étapes de déploiement :
1. [ ] Ouvrir un terminal
2. [ ] Aller à la racine : `cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena`
3. [ ] Exécuter : `redeploy-bank.bat` ou `mvn clean package`
4. [ ] Attendre "BUILD SUCCESS" (1-2 minutes)
5. [ ] Vérifier que `Bank-ear\target\Bank.ear` existe
6. [ ] Copier vers WildFly (automatique avec le script)
7. [ ] Attendre 10-15 secondes
8. [ ] Vérifier `D:\wildfly\standalone\deployments\Bank.ear.deployed`
9. [ ] Ouvrir le navigateur : `http://localhost:8080/centralizer`
10. [ ] Vider le cache (Ctrl+Shift+Delete)
11. [ ] Rafraîchir (Ctrl+F5)
12. [ ] Tester les menus

### Vérification :
- [ ] Current Account → List fonctionne
- [ ] Deposit Account → List fonctionne
- [ ] Deposit Account → New fonctionne
- [ ] Deposit Account → Deposit fonctionne
- [ ] Deposit Account → Withdraw fonctionne
- [ ] Deposit Account → Transactions fonctionne
- [ ] Loan → List fonctionne

---

## 🎯 URLs à Tester

Après le déploiement, ces URLs doivent fonctionner :

```
http://localhost:8080/centralizer/
http://localhost:8080/centralizer/modules/current/list.html
http://localhost:8080/centralizer/modules/deposit/list.html
http://localhost:8080/centralizer/modules/deposit/new.html
http://localhost:8080/centralizer/modules/loan/list.html
```

---

## 🐛 Dépannage Rapide

### Problème : "BUILD FAILURE"
**Solution :**
```bash
# Vérifier Maven
mvn -version

# Vérifier Java
java -version

# Nettoyer et recompiler
mvn clean
mvn package
```

### Problème : "Bank.ear.failed"
**Solution :**
1. Consulter les logs : `D:\wildfly\standalone\log\server.log`
2. Vérifier PostgreSQL est démarré
3. Vérifier la datasource WildFly

### Problème : Pages toujours vides
**Solution :**
1. Vider le cache du navigateur (Ctrl+Shift+Delete)
2. Vérifier que `Bank.ear.deployed` existe
3. Redémarrer WildFly
4. Tester l'URL directe : `http://localhost:8080/centralizer/modules/deposit/list.html`

### Problème : "Cannot find Bank.ear"
**Solution :**
```bash
# Vérifier que le build a réussi
dir Bank-ear\target\Bank.ear

# Si le fichier n'existe pas, recompiler
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
mvn clean package
```

---

## 📊 Temps de Déploiement

| Étape | Durée |
|-------|-------|
| Build Maven (tous les modules) | 1-2 minutes |
| Copie du Bank.ear | < 1 seconde |
| Déploiement WildFly | 10-15 secondes |
| **TOTAL** | **2-3 minutes** |

---

## 🎓 Points Importants à Retenir

### ✅ À FAIRE
- Compiler depuis la **racine** du projet
- Déployer le **Bank.ear** (pas le centralizer.war)
- Attendre le fichier `.deployed` avant de tester
- Vider le cache du navigateur après redéploiement

### ❌ À NE PAS FAIRE
- Compiler uniquement le module centralizer
- Copier seulement le centralizer.war
- Tester immédiatement sans attendre le déploiement
- Oublier de vider le cache du navigateur

---

## 🚀 Commande Rapide (Copier-Coller)

```bash
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena && mvn clean package && copy Bank-ear\target\Bank.ear D:\wildfly\standalone\deployments\ && echo. > D:\wildfly\standalone\deployments\Bank.ear.dodeploy && echo Deploiement lance ! Attendez 10-15 secondes...
```

---

## 📞 Support

Si le problème persiste après avoir suivi ce guide :

1. **Vérifier les logs WildFly** : `D:\wildfly\standalone\log\server.log`
2. **Vérifier la console du navigateur** : F12 → Console
3. **Exécuter le script de vérification** : `verify-files.bat`
4. **Consulter les guides détaillés** :
   - `SOLUTION-EAR.md` - Explication du problème
   - `REDEPLOY-GUIDE.md` - Guide complet
   - `LIRE-MOI-REDEPLOY.txt` - Instructions simples

---

## ✅ Résultat Attendu

Après le redéploiement réussi, vous aurez une interface **100% fonctionnelle** avec :

### 💳 Current Account
- ✅ Liste complète avec recherche
- ✅ Actions rapides (Dépôt, Retrait, Transactions)
- ✅ Indicateurs visuels (solde positif/négatif)

### 🏦 Deposit Account
- ✅ Liste avec compteur de retraits mensuels
- ✅ Création de compte avec taux d'intérêt
- ✅ Dépôt illimité
- ✅ Retrait avec limite (3 max/mois)
- ✅ Historique complet des transactions

### 💰 Loan
- ✅ Liste avec filtres par statut
- ✅ Paiements rapides
- ✅ Historique des remboursements
- ✅ Annulation de prêt

---

## 🎉 Prêt à Déployer !

**Exécutez maintenant :**
```
redeploy-bank.bat
```

**Ou en ligne de commande :**
```bash
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena
mvn clean package
```

**Dans 2-3 minutes, votre interface sera opérationnelle ! 🚀**
