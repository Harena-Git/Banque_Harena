package org.bank.customer.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.customer.entity.Customer;
import org.bank.customer.service.CustomerService;

import java.util.List;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @EJB
    private CustomerService customerService;

    @POST
    public Response createCustomer(Customer customer) {
        try {
            Customer created = customerService.createCustomer(customer);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        Customer customer = customerService.findCustomer(id);
        if (customer != null) {
            return Response.ok(customer).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Customer not found\"}").build();
    }

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = customerService.findAllCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("/email/{email}")
    public Response getCustomerByEmail(@PathParam("email") String email) {
        try {
            Customer customer = customerService.findByEmail(email);
            return Response.ok(customer).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Customer not found\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer customer) {
        customer.setId(id);
        Customer updated = customerService.updateCustomer(customer);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.ok("{\"message\": \"Customer deleted\"}").build();
    }

    @POST
    @Path("/{id}/deposit-accounts/{accountId}")
    public Response addDepositAccount(@PathParam("id") Long id, @PathParam("accountId") Long accountId) {
        customerService.addDepositAccount(id, accountId);
        return Response.ok("{\"message\": \"Deposit account added\"}").build();
    }

    @POST
    @Path("/{id}/current-accounts/{accountId}")
    public Response addCurrentAccount(@PathParam("id") Long id, @PathParam("accountId") Long accountId) {
        customerService.addCurrentAccount(id, accountId);
        return Response.ok("{\"message\": \"Current account added\"}").build();
    }

    @POST
    @Path("/{id}/loans/{loanId}")
    public Response addLoan(@PathParam("id") Long id, @PathParam("loanId") Long loanId) {
        customerService.addLoan(id, loanId);
        return Response.ok("{\"message\": \"Loan added\"}").build();
    }
}
