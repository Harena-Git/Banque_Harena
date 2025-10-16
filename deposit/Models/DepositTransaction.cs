using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace deposit.Models
{
    [Table("deposit_transactions")]
    public class DepositTransaction
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Required]
        [Column("account_id")]
        public long AccountId { get; set; }

        [Required]
        [Column("type")]
        [StringLength(20)]
        public string Type { get; set; } = string.Empty;

        [Required]
        [Column("amount", TypeName = "decimal(19,2)")]
        public decimal Amount { get; set; }

        [Required]
        [Column("balance_after", TypeName = "decimal(19,2)")]
        public decimal BalanceAfter { get; set; }

        [Column("description")]
        [StringLength(500)]
        public string? Description { get; set; }

        [Required]
        [Column("transaction_date")]
        public DateTime TransactionDate { get; set; }

        // Navigation property
        [ForeignKey("AccountId")]
        public virtual DepositAccount? Account { get; set; }

        public DepositTransaction()
        {
            TransactionDate = DateTime.UtcNow;
        }
    }

    public enum TransactionType
    {
        DEPOSIT,
        WITHDRAWAL
    }
}
