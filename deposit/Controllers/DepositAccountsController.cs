using deposit.Models;
using deposit.Services;
using Microsoft.AspNetCore.Mvc;

namespace deposit.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DepositAccountsController : ControllerBase
    {
        private readonly DepositAccountService _service;

        public DepositAccountsController(DepositAccountService service)
        {
            _service = service;
        }

        [HttpPost]
        public async Task<ActionResult<DepositAccount>> CreateAccount([FromBody] DepositAccount account)
        {
            try
            {
                var created = await _service.CreateAccountAsync(account);
                return CreatedAtAction(nameof(GetAccount), new { id = created.Id }, created);
            }
            catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<DepositAccount>> GetAccount(long id)
        {
            var account = await _service.GetAccountAsync(id);
            if (account == null)
                return NotFound(new { error = "Account not found" });

            return Ok(account);
        }

        [HttpGet]
        public async Task<ActionResult<List<DepositAccount>>> GetAllAccounts()
        {
            var accounts = await _service.GetAllAccountsAsync();
            return Ok(accounts);
        }

        [HttpGet("customer/{customerId}")]
        public async Task<ActionResult<List<DepositAccount>>> GetAccountsByCustomer(long customerId)
        {
            var accounts = await _service.GetAccountsByCustomerAsync(customerId);
            return Ok(accounts);
        }

        [HttpGet("number/{accountNumber}")]
        public async Task<ActionResult<DepositAccount>> GetAccountByNumber(string accountNumber)
        {
            var account = await _service.GetAccountByNumberAsync(accountNumber);
            if (account == null)
                return NotFound(new { error = "Account not found" });

            return Ok(account);
        }

        [HttpPost("{id}/deposit")]
        public async Task<ActionResult<DepositAccount>> Deposit(long id, [FromBody] TransactionRequest request)
        {
            try
            {
                var account = await _service.DepositAsync(id, request.Amount, request.Description);
                return Ok(account);
            }
            catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpPost("{id}/withdraw")]
        public async Task<ActionResult<DepositAccount>> Withdraw(long id, [FromBody] TransactionRequest request)
        {
            try
            {
                var account = await _service.WithdrawAsync(id, request.Amount, request.Description);
                return Ok(account);
            }
            catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpGet("{id}/balance")]
        public async Task<ActionResult<object>> GetBalance(long id)
        {
            var balance = await _service.GetBalanceAsync(id);
            return Ok(new { balance });
        }

        [HttpGet("{id}/transactions")]
        public async Task<ActionResult<List<DepositTransaction>>> GetTransactions(long id)
        {
            var transactions = await _service.GetTransactionHistoryAsync(id);
            return Ok(transactions);
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAccount(long id)
        {
            await _service.DeleteAccountAsync(id);
            return Ok(new { message = "Account deleted" });
        }
    }

    public class TransactionRequest
    {
        public decimal Amount { get; set; }
        public string? Description { get; set; }
    }
}
