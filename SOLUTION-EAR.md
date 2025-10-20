# 🎯 Solution : Redéploiement Bank.ear

## ❗ Problème Identifié

Vous avez raison ! Le projet utilise une architecture **EAR (Enterprise Archive)**, pas un simple WAR.

### Architecture Actuelle

```
Bank-parent/
├── customer/          (EJB module)
├── current/           (EJB module)
├── loan/              (EJB module)
├── centralizer/       (WAR module) ← Contient les pages HTML
└── Bank-ear/          (EAR module) ← Package FINAL déployé
    └── target/
        └── Bank.ear   ← C'EST CE FICHIER qui doit être sur WildFly !
```

### Pourquoi les pages sont vides ?

1. ✅ Les fichiers HTML existent dans `centralizer/src/main/webapp/modules/`
2. ❌ Mais le **Bank.ear** déployé sur WildFly est **ancien**
3. ❌ Il ne contient pas le nouveau `centralizer.war` avec les pages HTML

---

## ✅ Solution : Recompiler TOUT depuis la Racine

### Étape 1 : Utiliser le Script Automatique

**Double-cliquez sur** :
```
d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena\redeploy-bank.bat
```

Ce script va :
1. ✅ Compiler **tous les modules** (customer, current, loan, centralizer)
2. ✅ Créer le **Bank.ear** avec tout dedans
3. ✅ Copier le Bank.ear vers `D:\wildfly\standalone\deployments\`
4. ✅ Créer le marker `.dodeploy` pour déclencher le redéploiement

### Étape 2 : Attendre le Déploiement

- ⏱️ Build Maven : **1-2 minutes**
- ⏱️ Déploiement WildFly : **10-15 secondes**
- ⏱️ Total : **2-3 minutes**

### Étape 3 : Vérifier

1. **Vérifier le fichier .deployed** :
   ```
   D:\wildfly\standalone\deployments\Bank.ear.deployed
   ```
   ✅ Ce fichier doit apparaître après 10-15 secondes

2. **Vérifier les logs WildFly** :
   ```
   D:\wildfly\standalone\log\server.log
   ```
   Chercher : `Deployed "Bank.ear"`

3. **Tester dans le navigateur** :
   ```
   http://localhost:8080/centralizer
   ```
   - Vider le cache (Ctrl+Shift+Delete)
   - Rafraîchir (Ctrl+F5)
   - Tester les menus

---

## 🔍 Commandes Manuelles (Alternative)

Si le script ne fonctionne pas :

```bash
# 1. Aller à la RACINE (important !)
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena

# 2. Compiler TOUS les modules
mvn clean package

# 3. Vérifier que Bank.ear existe
dir Bank-ear\target\Bank.ear

# 4. Copier vers WildFly
copy Bank-ear\target\Bank.ear D:\wildfly\standalone\deployments\

# 5. Créer le marker
echo. > D:\wildfly\standalone\deployments\Bank.ear.dodeploy

# 6. Attendre 10-15 secondes
timeout /t 15

# 7. Vérifier le déploiement
dir D:\wildfly\standalone\deployments\Bank.ear.*
```

---

## 📊 Ordre de Compilation Maven

Maven compile dans cet ordre (défini dans `pom.xml` racine) :

```
1. customer (EJB)    → customer.jar
2. current (EJB)     → current.jar
3. loan (EJB)        → loan.jar
4. centralizer (WAR) → centralizer.war (avec les nouveaux HTML)
5. Bank-ear (EAR)    → Bank.ear (contient tout)
```

**C'est pourquoi il faut compiler depuis la racine !**

---

## 🛠️ Scripts Créés pour Vous

### 1. `redeploy-bank.bat` ⭐ PRINCIPAL
Recompile et redéploie automatiquement le Bank.ear complet.

### 2. `verify-files.bat`
Vérifie que tous les fichiers HTML sont présents et que le déploiement est OK.

### 3. `LIRE-MOI-REDEPLOY.txt`
Instructions simples en français.

### 4. `REDEPLOY-GUIDE.md`
Guide détaillé avec dépannage.

---

## 🎯 Workflow Correct

### ❌ INCORRECT (ce que vous faisiez avant)
```bash
cd centralizer
mvn clean package
copy target\centralizer.war D:\wildfly\...
```
→ Ne met à jour que le WAR, pas le EAR déployé !

### ✅ CORRECT (ce qu'il faut faire)
```bash
cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena  # RACINE !
mvn clean package                                       # Compile TOUT
copy Bank-ear\target\Bank.ear D:\wildfly\...           # Copie le EAR
```
→ Met à jour le EAR complet avec tous les modules !

---

## 🔧 Vérification Rapide

### Avant de tester, vérifiez :

- [ ] WildFly est démarré
- [ ] Vous êtes à la **racine** du projet (pas dans centralizer/)
- [ ] Maven est installé (`mvn -version`)
- [ ] Java JDK 21 est installé (`java -version`)
- [ ] PostgreSQL est démarré

### Après le build, vérifiez :

- [ ] `Bank-ear\target\Bank.ear` existe (plusieurs MB)
- [ ] `D:\wildfly\standalone\deployments\Bank.ear` existe
- [ ] `D:\wildfly\standalone\deployments\Bank.ear.deployed` existe
- [ ] Logs WildFly montrent "Deployed Bank.ear"

---

## 🎉 Résultat Attendu

Après le redéploiement, vous devriez voir :

### ✅ Current Account → List
- Liste complète des comptes
- Recherche par numéro/client
- Actions : Dépôt, Retrait, Transactions, Supprimer

### ✅ Deposit Account → List, New, Deposit, Withdraw, Transactions
- Liste avec compteur de retraits (X / 3)
- Formulaire de création
- Dépôt illimité
- Retrait avec vérification de limite
- Historique complet

### ✅ Loan → List
- Liste avec filtres par statut
- Paiements rapides
- Historique des remboursements
- Annulation de prêt

---

## 🐛 Dépannage

### "BUILD FAILURE"
```
[ERROR] Failed to execute goal...
```
→ Lire le message d'erreur Maven
→ Vérifier que tous les modules compilent individuellement
→ Vérifier les dépendances dans les pom.xml

### "Bank.ear.failed"
```
D:\wildfly\standalone\deployments\Bank.ear.failed
```
→ Consulter les logs WildFly
→ Vérifier que PostgreSQL est démarré
→ Vérifier la configuration de la datasource

### Les pages sont toujours vides
1. Vider le cache du navigateur (Ctrl+Shift+Delete)
2. Vérifier que Bank.ear.deployed existe
3. Redémarrer WildFly
4. Vérifier les logs pour les erreurs
5. Tester l'URL directement : `http://localhost:8080/centralizer/modules/deposit/list.html`

---

## 📞 Checklist de Déploiement

Suivez cette checklist dans l'ordre :

1. [ ] Fermer tous les terminaux Maven en cours
2. [ ] Aller à la racine : `cd d:\Cours\Architecture_Mr_Tahina\Bank\Banque_Harena`
3. [ ] Exécuter : `redeploy-bank.bat` OU `mvn clean package`
4. [ ] Attendre "BUILD SUCCESS"
5. [ ] Vérifier que `Bank-ear\target\Bank.ear` existe
6. [ ] Copier vers WildFly (fait automatiquement par le script)
7. [ ] Attendre 10-15 secondes
8. [ ] Vérifier `Bank.ear.deployed`
9. [ ] Vider le cache du navigateur
10. [ ] Rafraîchir (Ctrl+F5)
11. [ ] Tester les menus

---

## 🎓 Leçon Apprise

**Architecture EAR** :
- Un EAR contient plusieurs modules (EJB + WAR)
- Il faut compiler depuis la racine pour reconstruire le EAR complet
- Le déploiement d'un EAR est plus long qu'un WAR simple
- Toujours vérifier le fichier `.deployed` ou `.failed`

**Workflow de développement** :
1. Modifier les fichiers (HTML, Java, etc.)
2. Compiler depuis la **racine** : `mvn clean package`
3. Déployer le **Bank.ear** (pas le centralizer.war)
4. Attendre le redéploiement complet
5. Tester

---

## ✅ Prêt à Tester !

Exécutez maintenant :
```
redeploy-bank.bat
```

Et dans 2-3 minutes, votre interface sera **100% fonctionnelle** ! 🚀
