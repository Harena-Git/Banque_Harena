using deposit.Data;
using deposit.Models;
using Microsoft.EntityFrameworkCore;

namespace deposit.Services
{
    public class DepositAccountService
    {
        private readonly BankDbContext _context;

        public DepositAccountService(BankDbContext context)
        {
            _context = context;
        }

        public async Task<DepositAccount> CreateAccountAsync(DepositAccount account)
        {
            _context.DepositAccounts.Add(account);
            await _context.SaveChangesAsync();
            return account;
        }

        public async Task<DepositAccount?> GetAccountAsync(long id)
        {
            return await _context.DepositAccounts
                .Include(a => a.Transactions)
                .FirstOrDefaultAsync(a => a.Id == id);
        }

        public async Task<DepositAccount?> GetAccountByNumberAsync(string accountNumber)
        {
            return await _context.DepositAccounts
                .Include(a => a.Transactions)
                .FirstOrDefaultAsync(a => a.AccountNumber == accountNumber);
        }

        public async Task<List<DepositAccount>> GetAccountsByCustomerAsync(long customerId)
        {
            return await _context.DepositAccounts
                .Where(a => a.CustomerId == customerId)
                .Include(a => a.Transactions)
                .ToListAsync();
        }

        public async Task<List<DepositAccount>> GetAllAccountsAsync()
        {
            return await _context.DepositAccounts
                .Include(a => a.Transactions)
                .ToListAsync();
        }

        public async Task<DepositAccount> DepositAsync(long accountId, decimal amount, string? description)
        {
            var account = await GetAccountAsync(accountId);
            if (account == null)
                throw new InvalidOperationException("Account not found");

            account.Balance += amount;

            var transaction = new DepositTransaction
            {
                AccountId = accountId,
                Type = "DEPOSIT",
                Amount = amount,
                BalanceAfter = account.Balance,
                Description = description ?? "Deposit"
            };

            _context.DepositTransactions.Add(transaction);
            await _context.SaveChangesAsync();

            return account;
        }

        public async Task<DepositAccount> WithdrawAsync(long accountId, decimal amount, string? description)
        {
            var account = await GetAccountAsync(accountId);
            if (account == null)
                throw new InvalidOperationException("Account not found");

            // Check monthly withdrawal limit
            if (account.WithdrawalsThisMonth >= account.MonthlyWithdrawalLimit)
                throw new InvalidOperationException("Monthly withdrawal limit reached");

            if (account.Balance < amount)
                throw new InvalidOperationException("Insufficient balance");

            account.Balance -= amount;
            account.WithdrawalsThisMonth++;

            var transaction = new DepositTransaction
            {
                AccountId = accountId,
                Type = "WITHDRAWAL",
                Amount = amount,
                BalanceAfter = account.Balance,
                Description = description ?? "Withdrawal"
            };

            _context.DepositTransactions.Add(transaction);
            await _context.SaveChangesAsync();

            return account;
        }

        public async Task<decimal> GetBalanceAsync(long accountId)
        {
            var account = await GetAccountAsync(accountId);
            return account?.Balance ?? 0;
        }

        public async Task<List<DepositTransaction>> GetTransactionHistoryAsync(long accountId)
        {
            return await _context.DepositTransactions
                .Where(t => t.AccountId == accountId)
                .OrderByDescending(t => t.TransactionDate)
                .ToListAsync();
        }

        public async Task<List<DepositTransaction>> GetTransactionsByDateAsync(long accountId, DateTime startDate, DateTime endDate)
        {
            return await _context.DepositTransactions
                .Where(t => t.AccountId == accountId && t.TransactionDate >= startDate && t.TransactionDate <= endDate)
                .OrderByDescending(t => t.TransactionDate)
                .ToListAsync();
        }

        public async Task ResetMonthlyWithdrawalsAsync()
        {
            var accounts = await _context.DepositAccounts.ToListAsync();
            foreach (var account in accounts)
            {
                account.WithdrawalsThisMonth = 0;
            }
            await _context.SaveChangesAsync();
        }

        public async Task DeleteAccountAsync(long id)
        {
            var account = await GetAccountAsync(id);
            if (account != null)
            {
                _context.DepositAccounts.Remove(account);
                await _context.SaveChangesAsync();
            }
        }
    }
}
