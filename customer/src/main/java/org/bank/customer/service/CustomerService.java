package org.bank.customer.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.bank.customer.entity.Customer;

import java.util.List;

@Stateless
public class CustomerService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer createCustomer(Customer customer) {
        em.persist(customer);
        em.flush();
        return customer;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer findCustomer(Long id) {
        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            // Force initialization of collections
            customer.getDepositAccountIds().size();
            customer.getCurrentAccountIds().size();
            customer.getLoanIds().size();
        }
        return customer;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer findByEmail(String email) {
        Customer customer = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                .setParameter("email", email)
                .getSingleResult();
        if (customer != null) {
            // Force initialization of collections
            customer.getDepositAccountIds().size();
            customer.getCurrentAccountIds().size();
            customer.getLoanIds().size();
        }
        return customer;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Customer> findAllCustomers() {
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();
        // Force initialization of collections for all customers
        for (Customer customer : customers) {
            customer.getDepositAccountIds().size();
            customer.getCurrentAccountIds().size();
            customer.getLoanIds().size();
        }
        return customers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer updateCustomer(Customer customer) {
        Customer existing = em.find(Customer.class, customer.getId());
        if (existing != null) {
            // Update only the basic fields, not the collections
            existing.setFirstName(customer.getFirstName());
            existing.setLastName(customer.getLastName());
            existing.setEmail(customer.getEmail());
            existing.setPhone(customer.getPhone());
            existing.setAddress(customer.getAddress());

            // Don't touch the collections unless explicitly provided
            if (customer.getDepositAccountIds() != null && !customer.getDepositAccountIds().isEmpty()) {
                existing.getDepositAccountIds().clear();
                existing.getDepositAccountIds().addAll(customer.getDepositAccountIds());
            }
            if (customer.getCurrentAccountIds() != null && !customer.getCurrentAccountIds().isEmpty()) {
                existing.getCurrentAccountIds().clear();
                existing.getCurrentAccountIds().addAll(customer.getCurrentAccountIds());
            }
            if (customer.getLoanIds() != null && !customer.getLoanIds().isEmpty()) {
                existing.getLoanIds().clear();
                existing.getLoanIds().addAll(customer.getLoanIds());
            }

            em.flush();
            return existing;
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCustomer(Long id) {
        Customer customer = findCustomer(id);
        if (customer != null) {
            em.remove(customer);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addDepositAccount(Long customerId, Long accountId) {
        Customer customer = findCustomer(customerId);
        if (customer != null) {
            customer.getDepositAccountIds().add(accountId);
            em.merge(customer);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addCurrentAccount(Long customerId, Long accountId) {
        Customer customer = findCustomer(customerId);
        if (customer != null) {
            customer.getCurrentAccountIds().add(accountId);
            em.merge(customer);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addLoan(Long customerId, Long loanId) {
        Customer customer = findCustomer(customerId);
        if (customer != null) {
            customer.getLoanIds().add(loanId);
            em.merge(customer);
        }
    }
}
