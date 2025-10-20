# 🔄 Guide de Redéploiement - Bank Application (EAR)

## ❗ Problème Rencontré

Les nouvelles pages HTML créées ne s'affichent pas dans l'interface web car :
- ✅ Les fichiers existent dans `centralizer/src/main/webapp/modules/`
- ❌ Mais ils ne sont pas dans le **Bank.ear** déployé sur WildFly
- ❌ Le projet complet n'a pas été recompilé après la création des fichiers

## 🎯 Architecture du Projet

Ce projet utilise une architecture **EAR (Enterprise Archive)** :

```
Bank-parent (pom.xml racine)
├── customer (EJB)
├── current (EJB)
├── loan (EJB)
├── centralizer (WAR) ← Contient les pages HTML
└── Bank-ear (EAR) ← Package final déployé sur WildFly
```

**Important** : Il faut compiler depuis la **racine** pour reconstruire le **Bank.ear** complet !

---

## 🚀 Solution Rapide

### Option 1 : Utiliser le Script Automatique (RECOMMANDÉ)

1. **Double-cliquer sur** `redeploy-bank.bat` à la **racine** du projet
2. Attendre la fin du build Maven (tous les modules)
3. Le Bank.ear sera automatiquement copié dans WildFly
4. Attendre **10-15 secondes** que WildFly redéploie (EAR = plus long)
5. Rafraîchir votre navigateur (Ctrl+F5)

### Option 2 : Commandes Manuelles

```bash
# 1. Aller à la RACINE du projet (pas dans centralizer !)
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena

# 2. Compiler TOUS les modules
mvn clean package

# 3. Copier le Bank.ear vers WildFly
copy Bank-ear\target\Bank.ear D:\wildfly\standalone\deployments\

# 4. Créer le marker de déploiement
echo. > D:\wildfly\standalone\deployments\Bank.ear.dodeploy
```

---

## 📋 Vérification

### 1. Vérifier que le Bank.ear est créé
```
d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\Bank-ear\target\Bank.ear
```
✅ Ce fichier doit exister et avoir une date/heure récente (plusieurs MB)

### 2. Vérifier que le centralizer.war est dans le EAR
Le centralizer.war doit être inclus dans le Bank.ear avec les nouveaux fichiers HTML.

### 3. Vérifier le déploiement WildFly
```
D:\wildfly\standalone\deployments\Bank.ear
D:\wildfly\standalone\deployments\Bank.ear.deployed
```
✅ Le fichier `.deployed` doit apparaître après 10-15 secondes

### 4. Vérifier les logs WildFly
Ouvrir : `D:\wildfly\standalone\log\server.log`

Chercher :
```
Deployed "Bank.ear" (runtime-name : "Bank.ear")
```

### 4. Tester dans le navigateur
```
http://localhost:8080/centralizer
```

Cliquer sur les menus :
- ✅ Current Account → List (doit afficher la nouvelle page)
- ✅ Deposit Account → List (doit afficher la nouvelle page)
- ✅ Deposit Account → New (doit afficher le formulaire)
- ✅ Loan → List (doit afficher la nouvelle page)

---

## 🐛 Dépannage

### Erreur : "Maven build failed"
**Cause** : Problème de compilation Java

**Solution** :
```bash
# Vérifier les erreurs
mvn clean package

# Lire les messages d'erreur
# Corriger les erreurs de code si nécessaire
```

### Erreur : "Failed to copy WAR file"
**Cause** : Chemin WildFly incorrect

**Solution** :
1. Vérifier que WildFly est installé à `C:\wildfly\`
2. Si différent, modifier le chemin dans `redeploy.bat`

### Les pages sont toujours vides
**Cause** : Cache du navigateur ou déploiement incomplet

**Solution** :
1. Vider le cache du navigateur (Ctrl + Shift + Delete)
2. Redémarrer WildFly :
   ```bash
   # Arrêter WildFly (Ctrl+C dans le terminal)
   # Redémarrer
   C:\wildfly\bin\standalone.bat
   ```
3. Attendre le démarrage complet
4. Redéployer avec `redeploy.bat`

### Erreur 404 sur les pages
**Cause** : Les fichiers HTML ne sont pas dans le WAR

**Solution** :
1. Vérifier que les fichiers existent dans `src/main/webapp/modules/`
2. Recompiler : `mvn clean package`
3. Vérifier le contenu du WAR :
   ```bash
   # Extraire le WAR pour vérifier
   jar -tf target\centralizer.war | findstr modules
   ```
   Vous devez voir :
   ```
   modules/current/list.html
   modules/deposit/list.html
   modules/deposit/new.html
   etc.
   ```

---

## 📝 Checklist de Redéploiement

Avant de tester, vérifiez :

- [ ] WildFly est démarré (`standalone.bat`)
- [ ] Le projet a été compilé (`mvn clean package`)
- [ ] Le WAR existe dans `target/centralizer.war`
- [ ] Le WAR a été copié dans `C:\wildfly\standalone\deployments\`
- [ ] Le fichier `.deployed` est apparu (attendre 5-10 secondes)
- [ ] Le navigateur a été rafraîchi (F5 ou Ctrl+F5)
- [ ] Le cache du navigateur a été vidé si nécessaire

---

## 🎯 Workflow de Développement

À chaque fois que vous modifiez un fichier HTML, CSS ou Java :

1. **Sauvegarder** le fichier
2. **Recompiler** : `mvn clean package` OU double-clic sur `redeploy.bat`
3. **Attendre** le redéploiement (5-10 secondes)
4. **Rafraîchir** le navigateur (F5)

---

## 🔍 Vérification Rapide des Fichiers

### Fichiers qui doivent exister dans `src/main/webapp/modules/` :

#### Current Account
- [x] `current/list.html` ✅ NOUVEAU
- [x] `current/new.html`
- [x] `current/deposit.html`
- [x] `current/withdraw.html`
- [x] `current/transactions.html`

#### Deposit Account
- [x] `deposit/list.html` ✅ NOUVEAU
- [x] `deposit/new.html` ✅ NOUVEAU
- [x] `deposit/deposit.html` ✅ NOUVEAU
- [x] `deposit/withdraw.html` ✅ NOUVEAU
- [x] `deposit/transactions.html` ✅ NOUVEAU

#### Loan
- [x] `loan/list.html` ✅ NOUVEAU
- [x] `loan/new.html`
- [x] `loan/payment.html`
- [x] `loan/active.html`

#### Customer
- [x] `customer/list.html`
- [x] `customer/new.html`
- [x] `customer/search.html`

---

## ✅ Après le Redéploiement

Vous devriez pouvoir :
1. ✅ Voir la liste des comptes courants avec recherche et actions
2. ✅ Créer un nouveau compte d'épargne
3. ✅ Faire des dépôts sur les comptes d'épargne
4. ✅ Faire des retraits (max 3 par mois)
5. ✅ Voir l'historique des transactions
6. ✅ Voir la liste des prêts avec filtres par statut

---

## 📞 Support

Si le problème persiste après le redéploiement :
1. Vérifier les logs WildFly : `C:\wildfly\standalone\log\server.log`
2. Vérifier la console du navigateur (F12 → Console)
3. Vérifier que tous les services sont démarrés :
   - WildFly (port 8080)
   - PostgreSQL (port 5432)
   - Deposit Service C# (port 5000) - pour les comptes d'épargne

---

## 🎉 Résultat Attendu

Après le redéploiement, l'interface doit être **100% fonctionnelle** avec toutes les nouvelles pages accessibles et opérationnelles.
