# 📚 Référence Rapide - Base de Données

## 🎯 Commandes Essentielles

### Création de la base
```sql
-- Créer la base de données
CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';

-- Se connecter à la base
\c s5-bank

-- Exécuter le script complet
\i Create.sql
```

### Vérification
```sql
-- Lister toutes les tables
\dt

-- Lister les vues
\dv

-- Lister les fonctions
\df

-- Voir la structure d'une table
\d customers
\d current_accounts
\d loans
\d deposit_accounts
```

---

## 📊 Tables par Microservice

### 🧑 CUSTOMER (4 tables)
```sql
-- Table principale
SELECT * FROM customers;

-- Tables de liaison
SELECT * FROM customer_current_accounts;
SELECT * FROM customer_deposit_accounts;
SELECT * FROM customer_loans;
```

### 💳 CURRENT (2 tables)
```sql
-- Comptes courants
SELECT * FROM current_accounts;

-- Transactions
SELECT * FROM current_transactions;
```

### 💰 LOAN (2 tables)
```sql
-- Prêts
SELECT * FROM loans;

-- Remboursements
SELECT * FROM loan_payments;
```

### 🏦 DEPOSIT (2 tables)
```sql
-- Comptes d'épargne
SELECT * FROM deposit_accounts;

-- Transactions
SELECT * FROM deposit_transactions;
```

---

## 🔍 Requêtes Utiles

### Voir tous les comptes d'un client
```sql
-- Remplacer 1 par l'ID du client
SELECT 
    c.id,
    c.first_name,
    c.last_name,
    c.email
FROM customers c
WHERE c.id = 1;

-- Ses comptes courants
SELECT ca.* 
FROM current_accounts ca
JOIN customer_current_accounts cca ON ca.id = cca.current_account_id
WHERE cca.customer_id = 1;

-- Ses comptes d'épargne
SELECT da.* 
FROM deposit_accounts da
JOIN customer_deposit_accounts cda ON da.id = cda.deposit_account_id
WHERE cda.customer_id = 1;

-- Ses prêts
SELECT l.* 
FROM loans l
JOIN customer_loans cl ON l.id = cl.loan_id
WHERE cl.customer_id = 1;
```

### Résumé des comptes par client
```sql
SELECT * FROM v_customer_accounts_summary;
```

### Solde total par client
```sql
SELECT * FROM v_customer_total_balance;
```

### Historique des transactions d'un compte courant
```sql
-- Remplacer 10 par l'ID du compte
SELECT 
    ct.transaction_date,
    ct.type,
    ct.amount,
    ct.balance_after,
    ct.description
FROM current_transactions ct
WHERE ct.account_id = 10
ORDER BY ct.transaction_date DESC;
```

### Historique des remboursements d'un prêt
```sql
-- Remplacer 30 par l'ID du prêt
SELECT 
    lp.payment_date,
    lp.amount,
    lp.remaining_after_payment,
    lp.notes
FROM loan_payments lp
WHERE lp.loan_id = 30
ORDER BY lp.payment_date DESC;
```

### Prêts actifs avec montant restant
```sql
SELECT 
    l.loan_number,
    c.first_name || ' ' || c.last_name AS customer_name,
    l.principal_amount,
    l.total_amount_due,
    l.remaining_amount,
    l.annual_rate,
    l.end_date
FROM loans l
JOIN customer_loans cl ON l.id = cl.loan_id
JOIN customers c ON cl.customer_id = c.id
WHERE l.status = 'ACTIVE'
ORDER BY l.remaining_amount DESC;
```

### Comptes d'épargne avec limite de retrait atteinte
```sql
SELECT 
    da.account_number,
    c.first_name || ' ' || c.last_name AS customer_name,
    da.withdrawals_this_month,
    da.monthly_withdrawal_limit
FROM deposit_accounts da
JOIN customer_deposit_accounts cda ON da.id = cda.deposit_account_id
JOIN customers c ON cda.customer_id = c.id
WHERE da.withdrawals_this_month >= da.monthly_withdrawal_limit;
```

---

## 🧮 Utilisation des Fonctions

### Calculer les intérêts d'un prêt
```sql
-- Exemple: 10000€ à 5.5% sur 24 mois
SELECT calculate_loan_interest(10000, 5.5, 24);
-- Résultat: 1100.00
```

### Vérifier si un retrait est autorisé
```sql
-- Remplacer 20 par l'ID du compte d'épargne
SELECT can_withdraw_from_deposit(20);
-- Résultat: true ou false
```

---

## 📝 Exemples d'Insertion

### Créer un client
```sql
INSERT INTO customers (first_name, last_name, email, phone, address) 
VALUES ('Jean', 'Dupont', 'jean.dupont@email.com', '0612345678', '10 Rue de Paris, 75001 Paris')
RETURNING id;
```

### Créer un compte courant
```sql
INSERT INTO current_accounts (account_number, customer_id, balance, status) 
VALUES ('CC-2024-001', 1, 0.00, 'ACTIVE')
RETURNING id;

-- Lier au client
INSERT INTO customer_current_accounts (customer_id, current_account_id) 
VALUES (1, 1);
```

### Créer un compte d'épargne
```sql
INSERT INTO deposit_accounts (account_number, customer_id, balance, annual_rate, monthly_withdrawal_limit) 
VALUES ('DEP-2024-001', 1, 0.00, 2.50, 3)
RETURNING id;

-- Lier au client
INSERT INTO customer_deposit_accounts (customer_id, deposit_account_id) 
VALUES (1, 1);
```

### Créer un prêt
```sql
INSERT INTO loans (
    loan_number, customer_id, principal_amount, annual_rate, duration_months,
    total_amount_due, remaining_amount, start_date, end_date, status
) VALUES (
    'LOAN-2024-001', 1, 10000.00, 5.50, 24,
    11100.00, 11100.00, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 months', 'ACTIVE'
)
RETURNING id;

-- Lier au client
INSERT INTO customer_loans (customer_id, loan_id) 
VALUES (1, 1);
```

### Enregistrer une transaction (compte courant)
```sql
-- Dépôt de 1000€
UPDATE current_accounts SET balance = balance + 1000 WHERE id = 1;

INSERT INTO current_transactions (account_id, type, amount, balance_after, description) 
VALUES (1, 'DEPOSIT', 1000.00, 1000.00, 'Dépôt initial');
```

### Enregistrer un remboursement de prêt
```sql
-- Remboursement de 500€
UPDATE loans SET remaining_amount = remaining_amount - 500 WHERE id = 1;

INSERT INTO loan_payments (loan_id, amount, remaining_after_payment, notes) 
VALUES (1, 500.00, 10600.00, 'Remboursement mensuel');
```

---

## 🗑️ Suppression de Données

### Supprimer un client (et toutes ses associations)
```sql
-- Les tables de liaison seront supprimées automatiquement (CASCADE)
DELETE FROM customers WHERE id = 1;
```

### Supprimer un compte courant (et ses transactions)
```sql
-- Les transactions seront supprimées automatiquement (CASCADE)
DELETE FROM current_accounts WHERE id = 1;
```

### Supprimer un prêt (et ses remboursements)
```sql
-- Les remboursements seront supprimés automatiquement (CASCADE)
DELETE FROM loans WHERE id = 1;
```

---

## 📊 Statistiques

### Nombre total de clients
```sql
SELECT COUNT(*) AS total_customers FROM customers;
```

### Nombre de comptes par type
```sql
SELECT 
    (SELECT COUNT(*) FROM current_accounts) AS current_accounts,
    (SELECT COUNT(*) FROM deposit_accounts) AS deposit_accounts,
    (SELECT COUNT(*) FROM loans) AS loans;
```

### Montant total des prêts actifs
```sql
SELECT 
    COUNT(*) AS active_loans,
    SUM(principal_amount) AS total_principal,
    SUM(remaining_amount) AS total_remaining
FROM loans
WHERE status = 'ACTIVE';
```

### Solde total de tous les comptes
```sql
SELECT 
    SUM(balance) AS total_current_balance
FROM current_accounts;

SELECT 
    SUM(balance) AS total_deposit_balance
FROM deposit_accounts;
```

---

## 🔧 Maintenance

### Réinitialiser le compteur de retraits mensuels
```sql
-- À exécuter au début de chaque mois
UPDATE deposit_accounts 
SET withdrawals_this_month = 0;
```

### Mettre à jour le statut des prêts terminés
```sql
UPDATE loans 
SET status = 'PAID' 
WHERE remaining_amount <= 0 AND status = 'ACTIVE';
```

### Fermer les comptes avec solde nul depuis plus de 6 mois
```sql
UPDATE current_accounts 
SET status = 'CLOSED' 
WHERE balance = 0 
  AND updated_at < CURRENT_TIMESTAMP - INTERVAL '6 months'
  AND status = 'ACTIVE';
```

---

## 🎯 Commandes de Sauvegarde

### Sauvegarder la base de données
```bash
pg_dump -U postgres -d s5-bank -F c -f s5-bank-backup.dump
```

### Restaurer la base de données
```bash
pg_restore -U postgres -d s5-bank -c s5-bank-backup.dump
```

### Exporter une table en CSV
```sql
\copy customers TO 'customers.csv' CSV HEADER;
```

---

## 📖 Documentation Complète

Pour plus de détails, consultez :
- **`Create.sql`** - Script SQL complet
- **`DATABASE-EXPLANATION.md`** - Documentation détaillée
- **`DATABASE-DIAGRAM.txt`** - Diagramme visuel
