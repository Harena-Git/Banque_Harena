using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace deposit.Models
{
    [Table("deposit_accounts")]
    public class DepositAccount
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Required]
        [Column("account_number")]
        [StringLength(50)]
        public string AccountNumber { get; set; } = string.Empty;

        [Required]
        [Column("customer_id")]
        public long CustomerId { get; set; }

        [Required]
        [Column("balance", TypeName = "decimal(19,2)")]
        public decimal Balance { get; set; }

        [Required]
        [Column("annual_rate", TypeName = "decimal(5,2)")]
        public decimal AnnualRate { get; set; }

        [Required]
        [Column("monthly_withdrawal_limit")]
        public int MonthlyWithdrawalLimit { get; set; }

        [Required]
        [Column("withdrawals_this_month")]
        public int WithdrawalsThisMonth { get; set; }

        [Required]
        [Column("status")]
        [StringLength(20)]
        public string Status { get; set; } = "ACTIVE";

        [Required]
        [Column("created_at")]
        public DateTime CreatedAt { get; set; }

        [Column("updated_at")]
        public DateTime? UpdatedAt { get; set; }

        // Navigation property
        public virtual ICollection<DepositTransaction> Transactions { get; set; } = new List<DepositTransaction>();

        public DepositAccount()
        {
            CreatedAt = DateTime.UtcNow;
            Balance = 0;
            WithdrawalsThisMonth = 0;
        }
    }

    public enum AccountStatus
    {
        ACTIVE,
        SUSPENDED,
        CLOSED
    }
}
