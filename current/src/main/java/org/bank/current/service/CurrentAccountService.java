package org.bank.current.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.bank.current.entity.CurrentAccount;
import org.bank.current.entity.CurrentTransaction;
import org.bank.current.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CurrentAccountService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CurrentAccount createAccount(CurrentAccount account) {
        em.persist(account);
        em.flush();
        return account;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CurrentAccount findAccount(Long id) {
        return em.find(CurrentAccount.class, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CurrentAccount findByAccountNumber(String accountNumber) {
        return em.createQuery("SELECT a FROM CurrentAccount a WHERE a.accountNumber = :accountNumber", CurrentAccount.class)
                .setParameter("accountNumber", accountNumber)
                .getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CurrentAccount> findByCustomerId(Long customerId) {
        return em.createQuery("SELECT a FROM CurrentAccount a WHERE a.customerId = :customerId", CurrentAccount.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CurrentAccount> findAllAccounts() {
        return em.createQuery("SELECT a FROM CurrentAccount a", CurrentAccount.class)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CurrentAccount deposit(Long accountId, BigDecimal amount, String description) {
        CurrentAccount account = findAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        CurrentTransaction transaction = new CurrentTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                newBalance,
                description
        );
        account.addTransaction(transaction);

        em.flush();
        return account;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CurrentAccount withdraw(Long accountId, BigDecimal amount, String description) {
        CurrentAccount account = findAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        CurrentTransaction transaction = new CurrentTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                newBalance,
                description
        );
        account.addTransaction(transaction);

        em.flush();
        return account;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal getBalance(Long accountId) {
        CurrentAccount account = findAccount(accountId);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CurrentTransaction> getTransactionHistory(Long accountId) {
        return em.createQuery("SELECT t FROM CurrentTransaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC", CurrentTransaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CurrentTransaction> getTransactionsByDate(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery("SELECT t FROM CurrentTransaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC", CurrentTransaction.class)
                .setParameter("accountId", accountId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAccount(Long id) {
        CurrentAccount account = findAccount(id);
        if (account != null) {
            em.remove(account);
        }
    }
}
