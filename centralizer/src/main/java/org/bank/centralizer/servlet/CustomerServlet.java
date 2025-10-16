package org.bank.centralizer.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bank.customer.service.CustomerService;
import org.bank.customer.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.List;

@WebServlet("/customers/*")
public class CustomerServlet extends HttpServlet {

    @EJB
    private CustomerService customerService;

    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all customers
                List<Customer> customers = customerService.findAllCustomers();
                String json = objectMapper.writeValueAsString(customers);
                out.println(json);
            } else {
                String[] pathParts = pathInfo.split("/");

                // GET /customers/email/{email}
                if (pathParts.length >= 3 && "email".equals(pathParts[1])) {
                    String email = pathParts[2];
                    try {
                        Customer customer = customerService.findByEmail(email);
                        String json = objectMapper.writeValueAsString(customer);
                        out.println(json);
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"Customer not found with email: " + email + "\"}");
                    }
                }
                // GET /customers/{id}
                else if (pathParts.length >= 2) {
                    Long customerId = Long.parseLong(pathParts[1]);
                    Customer customer = customerService.findCustomer(customerId);

                    if (customer != null) {
                        String json = objectMapper.writeValueAsString(customer);
                        out.println(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"Customer not found\"}");
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid customer ID\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            // POST /customers/{id}/deposit-accounts/{accountId}
            if (pathInfo != null && pathInfo.contains("/deposit-accounts/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 4) {
                    Long customerId = Long.parseLong(pathParts[1]);
                    Long accountId = Long.parseLong(pathParts[3]);
                    customerService.addDepositAccount(customerId, accountId);
                    out.println("{\"message\": \"Deposit account added successfully\"}");
                    return;
                }
            }

            // POST /customers/{id}/current-accounts/{accountId}
            if (pathInfo != null && pathInfo.contains("/current-accounts/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 4) {
                    Long customerId = Long.parseLong(pathParts[1]);
                    Long accountId = Long.parseLong(pathParts[3]);
                    customerService.addCurrentAccount(customerId, accountId);
                    out.println("{\"message\": \"Current account added successfully\"}");
                    return;
                }
            }

            // POST /customers/{id}/loans/{loanId}
            if (pathInfo != null && pathInfo.contains("/loans/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 4) {
                    Long customerId = Long.parseLong(pathParts[1]);
                    Long loanId = Long.parseLong(pathParts[3]);
                    customerService.addLoan(customerId, loanId);
                    out.println("{\"message\": \"Loan added successfully\"}");
                    return;
                }
            }

            // POST /customers - Create new customer
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Customer customer = objectMapper.readValue(sb.toString(), Customer.class);
            Customer created = customerService.createCustomer(customer);

            response.setStatus(HttpServletResponse.SC_CREATED);
            String json = objectMapper.writeValueAsString(created);
            out.println(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    Long customerId = Long.parseLong(pathParts[1]);

                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    Customer customer = objectMapper.readValue(sb.toString(), Customer.class);
                    customer.setId(customerId);

                    Customer updated = customerService.updateCustomer(customer);

                    String json = objectMapper.writeValueAsString(updated);
                    out.println(json);
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    Long customerId = Long.parseLong(pathParts[1]);
                    customerService.deleteCustomer(customerId);
                    out.println("{\"message\": \"Customer deleted successfully\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
