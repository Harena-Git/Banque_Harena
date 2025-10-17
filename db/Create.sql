-- ============================================================================
-- SCRIPT DE CRÉATION DE BASE DE DONNÉES - BANK APPLICATION
-- ============================================================================
-- 
-- Ce script crée la structure complète de la base de données pour l'application
-- bancaire avec architecture microservices.
--
-- ARCHITECTURE:
-- - Base de données unique: s5-bank
-- - 4 microservices partageant la même base
-- - Relations via IDs (pas de clés étrangères entre microservices)
--
-- MICROSERVICES:
-- 1. Customer (Java)   - Gestion des clients
-- 2. Current (Java)    - Comptes courants
-- 3. Loan (Java)       - Prêts bancaires
-- 4. Deposit (C#)      - Comptes d'épargne
--
-- ============================================================================

-- Connexion à PostgreSQL et création de la base
-- Exécuter d'abord: CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
-- Puis: \c s5-bank

-- ============================================================================
-- MICROSERVICE 1: CUSTOMER (Gestion des Clients)
-- ============================================================================
-- Ce microservice gère les profils clients et leurs associations avec les comptes

-- Table principale: Clients
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

COMMENT ON TABLE customers IS 'Profils des clients de la banque';
COMMENT ON COLUMN customers.email IS 'Email unique du client (utilisé pour l''authentification)';
COMMENT ON COLUMN customers.created_at IS 'Date de création du compte client';

-- Table de liaison: Client <-> Comptes d'épargne (Deposit)
CREATE TABLE IF NOT EXISTS customer_deposit_accounts (
    customer_id BIGINT NOT NULL,
    deposit_account_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, deposit_account_id),
    CONSTRAINT fk_customer_deposit FOREIGN KEY (customer_id) 
        REFERENCES customers(id) ON DELETE CASCADE
);

COMMENT ON TABLE customer_deposit_accounts IS 'Association entre clients et comptes d''épargne';
COMMENT ON COLUMN customer_deposit_accounts.deposit_account_id IS 'Référence vers deposit_accounts.id (autre microservice)';

-- Table de liaison: Client <-> Comptes courants (Current)
CREATE TABLE IF NOT EXISTS customer_current_accounts (
    customer_id BIGINT NOT NULL,
    current_account_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, current_account_id),
    CONSTRAINT fk_customer_current FOREIGN KEY (customer_id) 
        REFERENCES customers(id) ON DELETE CASCADE
);

COMMENT ON TABLE customer_current_accounts IS 'Association entre clients et comptes courants';
COMMENT ON COLUMN customer_current_accounts.current_account_id IS 'Référence vers current_accounts.id (autre microservice)';

-- Table de liaison: Client <-> Prêts (Loan)
CREATE TABLE IF NOT EXISTS customer_loans (
    customer_id BIGINT NOT NULL,
    loan_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, loan_id),
    CONSTRAINT fk_customer_loan FOREIGN KEY (customer_id) 
        REFERENCES customers(id) ON DELETE CASCADE
);

COMMENT ON TABLE customer_loans IS 'Association entre clients et prêts';
COMMENT ON COLUMN customer_loans.loan_id IS 'Référence vers loans.id (autre microservice)';

-- ============================================================================
-- MICROSERVICE 2: CURRENT (Comptes Courants)
-- ============================================================================
-- Ce microservice gère les comptes courants et leurs transactions

-- Table principale: Comptes courants
CREATE TABLE IF NOT EXISTS current_accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_current_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED'))
);

COMMENT ON TABLE current_accounts IS 'Comptes courants des clients';
COMMENT ON COLUMN current_accounts.account_number IS 'Numéro de compte unique (ex: CC-2024-001)';
COMMENT ON COLUMN current_accounts.customer_id IS 'Référence vers customers.id';
COMMENT ON COLUMN current_accounts.balance IS 'Solde actuel du compte (peut être négatif si découvert autorisé)';
COMMENT ON COLUMN current_accounts.status IS 'ACTIVE: compte actif, SUSPENDED: suspendu, CLOSED: fermé';

-- Table des transactions: Transactions sur comptes courants
CREATE TABLE IF NOT EXISTS current_transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    balance_after DECIMAL(19,2) NOT NULL,
    description VARCHAR(500),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_current_transaction_account FOREIGN KEY (account_id) 
        REFERENCES current_accounts(id) ON DELETE CASCADE,
    CONSTRAINT chk_current_transaction_type CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'FEE', 'INTEREST')),
    CONSTRAINT chk_current_amount_positive CHECK (amount > 0)
);

COMMENT ON TABLE current_transactions IS 'Historique des transactions sur comptes courants';
COMMENT ON COLUMN current_transactions.type IS 'DEPOSIT: dépôt, WITHDRAWAL: retrait, TRANSFER_IN/OUT: virement, FEE: frais, INTEREST: intérêts';
COMMENT ON COLUMN current_transactions.amount IS 'Montant de la transaction (toujours positif)';
COMMENT ON COLUMN current_transactions.balance_after IS 'Solde du compte après la transaction';

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_current_accounts_customer ON current_accounts(customer_id);
CREATE INDEX IF NOT EXISTS idx_current_transactions_account ON current_transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_current_transactions_date ON current_transactions(transaction_date DESC);

-- ============================================================================
-- MICROSERVICE 3: LOAN (Prêts Bancaires)
-- ============================================================================
-- Ce microservice gère les prêts et leurs remboursements

-- Table principale: Prêts
CREATE TABLE IF NOT EXISTS loans (
    id BIGSERIAL PRIMARY KEY,
    loan_number VARCHAR(255) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    principal_amount DECIMAL(19,2) NOT NULL,
    annual_rate DECIMAL(5,2) NOT NULL,
    duration_months INTEGER NOT NULL,
    total_amount_due DECIMAL(19,2) NOT NULL,
    remaining_amount DECIMAL(19,2) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_loan_status CHECK (status IN ('ACTIVE', 'PAID', 'DEFAULTED', 'CANCELLED')),
    CONSTRAINT chk_loan_principal_positive CHECK (principal_amount > 0),
    CONSTRAINT chk_loan_rate_positive CHECK (annual_rate >= 0),
    CONSTRAINT chk_loan_duration_positive CHECK (duration_months > 0)
);

COMMENT ON TABLE loans IS 'Prêts accordés aux clients';
COMMENT ON COLUMN loans.loan_number IS 'Numéro de prêt unique (ex: LOAN-2024-001)';
COMMENT ON COLUMN loans.customer_id IS 'Référence vers customers.id';
COMMENT ON COLUMN loans.principal_amount IS 'Montant principal emprunté';
COMMENT ON COLUMN loans.annual_rate IS 'Taux d''intérêt annuel (ex: 5.50 pour 5.5%)';
COMMENT ON COLUMN loans.duration_months IS 'Durée du prêt en mois';
COMMENT ON COLUMN loans.total_amount_due IS 'Montant total à rembourser (principal + intérêts)';
COMMENT ON COLUMN loans.remaining_amount IS 'Montant restant à rembourser';
COMMENT ON COLUMN loans.status IS 'ACTIVE: en cours, PAID: remboursé, DEFAULTED: défaut de paiement, CANCELLED: annulé';

-- Table des remboursements: Paiements sur prêts
CREATE TABLE IF NOT EXISTS loan_payments (
    id BIGSERIAL PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    remaining_after_payment DECIMAL(19,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(500),
    CONSTRAINT fk_loan_payment_loan FOREIGN KEY (loan_id) 
        REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT chk_loan_payment_amount_positive CHECK (amount > 0)
);

COMMENT ON TABLE loan_payments IS 'Historique des remboursements de prêts';
COMMENT ON COLUMN loan_payments.amount IS 'Montant du remboursement';
COMMENT ON COLUMN loan_payments.remaining_after_payment IS 'Montant restant dû après ce paiement';
COMMENT ON COLUMN loan_payments.notes IS 'Notes optionnelles (ex: "Remboursement anticipé")';

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_loans_customer ON loans(customer_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_loan_payments_loan ON loan_payments(loan_id);
CREATE INDEX IF NOT EXISTS idx_loan_payments_date ON loan_payments(payment_date DESC);

-- ============================================================================
-- MICROSERVICE 4: DEPOSIT (Comptes d'Épargne) - C# / ASP.NET
-- ============================================================================
-- Ce microservice gère les comptes d'épargne avec taux d'intérêt et limite de retraits

-- Table principale: Comptes d'épargne
CREATE TABLE IF NOT EXISTS deposit_accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    annual_rate DECIMAL(5,2) NOT NULL,
    monthly_withdrawal_limit INTEGER NOT NULL DEFAULT 3,
    withdrawals_this_month INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_deposit_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT chk_deposit_balance_positive CHECK (balance >= 0),
    CONSTRAINT chk_deposit_rate_positive CHECK (annual_rate >= 0),
    CONSTRAINT chk_deposit_withdrawal_limit CHECK (monthly_withdrawal_limit >= 0),
    CONSTRAINT chk_deposit_withdrawals_count CHECK (withdrawals_this_month >= 0)
);

COMMENT ON TABLE deposit_accounts IS 'Comptes d''épargne avec intérêts et limite de retraits';
COMMENT ON COLUMN deposit_accounts.account_number IS 'Numéro de compte unique (ex: DEP-2024-001)';
COMMENT ON COLUMN deposit_accounts.customer_id IS 'Référence vers customers.id';
COMMENT ON COLUMN deposit_accounts.balance IS 'Solde actuel (ne peut pas être négatif)';
COMMENT ON COLUMN deposit_accounts.annual_rate IS 'Taux d''intérêt annuel (ex: 2.50 pour 2.5%)';
COMMENT ON COLUMN deposit_accounts.monthly_withdrawal_limit IS 'Nombre maximum de retraits par mois (généralement 3)';
COMMENT ON COLUMN deposit_accounts.withdrawals_this_month IS 'Nombre de retraits effectués ce mois-ci';

-- Table des transactions: Transactions sur comptes d'épargne
CREATE TABLE IF NOT EXISTS deposit_transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    balance_after DECIMAL(19,2) NOT NULL,
    description VARCHAR(500),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_deposit_transaction_account FOREIGN KEY (account_id) 
        REFERENCES deposit_accounts(id) ON DELETE CASCADE,
    CONSTRAINT chk_deposit_transaction_type CHECK (type IN ('DEPOSIT', 'WITHDRAWAL')),
    CONSTRAINT chk_deposit_amount_positive CHECK (amount > 0)
);

COMMENT ON TABLE deposit_transactions IS 'Historique des transactions sur comptes d''épargne';
COMMENT ON COLUMN deposit_transactions.type IS 'DEPOSIT: dépôt, WITHDRAWAL: retrait';
COMMENT ON COLUMN deposit_transactions.amount IS 'Montant de la transaction (toujours positif)';
COMMENT ON COLUMN deposit_transactions.balance_after IS 'Solde du compte après la transaction';

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_deposit_accounts_customer ON deposit_accounts(customer_id);
CREATE INDEX IF NOT EXISTS idx_deposit_transactions_account ON deposit_transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_deposit_transactions_date ON deposit_transactions(transaction_date DESC);

-- ============================================================================
-- DONNÉES DE TEST (Optionnel)
-- ============================================================================
-- Vous pouvez décommenter cette section pour insérer des données de test

/*
-- Insertion de clients de test
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Jean', 'Dupont', 'jean.dupont@email.com', '0612345678', '10 Rue de Paris, 75001 Paris'),
('Marie', 'Martin', 'marie.martin@email.com', '0623456789', '25 Avenue des Champs, 69001 Lyon'),
('Pierre', 'Bernard', 'pierre.bernard@email.com', '0634567890', '5 Boulevard Victor Hugo, 31000 Toulouse');

-- Insertion de comptes courants
INSERT INTO current_accounts (account_number, customer_id, balance, status) VALUES
('CC-2024-001', 1, 1500.00, 'ACTIVE'),
('CC-2024-002', 2, 2500.50, 'ACTIVE'),
('CC-2024-003', 3, 500.00, 'ACTIVE');

-- Insertion de comptes d'épargne
INSERT INTO deposit_accounts (account_number, customer_id, balance, annual_rate, monthly_withdrawal_limit) VALUES
('DEP-2024-001', 1, 5000.00, 2.50, 3),
('DEP-2024-002', 2, 10000.00, 3.00, 3);

-- Insertion de prêts
INSERT INTO loans (loan_number, customer_id, principal_amount, annual_rate, duration_months, 
                   total_amount_due, remaining_amount, start_date, end_date, status) VALUES
('LOAN-2024-001', 1, 10000.00, 5.50, 24, 10550.00, 10550.00, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 months', 'ACTIVE');

-- Insertion de transactions sur compte courant
INSERT INTO current_transactions (account_id, type, amount, balance_after, description) VALUES
(1, 'DEPOSIT', 1000.00, 1000.00, 'Dépôt initial'),
(1, 'DEPOSIT', 500.00, 1500.00, 'Virement salaire');

-- Insertion de transactions sur compte d'épargne
INSERT INTO deposit_transactions (account_id, type, amount, balance_after, description) VALUES
(1, 'DEPOSIT', 5000.00, 5000.00, 'Dépôt initial');

-- Mise à jour des associations client-comptes
INSERT INTO customer_current_accounts (customer_id, current_account_id) VALUES (1, 1), (2, 2), (3, 3);
INSERT INTO customer_deposit_accounts (customer_id, deposit_account_id) VALUES (1, 1), (2, 2);
INSERT INTO customer_loans (customer_id, loan_id) VALUES (1, 1);
*/

-- ============================================================================
-- VUES UTILES (Optionnel)
-- ============================================================================
-- Vues pour faciliter les requêtes courantes

-- Vue: Résumé des comptes par client
CREATE OR REPLACE VIEW v_customer_accounts_summary AS
SELECT 
    c.id AS customer_id,
    c.first_name,
    c.last_name,
    c.email,
    COUNT(DISTINCT cca.current_account_id) AS current_accounts_count,
    COUNT(DISTINCT cda.deposit_account_id) AS deposit_accounts_count,
    COUNT(DISTINCT cl.loan_id) AS loans_count
FROM customers c
LEFT JOIN customer_current_accounts cca ON c.id = cca.customer_id
LEFT JOIN customer_deposit_accounts cda ON c.id = cda.customer_id
LEFT JOIN customer_loans cl ON c.id = cl.customer_id
GROUP BY c.id, c.first_name, c.last_name, c.email;

COMMENT ON VIEW v_customer_accounts_summary IS 'Résumé du nombre de comptes et prêts par client';

-- Vue: Solde total par client
CREATE OR REPLACE VIEW v_customer_total_balance AS
SELECT 
    c.id AS customer_id,
    c.first_name,
    c.last_name,
    COALESCE(SUM(ca.balance), 0) AS total_current_balance,
    COALESCE(SUM(da.balance), 0) AS total_deposit_balance,
    COALESCE(SUM(ca.balance), 0) + COALESCE(SUM(da.balance), 0) AS total_balance
FROM customers c
LEFT JOIN customer_current_accounts cca ON c.id = cca.customer_id
LEFT JOIN current_accounts ca ON cca.current_account_id = ca.id
LEFT JOIN customer_deposit_accounts cda ON c.id = cda.customer_id
LEFT JOIN deposit_accounts da ON cda.deposit_account_id = da.id
GROUP BY c.id, c.first_name, c.last_name;

COMMENT ON VIEW v_customer_total_balance IS 'Solde total (courant + épargne) par client';

-- ============================================================================
-- FONCTIONS UTILES (Optionnel)
-- ============================================================================

-- Fonction: Calculer le montant total des intérêts d'un prêt
CREATE OR REPLACE FUNCTION calculate_loan_interest(
    p_principal DECIMAL,
    p_annual_rate DECIMAL,
    p_duration_months INTEGER
) RETURNS DECIMAL AS $$
BEGIN
    -- Calcul des intérêts simples: Principal * (Taux * Durée/12)
    RETURN p_principal * (p_annual_rate / 100) * (p_duration_months / 12.0);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION calculate_loan_interest IS 'Calcule le montant total des intérêts pour un prêt';

-- Fonction: Vérifier si un retrait est autorisé sur un compte d'épargne
CREATE OR REPLACE FUNCTION can_withdraw_from_deposit(
    p_account_id BIGINT
) RETURNS BOOLEAN AS $$
DECLARE
    v_withdrawals_this_month INTEGER;
    v_monthly_limit INTEGER;
BEGIN
    SELECT withdrawals_this_month, monthly_withdrawal_limit
    INTO v_withdrawals_this_month, v_monthly_limit
    FROM deposit_accounts
    WHERE id = p_account_id;
    
    RETURN v_withdrawals_this_month < v_monthly_limit;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION can_withdraw_from_deposit IS 'Vérifie si un retrait est autorisé (limite mensuelle non atteinte)';

-- ============================================================================
-- STATISTIQUES ET VÉRIFICATIONS
-- ============================================================================

-- Afficher le résumé de la structure créée
DO $$
BEGIN
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'CRÉATION DE LA BASE DE DONNÉES TERMINÉE';
    RAISE NOTICE '============================================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Tables créées:';
    RAISE NOTICE '  - customers (+ 3 tables de liaison)';
    RAISE NOTICE '  - current_accounts + current_transactions';
    RAISE NOTICE '  - loans + loan_payments';
    RAISE NOTICE '  - deposit_accounts + deposit_transactions';
    RAISE NOTICE '';
    RAISE NOTICE 'Vues créées:';
    RAISE NOTICE '  - v_customer_accounts_summary';
    RAISE NOTICE '  - v_customer_total_balance';
    RAISE NOTICE '';
    RAISE NOTICE 'Fonctions créées:';
    RAISE NOTICE '  - calculate_loan_interest()';
    RAISE NOTICE '  - can_withdraw_from_deposit()';
    RAISE NOTICE '';
    RAISE NOTICE 'Pour vérifier les tables: \dt';
    RAISE NOTICE 'Pour vérifier les vues: \dv';
    RAISE NOTICE 'Pour vérifier les fonctions: \df';
    RAISE NOTICE '============================================================================';
END $$;

-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================
