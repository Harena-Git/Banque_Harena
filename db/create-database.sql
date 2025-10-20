-- ============================================================================
-- Script de création de la base de données pour Bank Application
-- ============================================================================
-- 
-- Exécution :
-- 1. Ouvrez pgAdmin ou psql
-- 2. Connectez-vous en tant que superuser (postgres)
-- 3. Exécutez ce script
--
-- ============================================================================

-- Créer la base de données
CREATE DATABASE "s5-bank" 
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'French_France.1252'
    LC_CTYPE = 'French_France.1252'
    TEMPLATE = template0;

-- Se connecter à la base de données
\c s5-bank

-- Afficher un message de confirmation
SELECT 'Base de données s5-bank créée avec succès !' AS message;

-- Afficher les informations de la base
SELECT 
    current_database() AS database_name,
    pg_encoding_to_char(encoding) AS encoding,
    datcollate AS collation,
    datctype AS ctype
FROM pg_database 
WHERE datname = 's5-bank';

-- ============================================================================
-- NOTE : Les tables seront créées automatiquement par Hibernate
-- ============================================================================
-- 
-- Lorsque vous déploierez l'application, Hibernate créera automatiquement :
-- - customers
-- - current_accounts
-- - current_transactions
-- - loans
-- - loan_payments
--
-- Pour vérifier les tables après déploiement :
-- \dt
--
-- ============================================================================
