package org.bank.loan.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.bank.loan.entity.Loan;
import org.bank.loan.entity.LoanPayment;

import java.math.BigDecimal;
import java.util.List;

@Stateless
public class LoanService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Loan createLoan(Loan loan) {
        em.persist(loan);
        em.flush();
        return loan;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Loan findLoan(Long id) {
        return em.find(Loan.class, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Loan findByLoanNumber(String loanNumber) {
        return em.createQuery("SELECT l FROM Loan l WHERE l.loanNumber = :loanNumber", Loan.class)
                .setParameter("loanNumber", loanNumber)
                .getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Loan> findByCustomerId(Long customerId) {
        return em.createQuery("SELECT l FROM Loan l WHERE l.customerId = :customerId", Loan.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Loan> findAllLoans() {
        return em.createQuery("SELECT l FROM Loan l", Loan.class)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Loan> findActiveLoans() {
        return em.createQuery("SELECT l FROM Loan l WHERE l.status = :status", Loan.class)
                .setParameter("status", Loan.LoanStatus.ACTIVE)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Loan makePayment(Long loanId, BigDecimal amount, String notes) {
        Loan loan = findLoan(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new IllegalArgumentException("Loan is not active");
        }

        if (amount.compareTo(loan.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds remaining amount");
        }

        BigDecimal newRemaining = loan.getRemainingAmount().subtract(amount);
        loan.setRemainingAmount(newRemaining);

        LoanPayment payment = new LoanPayment(loan, amount, newRemaining, notes);
        loan.addPayment(payment);

        // Check if loan is fully paid
        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.PAID);
        }

        em.flush();
        return loan;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal getRemainingAmount(Long loanId) {
        Loan loan = findLoan(loanId);
        return loan != null ? loan.getRemainingAmount() : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal getTotalAmountDue(Long loanId) {
        Loan loan = findLoan(loanId);
        return loan != null ? loan.getTotalAmountDue() : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<LoanPayment> getPaymentHistory(Long loanId) {
        return em.createQuery("SELECT p FROM LoanPayment p WHERE p.loan.id = :loanId ORDER BY p.paymentDate DESC", LoanPayment.class)
                .setParameter("loanId", loanId)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Loan updateLoanStatus(Long loanId, Loan.LoanStatus status) {
        Loan loan = findLoan(loanId);
        if (loan != null) {
            loan.setStatus(status);
            em.flush();
            return loan;
        }
        throw new IllegalArgumentException("Loan not found");
    }

    public void deleteLoan(Long id) {
        Loan loan = findLoan(id);
        if (loan != null) {
            em.remove(loan);
        }
    }
}
