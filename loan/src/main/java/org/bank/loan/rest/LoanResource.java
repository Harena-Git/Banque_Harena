package org.bank.loan.rest;

import jakarta.ejb.EJB;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.loan.entity.Loan;
import org.bank.loan.entity.LoanPayment;
import org.bank.loan.service.LoanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/loans")
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    @EJB
    private LoanService loanService;

    @POST
    public Response createLoan(Loan loan) {
        try {
            Loan created = loanService.createLoan(loan);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getLoan(@PathParam("id") Long id) {
        Loan loan = loanService.findLoan(id);
        if (loan != null) {
            return Response.ok(loan).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Loan not found\"}").build();
    }

    @GET
    public Response getAllLoans() {
        List<Loan> loans = loanService.findAllLoans();
        return Response.ok(loans).build();
    }

    @GET
    @Path("/active")
    public Response getActiveLoans() {
        List<Loan> loans = loanService.findActiveLoans();
        return Response.ok(loans).build();
    }

    @GET
    @Path("/customer/{customerId}")
    public Response getLoansByCustomer(@PathParam("customerId") Long customerId) {
        List<Loan> loans = loanService.findByCustomerId(customerId);
        return Response.ok(loans).build();
    }

    @GET
    @Path("/number/{loanNumber}")
    public Response getLoanByNumber(@PathParam("loanNumber") String loanNumber) {
        try {
            Loan loan = loanService.findByLoanNumber(loanNumber);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Loan not found\"}").build();
        }
    }

    @POST
    @Path("/{id}/payment")
    public Response makePayment(@PathParam("id") Long id, Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String notes = request.getOrDefault("notes", "").toString();

            Loan loan = loanService.makePayment(id, amount, notes);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}/remaining")
    public Response getRemainingAmount(@PathParam("id") Long id) {
        try {
            BigDecimal remaining = loanService.getRemainingAmount(id);
            return Response.ok("{\"remainingAmount\": " + remaining + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Loan not found\"}").build();
        }
    }

    @GET
    @Path("/{id}/total")
    public Response getTotalAmountDue(@PathParam("id") Long id) {
        try {
            BigDecimal total = loanService.getTotalAmountDue(id);
            return Response.ok("{\"totalAmountDue\": " + total + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Loan not found\"}").build();
        }
    }

    @GET
    @Path("/{id}/payments")
    public Response getPaymentHistory(@PathParam("id") Long id) {
        List<LoanPayment> payments = loanService.getPaymentHistory(id);
        return Response.ok(payments).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, Map<String, String> request) {
        try {
            Loan.LoanStatus status = Loan.LoanStatus.valueOf(request.get("status"));
            Loan loan = loanService.updateLoanStatus(id, status);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLoan(@PathParam("id") Long id) {
        loanService.deleteLoan(id);
        return Response.ok("{\"message\": \"Loan deleted\"}").build();
    }
}
