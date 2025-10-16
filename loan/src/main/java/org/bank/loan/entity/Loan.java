package org.bank.loan.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loanNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualRate;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmountDue;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanPayment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
        calculateTotalAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private void calculateTotalAmount() {
        // Simple interest calculation: Total = Principal * (1 + rate * duration/12)
        BigDecimal interestMultiplier = annualRate
                .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(durationMonths))
                .divide(BigDecimal.valueOf(12), 4, BigDecimal.ROUND_HALF_UP)
                .add(BigDecimal.ONE);

        totalAmountDue = principalAmount.multiply(interestMultiplier)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        if (remainingAmount == null) {
            remainingAmount = totalAmountDue;
        }
    }

    // Constructors
    public Loan() {
    }

    public Loan(String loanNumber, Long customerId, BigDecimal principalAmount, 
                BigDecimal annualRate, Integer durationMonths) {
        this.loanNumber = loanNumber;
        this.customerId = customerId;
        this.principalAmount = principalAmount;
        this.annualRate = annualRate;
        this.durationMonths = durationMonths;
        this.startDate = LocalDateTime.now();
        this.endDate = startDate.plusMonths(durationMonths);
        this.status = LoanStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(BigDecimal totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<LoanPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<LoanPayment> payments) {
        this.payments = payments;
    }

    // Helper methods
    public void addPayment(LoanPayment payment) {
        payments.add(payment);
        payment.setLoan(this);
    }

    public void removePayment(LoanPayment payment) {
        payments.remove(payment);
        payment.setLoan(null);
    }

    public enum LoanStatus {
        ACTIVE, PAID, DEFAULTED, CANCELLED
    }
}
