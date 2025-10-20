-- ============================================================================
-- SCRIPT DE NETTOYAGE COMPLET - BANK APPLICATION
-- ============================================================================
--
-- Ce script supprime TOUTES les données de TOUTES les tables
-- ⚠️ ATTENTION: Cette opération est IRRÉVERSIBLE !
-- ⚠️ Toutes les données seront DÉFINITIVEMENT perdues !
--
-- Utilisation dans PostgreSQL:
-- 1. Ouvrir pgAdmin ou psql
-- 2. Se connecter à la base de données s5-bank
-- 3. Copier-coller ce script complet
-- 4. Exécuter
--
-- ============================================================================

-- Désactiver temporairement les contraintes de clés étrangères
SET session_replication_role = 'replica';

-- ============================================================================
-- SUPPRESSION DES DONNÉES - Dans l'ordre inverse des dépendances
-- ============================================================================

-- Module DEPOSIT (C#)
DELETE FROM deposit_transactions;
DELETE FROM deposit_accounts;

-- Module LOAN (Java)
DELETE FROM loan_payments;
DELETE FROM loans;

-- Module CURRENT (Java)
DELETE FROM current_transactions;
DELETE FROM current_accounts;

-- Module CUSTOMER (Java) - Tables de liaison d'abord
DELETE FROM customer_loans;
DELETE FROM customer_deposit_accounts;
DELETE FROM customer_current_accounts;
DELETE FROM customers;

-- Réactiver les contraintes
SET session_replication_role = 'origin';

-- ============================================================================
-- RÉINITIALISATION DES SÉQUENCES (Auto-increment)
-- ============================================================================

-- Customer sequences
ALTER SEQUENCE IF EXISTS customers_id_seq RESTART WITH 1;

-- Current sequences
ALTER SEQUENCE IF EXISTS current_accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS current_transactions_id_seq RESTART WITH 1;

-- Loan sequences
ALTER SEQUENCE IF EXISTS loans_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS loan_payments_id_seq RESTART WITH 1;

-- Deposit sequences
ALTER SEQUENCE IF EXISTS deposit_accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS deposit_transactions_id_seq RESTART WITH 1;

-- ============================================================================
-- VÉRIFICATION (Affiche le nombre de lignes - devrait être 0 partout)
-- ============================================================================

SELECT 'customers' AS table_name, COUNT(*) AS row_count FROM customers
UNION ALL
SELECT 'customer_current_accounts', COUNT(*) FROM customer_current_accounts
UNION ALL
SELECT 'customer_deposit_accounts', COUNT(*) FROM customer_deposit_accounts
UNION ALL
SELECT 'customer_loans', COUNT(*) FROM customer_loans
UNION ALL
SELECT 'current_accounts', COUNT(*) FROM current_accounts
UNION ALL
SELECT 'current_transactions', COUNT(*) FROM current_transactions
UNION ALL
SELECT 'loans', COUNT(*) FROM loans
UNION ALL
SELECT 'loan_payments', COUNT(*) FROM loan_payments
UNION ALL
SELECT 'deposit_accounts', COUNT(*) FROM deposit_accounts
UNION ALL
SELECT 'deposit_transactions', COUNT(*) FROM deposit_transactions;

-- ============================================================================
-- FIN DU SCRIPT - Base de données nettoyée
-- ============================================================================
