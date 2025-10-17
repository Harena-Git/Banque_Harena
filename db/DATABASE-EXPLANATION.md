# 📊 Explication de la Base de Données - Bank Application

## Vue d'ensemble

La base de données `s5-bank` est **partagée par 4 microservices** qui gèrent différents aspects de l'application bancaire.

---

## 🏗️ Architecture de la Base de Données

### Principe de Conception
- **Une seule base de données** : `s5-bank`
- **4 microservices** qui accèdent à différentes tables
- **Pas de clés étrangères entre microservices** (couplage faible)
- **Relations via IDs** uniquement

```
┌─────────────────────────────────────────────────────────────┐
│                    Base de Données: s5-bank                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  CUSTOMER    │  │   CURRENT    │  │     LOAN     │      │
│  │ (Java/EJB)   │  │  (Java/EJB)  │  │  (Java/EJB)  │      │
│  ├──────────────┤  ├──────────────┤  ├──────────────┤      │
│  │ customers    │  │ current_     │  │ loans        │      │
│  │ customer_*   │  │ accounts     │  │ loan_        │      │
│  │              │  │ current_     │  │ payments     │      │
│  │              │  │ transactions │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  ┌──────────────┐                                            │
│  │   DEPOSIT    │                                            │
│  │  (C#/ASP.NET)│                                            │
│  ├──────────────┤                                            │
│  │ deposit_     │                                            │
│  │ accounts     │                                            │
│  │ deposit_     │                                            │
│  │ transactions │                                            │
│  └──────────────┘                                            │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 Microservice 1: CUSTOMER (Gestion des Clients)

### Tables

#### `customers` - Table principale
Stocke les informations des clients de la banque.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique (auto-incrémenté) |
| `first_name` | VARCHAR(255) | Prénom du client |
| `last_name` | VARCHAR(255) | Nom de famille |
| `email` | VARCHAR(255) | Email unique (utilisé pour login) |
| `phone` | VARCHAR(255) | Numéro de téléphone |
| `address` | VARCHAR(255) | Adresse postale |
| `created_at` | TIMESTAMP | Date de création du profil |
| `updated_at` | TIMESTAMP | Date de dernière modification |

**Contraintes** :
- Email doit être unique
- Tous les champs sont obligatoires sauf `updated_at`

#### `customer_deposit_accounts` - Table de liaison
Associe les clients à leurs comptes d'épargne.

| Colonne | Type | Description |
|---------|------|-------------|
| `customer_id` | BIGINT | Référence vers `customers.id` |
| `deposit_account_id` | BIGINT | Référence vers `deposit_accounts.id` |

**Relation** : Un client peut avoir plusieurs comptes d'épargne.

#### `customer_current_accounts` - Table de liaison
Associe les clients à leurs comptes courants.

| Colonne | Type | Description |
|---------|------|-------------|
| `customer_id` | BIGINT | Référence vers `customers.id` |
| `current_account_id` | BIGINT | Référence vers `current_accounts.id` |

**Relation** : Un client peut avoir plusieurs comptes courants.

#### `customer_loans` - Table de liaison
Associe les clients à leurs prêts.

| Colonne | Type | Description |
|---------|------|-------------|
| `customer_id` | BIGINT | Référence vers `customers.id` |
| `loan_id` | BIGINT | Référence vers `loans.id` |

**Relation** : Un client peut avoir plusieurs prêts.

### Fonctionnalités
- ✅ Créer un nouveau client
- ✅ Modifier les informations d'un client
- ✅ Consulter les comptes et prêts d'un client
- ✅ Associer/dissocier des comptes à un client

---

## 💳 Microservice 2: CURRENT (Comptes Courants)

### Tables

#### `current_accounts` - Comptes courants
Gère les comptes courants des clients.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `account_number` | VARCHAR(255) | Numéro de compte unique (ex: CC-2024-001) |
| `customer_id` | BIGINT | Référence vers le client propriétaire |
| `balance` | DECIMAL(19,2) | Solde actuel (peut être négatif) |
| `status` | VARCHAR(20) | ACTIVE, SUSPENDED, CLOSED |
| `created_at` | TIMESTAMP | Date de création du compte |
| `updated_at` | TIMESTAMP | Date de dernière modification |

**Contraintes** :
- `account_number` doit être unique
- `status` doit être ACTIVE, SUSPENDED ou CLOSED

#### `current_transactions` - Transactions
Historique de toutes les transactions sur les comptes courants.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `account_id` | BIGINT | Référence vers `current_accounts.id` |
| `type` | VARCHAR(20) | Type de transaction (voir ci-dessous) |
| `amount` | DECIMAL(19,2) | Montant (toujours positif) |
| `balance_after` | DECIMAL(19,2) | Solde après la transaction |
| `description` | VARCHAR(500) | Description optionnelle |
| `transaction_date` | TIMESTAMP | Date et heure de la transaction |

**Types de transactions** :
- `DEPOSIT` : Dépôt d'argent
- `WITHDRAWAL` : Retrait d'argent
- `TRANSFER_IN` : Virement entrant
- `TRANSFER_OUT` : Virement sortant
- `FEE` : Frais bancaires
- `INTEREST` : Intérêts créditeurs

**Contraintes** :
- `amount` doit être > 0
- Clé étrangère vers `current_accounts` avec suppression en cascade

### Fonctionnalités
- ✅ Créer un compte courant pour un client
- ✅ Effectuer un dépôt
- ✅ Effectuer un retrait
- ✅ Consulter le solde actuel
- ✅ Consulter l'historique des transactions
- ✅ Consulter le solde à une date donnée

### Exemple de Flux
```
1. Client dépose 1000€
   → balance = 0 + 1000 = 1000€
   → Transaction: DEPOSIT, amount=1000, balance_after=1000

2. Client retire 300€
   → balance = 1000 - 300 = 700€
   → Transaction: WITHDRAWAL, amount=300, balance_after=700

3. Frais bancaires de 5€
   → balance = 700 - 5 = 695€
   → Transaction: FEE, amount=5, balance_after=695
```

---

## 💰 Microservice 3: LOAN (Prêts Bancaires)

### Tables

#### `loans` - Prêts
Gère les prêts accordés aux clients.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `loan_number` | VARCHAR(255) | Numéro de prêt unique (ex: LOAN-2024-001) |
| `customer_id` | BIGINT | Référence vers le client emprunteur |
| `principal_amount` | DECIMAL(19,2) | Montant emprunté (capital) |
| `annual_rate` | DECIMAL(5,2) | Taux d'intérêt annuel (ex: 5.50 pour 5.5%) |
| `duration_months` | INTEGER | Durée du prêt en mois |
| `total_amount_due` | DECIMAL(19,2) | Montant total à rembourser (capital + intérêts) |
| `remaining_amount` | DECIMAL(19,2) | Montant restant à rembourser |
| `start_date` | TIMESTAMP | Date de début du prêt |
| `end_date` | TIMESTAMP | Date de fin prévue |
| `status` | VARCHAR(20) | ACTIVE, PAID, DEFAULTED, CANCELLED |
| `created_at` | TIMESTAMP | Date de création |
| `updated_at` | TIMESTAMP | Date de dernière modification |

**Statuts** :
- `ACTIVE` : Prêt en cours
- `PAID` : Prêt entièrement remboursé
- `DEFAULTED` : Défaut de paiement
- `CANCELLED` : Prêt annulé

**Calcul des intérêts** :
```
Intérêts = Principal × (Taux / 100) × (Durée / 12)
Total dû = Principal + Intérêts
```

**Exemple** :
- Principal : 10 000€
- Taux : 5.5% par an
- Durée : 24 mois
- Intérêts : 10 000 × 0.055 × (24/12) = 1 100€
- Total dû : 10 000 + 1 100 = 11 100€

#### `loan_payments` - Remboursements
Historique des remboursements de prêts.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `loan_id` | BIGINT | Référence vers `loans.id` |
| `amount` | DECIMAL(19,2) | Montant du remboursement |
| `remaining_after_payment` | DECIMAL(19,2) | Montant restant après ce paiement |
| `payment_date` | TIMESTAMP | Date du remboursement |
| `notes` | VARCHAR(500) | Notes optionnelles |

**Contraintes** :
- `amount` doit être > 0
- Clé étrangère vers `loans` avec suppression en cascade

### Fonctionnalités
- ✅ Créer un prêt pour un client
- ✅ Calculer automatiquement le montant total avec intérêts
- ✅ Enregistrer un remboursement
- ✅ Mettre à jour le montant restant
- ✅ Consulter l'historique des remboursements
- ✅ Consulter le montant total restant à rembourser

### Exemple de Flux
```
1. Création du prêt
   → Principal: 10 000€, Taux: 5.5%, Durée: 24 mois
   → Total dû: 11 100€
   → Remaining: 11 100€

2. Premier remboursement de 500€
   → Remaining: 11 100 - 500 = 10 600€
   → Payment: amount=500, remaining_after=10600

3. Deuxième remboursement de 1000€
   → Remaining: 10 600 - 1000 = 9 600€
   → Payment: amount=1000, remaining_after=9600
```

---

## 🏦 Microservice 4: DEPOSIT (Comptes d'Épargne)

### Tables

#### `deposit_accounts` - Comptes d'épargne
Gère les comptes d'épargne avec taux d'intérêt et limite de retraits.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `account_number` | VARCHAR(50) | Numéro de compte unique (ex: DEP-2024-001) |
| `customer_id` | BIGINT | Référence vers le client propriétaire |
| `balance` | DECIMAL(19,2) | Solde actuel (ne peut pas être négatif) |
| `annual_rate` | DECIMAL(5,2) | Taux d'intérêt annuel (ex: 2.50 pour 2.5%) |
| `monthly_withdrawal_limit` | INTEGER | Nombre max de retraits par mois (généralement 3) |
| `withdrawals_this_month` | INTEGER | Nombre de retraits ce mois-ci |
| `status` | VARCHAR(20) | ACTIVE, SUSPENDED, CLOSED |
| `created_at` | TIMESTAMP | Date de création |
| `updated_at` | TIMESTAMP | Date de dernière modification |

**Contraintes** :
- `balance` doit être ≥ 0 (pas de découvert)
- `withdrawals_this_month` ≤ `monthly_withdrawal_limit`

**Spécificités** :
- ✅ Taux d'intérêt annuel (ex: 2.5%)
- ✅ Limite de retraits mensuels (généralement 3)
- ✅ Solde ne peut pas être négatif

#### `deposit_transactions` - Transactions
Historique des transactions sur les comptes d'épargne.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGSERIAL | Identifiant unique |
| `account_id` | BIGINT | Référence vers `deposit_accounts.id` |
| `type` | VARCHAR(20) | DEPOSIT ou WITHDRAWAL |
| `amount` | DECIMAL(19,2) | Montant (toujours positif) |
| `balance_after` | DECIMAL(19,2) | Solde après la transaction |
| `description` | VARCHAR(500) | Description optionnelle |
| `transaction_date` | TIMESTAMP | Date et heure de la transaction |

**Types de transactions** :
- `DEPOSIT` : Dépôt d'argent
- `WITHDRAWAL` : Retrait d'argent

**Contraintes** :
- `amount` doit être > 0
- Clé étrangère vers `deposit_accounts` avec suppression en cascade

### Fonctionnalités
- ✅ Créer un compte d'épargne pour un client
- ✅ Effectuer un dépôt (illimité)
- ✅ Effectuer un retrait (limité à 3 par mois)
- ✅ Consulter le solde actuel
- ✅ Consulter l'historique des transactions
- ✅ Calculer les intérêts annuels

### Exemple de Flux
```
1. Création du compte
   → Balance: 0€
   → Annual rate: 2.5%
   → Monthly withdrawal limit: 3
   → Withdrawals this month: 0

2. Dépôt de 5000€
   → Balance: 5000€
   → Transaction: DEPOSIT, amount=5000, balance_after=5000

3. Premier retrait de 500€
   → Balance: 4500€
   → Withdrawals this month: 1
   → Transaction: WITHDRAWAL, amount=500, balance_after=4500

4. Tentative de 4ème retrait dans le mois
   → ❌ REFUSÉ (limite de 3 retraits atteinte)
```

---

## 🔗 Relations entre Microservices

### Principe de Couplage Faible
Les microservices communiquent via **IDs** uniquement, sans clés étrangères entre eux.

```
Customer (id=1)
    ↓ (via customer_id)
    ├─→ Current Account (id=10, customer_id=1)
    ├─→ Deposit Account (id=20, customer_id=1)
    └─→ Loan (id=30, customer_id=1)
```

### Pourquoi pas de clés étrangères ?
- ✅ **Indépendance** : Chaque microservice peut évoluer séparément
- ✅ **Scalabilité** : Possibilité de séparer les bases plus tard
- ✅ **Résilience** : Un microservice en panne n'affecte pas les autres
- ⚠️ **Intégrité** : Gérée au niveau applicatif (pas par la base)

---

## 📊 Vues Utiles

### `v_customer_accounts_summary`
Résumé du nombre de comptes et prêts par client.

```sql
SELECT * FROM v_customer_accounts_summary;
```

**Résultat** :
| customer_id | first_name | last_name | current_accounts_count | deposit_accounts_count | loans_count |
|-------------|------------|-----------|------------------------|------------------------|-------------|
| 1 | Jean | Dupont | 2 | 1 | 1 |
| 2 | Marie | Martin | 1 | 2 | 0 |

### `v_customer_total_balance`
Solde total (courant + épargne) par client.

```sql
SELECT * FROM v_customer_total_balance;
```

**Résultat** :
| customer_id | first_name | last_name | total_current_balance | total_deposit_balance | total_balance |
|-------------|------------|-----------|----------------------|----------------------|---------------|
| 1 | Jean | Dupont | 1500.00 | 5000.00 | 6500.00 |
| 2 | Marie | Martin | 2500.50 | 10000.00 | 12500.50 |

---

## 🛠️ Fonctions Utiles

### `calculate_loan_interest(principal, rate, months)`
Calcule le montant des intérêts pour un prêt.

```sql
SELECT calculate_loan_interest(10000, 5.5, 24);
-- Résultat: 1100.00
```

### `can_withdraw_from_deposit(account_id)`
Vérifie si un retrait est autorisé (limite mensuelle).

```sql
SELECT can_withdraw_from_deposit(1);
-- Résultat: true ou false
```

---

## 📈 Index et Performances

### Index créés automatiquement
- **Clés primaires** : Index unique sur tous les `id`
- **Contraintes UNIQUE** : Index sur `email`, `account_number`, `loan_number`

### Index supplémentaires
- `idx_current_accounts_customer` : Recherche rapide des comptes par client
- `idx_current_transactions_account` : Recherche rapide des transactions par compte
- `idx_current_transactions_date` : Tri des transactions par date
- `idx_loans_customer` : Recherche rapide des prêts par client
- `idx_loans_status` : Filtrage des prêts par statut
- `idx_deposit_accounts_customer` : Recherche rapide des comptes d'épargne par client

---

## 🔒 Contraintes et Validations

### Au niveau Base de Données
- ✅ Contraintes NOT NULL
- ✅ Contraintes UNIQUE
- ✅ Contraintes CHECK (montants positifs, statuts valides)
- ✅ Clés étrangères (au sein d'un même microservice)

### Au niveau Applicatif
- ✅ Validation des emails
- ✅ Validation des montants
- ✅ Vérification des limites de retrait
- ✅ Calcul automatique des intérêts
- ✅ Mise à jour automatique des soldes

---

## 📝 Résumé

| Microservice | Tables | Fonctionnalité Principale |
|--------------|--------|---------------------------|
| **Customer** | 4 tables | Gestion des profils clients et associations |
| **Current** | 2 tables | Comptes courants avec transactions illimitées |
| **Loan** | 2 tables | Prêts avec calcul d'intérêts et remboursements |
| **Deposit** | 2 tables | Comptes d'épargne avec intérêts et limite de retraits |

**Total** : 10 tables + 2 vues + 2 fonctions

---

**Pour plus d'informations, consultez le fichier `Create.sql` qui contient le script complet de création.**
