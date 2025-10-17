# 📑 Index de la Documentation Base de Données

Bienvenue dans la documentation de la base de données de Bank Application !

---

## 🎯 Par où commencer ?

### Pour créer rapidement la base de données
1. **Exécutez** : `Create.sql` (script SQL complet)
2. **Vérifiez** : `DATABASE-QUICK-REFERENCE.md` (commandes de vérification)

### Pour comprendre la structure
1. **Lisez** : `DATABASE-EXPLANATION.md` (documentation complète)
2. **Visualisez** : `DATABASE-DIAGRAM.txt` (diagramme ASCII)

### Pour les requêtes courantes
1. **Consultez** : `DATABASE-QUICK-REFERENCE.md` (exemples de requêtes)

---

## 📚 Documentation Disponible

| Fichier | Description | Niveau |
|---------|-------------|--------|
| **Create.sql** | Script SQL complet de création | ⭐ Essentiel |
| **DATABASE-EXPLANATION.md** | Documentation détaillée de toutes les tables | ⭐⭐⭐ Complet |
| **DATABASE-DIAGRAM.txt** | Diagramme visuel ASCII de la structure | ⭐⭐ Intermédiaire |
| **DATABASE-QUICK-REFERENCE.md** | Référence rapide avec exemples de requêtes | ⭐ Pratique |
| **DATABASE-INDEX.md** | Ce fichier - Navigation dans la documentation | ⭐ Guide |

---

## 🗂️ Structure de la Documentation

### 1. Create.sql
**Contenu :**
- Script SQL complet et exécutable
- Création de toutes les tables
- Création des vues
- Création des fonctions
- Commentaires détaillés
- Données de test (optionnel)

**Utilisation :**
```bash
psql -U postgres -d s5-bank -f Create.sql
```

---

### 2. DATABASE-EXPLANATION.md
**Contenu :**
- Vue d'ensemble de l'architecture
- Documentation détaillée de chaque microservice
- Explication de chaque table et colonne
- Relations entre les tables
- Fonctionnalités de chaque module
- Exemples de flux de données
- Vues et fonctions utiles

**Sections :**
- 🧑 Microservice Customer
- 💳 Microservice Current
- 💰 Microservice Loan
- 🏦 Microservice Deposit
- 🔗 Relations entre microservices
- 📊 Vues utiles
- 🛠️ Fonctions utiles

---

### 3. DATABASE-DIAGRAM.txt
**Contenu :**
- Diagramme ASCII de toutes les tables
- Relations visuelles entre les tables
- Exemples de flux de données
- Légende des symboles
- Statistiques

**Format :**
```
┌─────────────────────┐
│     customers       │
├─────────────────────┤
│ PK  id              │
│     first_name      │
│ UQ  email           │
└─────────────────────┘
```

---

### 4. DATABASE-QUICK-REFERENCE.md
**Contenu :**
- Commandes essentielles PostgreSQL
- Requêtes SQL par microservice
- Exemples d'insertion
- Exemples de mise à jour
- Exemples de suppression
- Requêtes statistiques
- Commandes de maintenance
- Commandes de sauvegarde

**Sections :**
- 🎯 Commandes essentielles
- 📊 Tables par microservice
- 🔍 Requêtes utiles
- 🧮 Utilisation des fonctions
- 📝 Exemples d'insertion
- 🗑️ Suppression de données
- 📊 Statistiques
- 🔧 Maintenance
- 🎯 Sauvegarde

---

## 🎯 Guides par Scénario

### Scénario 1 : Première installation
```
1. Create.sql                    → Créer la base de données
2. DATABASE-QUICK-REFERENCE.md   → Vérifier la création
3. DATABASE-EXPLANATION.md       → Comprendre la structure
```

### Scénario 2 : Développement d'une fonctionnalité
```
1. DATABASE-EXPLANATION.md       → Comprendre le microservice concerné
2. DATABASE-QUICK-REFERENCE.md   → Trouver des exemples de requêtes
3. DATABASE-DIAGRAM.txt          → Visualiser les relations
```

### Scénario 3 : Maintenance de la base
```
1. DATABASE-QUICK-REFERENCE.md   → Commandes de maintenance
2. Create.sql                    → Référence pour les contraintes
```

### Scénario 4 : Documentation pour un nouveau développeur
```
1. DATABASE-INDEX.md (ce fichier) → Vue d'ensemble
2. DATABASE-DIAGRAM.txt           → Visualisation rapide
3. DATABASE-EXPLANATION.md        → Compréhension approfondie
4. DATABASE-QUICK-REFERENCE.md    → Exemples pratiques
```

---

## 📖 Documentation par Microservice

### 🧑 Customer (Gestion des Clients)

**Tables :**
- `customers` - Profils clients
- `customer_current_accounts` - Liaison clients ↔ comptes courants
- `customer_deposit_accounts` - Liaison clients ↔ comptes d'épargne
- `customer_loans` - Liaison clients ↔ prêts

**Documentation :**
- **Détails** : `DATABASE-EXPLANATION.md` → Section "Microservice 1: CUSTOMER"
- **Diagramme** : `DATABASE-DIAGRAM.txt` → Section "MICROSERVICE 1: CUSTOMER"
- **Requêtes** : `DATABASE-QUICK-REFERENCE.md` → Section "CUSTOMER (4 tables)"

---

### 💳 Current (Comptes Courants)

**Tables :**
- `current_accounts` - Comptes courants
- `current_transactions` - Transactions sur comptes courants

**Documentation :**
- **Détails** : `DATABASE-EXPLANATION.md` → Section "Microservice 2: CURRENT"
- **Diagramme** : `DATABASE-DIAGRAM.txt` → Section "MICROSERVICE 2: CURRENT"
- **Requêtes** : `DATABASE-QUICK-REFERENCE.md` → Section "CURRENT (2 tables)"

**Types de transactions :**
- DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, FEE, INTEREST

---

### 💰 Loan (Prêts Bancaires)

**Tables :**
- `loans` - Prêts
- `loan_payments` - Remboursements de prêts

**Documentation :**
- **Détails** : `DATABASE-EXPLANATION.md` → Section "Microservice 3: LOAN"
- **Diagramme** : `DATABASE-DIAGRAM.txt` → Section "MICROSERVICE 3: LOAN"
- **Requêtes** : `DATABASE-QUICK-REFERENCE.md` → Section "LOAN (2 tables)"

**Calcul des intérêts :**
```sql
SELECT calculate_loan_interest(principal, rate, months);
```

---

### 🏦 Deposit (Comptes d'Épargne)

**Tables :**
- `deposit_accounts` - Comptes d'épargne
- `deposit_transactions` - Transactions sur comptes d'épargne

**Documentation :**
- **Détails** : `DATABASE-EXPLANATION.md` → Section "Microservice 4: DEPOSIT"
- **Diagramme** : `DATABASE-DIAGRAM.txt` → Section "MICROSERVICE 4: DEPOSIT"
- **Requêtes** : `DATABASE-QUICK-REFERENCE.md` → Section "DEPOSIT (2 tables)"

**Spécificités :**
- Taux d'intérêt annuel
- Limite de retraits mensuels (généralement 3)
- Solde ne peut pas être négatif

---

## 🔍 Recherche Rapide

### Je veux...

#### ...créer la base de données
→ `Create.sql`

#### ...comprendre une table spécifique
→ `DATABASE-EXPLANATION.md` (rechercher le nom de la table)

#### ...voir les relations entre tables
→ `DATABASE-DIAGRAM.txt`

#### ...trouver un exemple de requête
→ `DATABASE-QUICK-REFERENCE.md`

#### ...comprendre le calcul des intérêts
→ `DATABASE-EXPLANATION.md` → Section "Microservice 3: LOAN"

#### ...savoir comment insérer des données
→ `DATABASE-QUICK-REFERENCE.md` → Section "Exemples d'Insertion"

#### ...faire une sauvegarde
→ `DATABASE-QUICK-REFERENCE.md` → Section "Commandes de Sauvegarde"

---

## 📊 Statistiques de la Base de Données

| Élément | Nombre |
|---------|--------|
| **Tables** | 10 |
| **Vues** | 2 |
| **Fonctions** | 2 |
| **Microservices** | 4 |

### Répartition des tables

| Microservice | Tables | Description |
|--------------|--------|-------------|
| Customer | 4 | 1 principale + 3 de liaison |
| Current | 2 | 1 compte + 1 transactions |
| Loan | 2 | 1 prêt + 1 remboursements |
| Deposit | 2 | 1 compte + 1 transactions |

---

## 🛠️ Outils et Commandes

### Commandes PostgreSQL Essentielles

```sql
-- Lister les tables
\dt

-- Lister les vues
\dv

-- Lister les fonctions
\df

-- Voir la structure d'une table
\d nom_table

-- Exécuter un script
\i chemin/vers/script.sql

-- Se connecter à une base
\c nom_base

-- Quitter
\q
```

### Commandes Shell

```bash
# Créer une base
createdb -U postgres s5-bank

# Exécuter un script
psql -U postgres -d s5-bank -f Create.sql

# Sauvegarder
pg_dump -U postgres -d s5-bank -F c -f backup.dump

# Restaurer
pg_restore -U postgres -d s5-bank -c backup.dump
```

---

## 🎓 Niveau de Difficulté

| Document | Difficulté | Temps de Lecture |
|----------|-----------|------------------|
| DATABASE-INDEX.md | ⭐ Facile | 5 min |
| DATABASE-QUICK-REFERENCE.md | ⭐ Facile | 10 min |
| DATABASE-DIAGRAM.txt | ⭐⭐ Moyen | 10 min |
| DATABASE-EXPLANATION.md | ⭐⭐⭐ Avancé | 30 min |
| Create.sql | ⭐⭐ Moyen | 15 min (lecture) |

---

## 📞 Besoin d'Aide ?

### Ordre de consultation recommandé :
1. Consultez `DATABASE-QUICK-REFERENCE.md` pour les requêtes courantes
2. Consultez `DATABASE-EXPLANATION.md` pour comprendre la structure
3. Consultez `DATABASE-DIAGRAM.txt` pour visualiser les relations
4. Consultez `Create.sql` pour voir le code SQL exact

### Questions Fréquentes :

**Q: Comment créer la base de données ?**
→ Exécutez `Create.sql` avec psql

**Q: Quelle est la différence entre current et deposit ?**
→ Consultez `DATABASE-EXPLANATION.md` sections 2 et 4

**Q: Comment calculer les intérêts d'un prêt ?**
→ Consultez `DATABASE-QUICK-REFERENCE.md` → "Utilisation des Fonctions"

**Q: Comment voir tous les comptes d'un client ?**
→ Consultez `DATABASE-QUICK-REFERENCE.md` → "Voir tous les comptes d'un client"

**Q: Pourquoi pas de clés étrangères entre microservices ?**
→ Consultez `DATABASE-EXPLANATION.md` → "Relations entre Microservices"

---

## 🌟 Recommandations

### Pour les débutants
1. Commencez par `DATABASE-DIAGRAM.txt` pour visualiser
2. Lisez `DATABASE-EXPLANATION.md` section par section
3. Pratiquez avec `DATABASE-QUICK-REFERENCE.md`

### Pour les développeurs expérimentés
1. Exécutez `Create.sql`
2. Consultez `DATABASE-QUICK-REFERENCE.md` pour les requêtes
3. Référez-vous à `DATABASE-EXPLANATION.md` au besoin

### Pour les administrateurs de base de données
1. Étudiez `Create.sql` en détail
2. Consultez `DATABASE-EXPLANATION.md` pour les contraintes
3. Utilisez `DATABASE-QUICK-REFERENCE.md` pour la maintenance

---

## ✅ Checklist de Compréhension

Après avoir lu la documentation, vous devriez pouvoir :

- [ ] Créer la base de données complète
- [ ] Expliquer le rôle de chaque microservice
- [ ] Identifier les tables de chaque microservice
- [ ] Comprendre les relations entre les tables
- [ ] Écrire des requêtes pour chaque microservice
- [ ] Utiliser les vues et fonctions
- [ ] Insérer des données de test
- [ ] Effectuer des opérations de maintenance

---

**Bonne exploration de la base de données ! 🚀**

*Pour toute question, consultez d'abord la documentation appropriée ci-dessus.*
