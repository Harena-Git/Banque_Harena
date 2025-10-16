-- Database: s5-bank
-- This script creates the database if it doesn't exist

-- Connect to PostgreSQL as superuser and run:
-- CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';

-- Grant privileges if needed:
-- GRANT ALL PRIVILEGES ON DATABASE "s5-bank" TO postgres;

-- The tables will be created automatically by Hibernate and Entity Framework
-- based on the entity classes when the applications start.

-- Verify tables after deployment:
-- \c s5-bank
-- \dt

-- Expected tables:
-- - customers
-- - customer_deposit_accounts
-- - customer_current_accounts
-- - customer_loans
-- - current_accounts
-- - current_transactions
-- - deposit_accounts
-- - deposit_transactions
-- - loans
-- - loan_payments
