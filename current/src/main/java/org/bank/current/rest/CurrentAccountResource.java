package org.bank.current.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.current.entity.CurrentAccount;
import org.bank.current.entity.CurrentTransaction;
import org.bank.current.service.CurrentAccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/current-accounts")
@Consumes(MediaType.APPLICATION_JSON)
public class CurrentAccountResource {

    @EJB
    private CurrentAccountService accountService;

    @POST
    public Response createAccount(CurrentAccount account) {
        try {
            CurrentAccount created = accountService.createAccount(account);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        CurrentAccount account = accountService.findAccount(id);
        if (account != null) {
            return Response.ok(account).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Account not found\"}").build();
    }

    @GET
    public Response getAllAccounts() {
        List<CurrentAccount> accounts = accountService.findAllAccounts();
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/customer/{customerId}")
    public Response getAccountsByCustomer(@PathParam("customerId") Long customerId) {
        List<CurrentAccount> accounts = accountService.findByCustomerId(customerId);
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/number/{accountNumber}")
    public Response getAccountByNumber(@PathParam("accountNumber") String accountNumber) {
        try {
            CurrentAccount account = accountService.findByAccountNumber(accountNumber);
            return Response.ok(account).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Account not found\"}").build();
        }
    }

    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.getOrDefault("description", "Deposit").toString();

            CurrentAccount account = accountService.deposit(id, amount, description);
            return Response.ok(account).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/{id}/withdraw")
    public Response withdraw(@PathParam("id") Long id, Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.getOrDefault("description", "Withdrawal").toString();

            CurrentAccount account = accountService.withdraw(id, amount, description);
            return Response.ok(account).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}/balance")
    public Response getBalance(@PathParam("id") Long id) {
        try {
            BigDecimal balance = accountService.getBalance(id);
            return Response.ok("{\"balance\": " + balance + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Account not found\"}").build();
        }
    }

    @GET
    @Path("/{id}/transactions")
    public Response getTransactions(@PathParam("id") Long id) {
        List<CurrentTransaction> transactions = accountService.getTransactionHistory(id);
        return Response.ok(transactions).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        accountService.deleteAccount(id);
        return Response.ok("{\"message\": \"Account deleted\"}").build();
    }
}
