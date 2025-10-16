using Microsoft.EntityFrameworkCore;
using deposit.Models;

namespace deposit.Data
{
    public class BankDbContext : DbContext
    {
        public BankDbContext(DbContextOptions<BankDbContext> options) : base(options)
        {
        }

        public DbSet<DepositAccount> DepositAccounts { get; set; }
        public DbSet<DepositTransaction> DepositTransactions { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configure DepositAccount
            modelBuilder.Entity<DepositAccount>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.AccountNumber).IsUnique();
                entity.HasIndex(e => e.CustomerId);

                entity.HasMany(e => e.Transactions)
                      .WithOne(e => e.Account)
                      .HasForeignKey(e => e.AccountId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // Configure DepositTransaction
            modelBuilder.Entity<DepositTransaction>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.AccountId);
                entity.HasIndex(e => e.TransactionDate);
            });
        }
    }
}
