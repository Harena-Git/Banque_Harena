package org.bank.centralizer.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bank.current.service.CurrentAccountService;
import org.bank.current.entity.CurrentAccount;
import org.bank.current.entity.CurrentTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/current-accounts/*")
public class CurrentAccountServlet extends HttpServlet {

    @EJB
    private CurrentAccountService currentAccountService;

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
                // GET /current-accounts - List all accounts
                List<CurrentAccount> accounts = currentAccountService.findAllAccounts();
                String json = objectMapper.writeValueAsString(accounts);
                out.println(json);
            } else {
                String[] pathParts = pathInfo.split("/");

                // GET /current-accounts/customer/{customerId}
                if (pathParts.length >= 3 && "customer".equals(pathParts[1])) {
                    Long customerId = Long.parseLong(pathParts[2]);
                    List<CurrentAccount> accounts = currentAccountService.findByCustomerId(customerId);
                    String json = objectMapper.writeValueAsString(accounts);
                    out.println(json);
                }
                // GET /current-accounts/{id}/transactions
                else if (pathParts.length >= 3 && "transactions".equals(pathParts[2])) {
                    Long accountId = Long.parseLong(pathParts[1]);
                    List<CurrentTransaction> transactions = currentAccountService.getTransactionHistory(accountId);
                    String json = objectMapper.writeValueAsString(transactions);
                    out.println(json);
                }
                // GET /current-accounts/{id}/balance
                else if (pathParts.length >= 3 && "balance".equals(pathParts[2])) {
                    Long accountId = Long.parseLong(pathParts[1]);
                    BigDecimal balance = currentAccountService.getBalance(accountId);
                    out.println("{\"balance\": " + balance + "}");
                }
                // GET /current-accounts/{id}
                else if (pathParts.length >= 2) {
                    Long accountId = Long.parseLong(pathParts[1]);
                    CurrentAccount account = currentAccountService.findAccount(accountId);

                    if (account != null) {
                        String json = objectMapper.writeValueAsString(account);
                        out.println(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"Account not found\"}");
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid ID format\"}");
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
            // POST /current-accounts/{id}/deposit
            if (pathInfo != null && pathInfo.contains("/deposit")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    Long accountId = Long.parseLong(pathParts[1]);

                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    Map<String, Object> data = objectMapper.readValue(sb.toString(), Map.class);
                    BigDecimal amount = new BigDecimal(data.get("amount").toString());
                    String description = data.getOrDefault("description", "Deposit").toString();

                    CurrentAccount account = currentAccountService.deposit(accountId, amount, description);
                    String json = objectMapper.writeValueAsString(account);
                    out.println(json);
                    return;
                }
            }

            // POST /current-accounts/{id}/withdraw
            if (pathInfo != null && pathInfo.contains("/withdraw")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    Long accountId = Long.parseLong(pathParts[1]);

                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    Map<String, Object> data = objectMapper.readValue(sb.toString(), Map.class);
                    BigDecimal amount = new BigDecimal(data.get("amount").toString());
                    String description = data.getOrDefault("description", "Withdrawal").toString();

                    CurrentAccount account = currentAccountService.withdraw(accountId, amount, description);
                    String json = objectMapper.writeValueAsString(account);
                    out.println(json);
                    return;
                }
            }

            // POST /current-accounts - Create new account
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            CurrentAccount account = objectMapper.readValue(sb.toString(), CurrentAccount.class);
            CurrentAccount created = currentAccountService.createAccount(account);

            response.setStatus(HttpServletResponse.SC_CREATED);
            String json = objectMapper.writeValueAsString(created);
            out.println(json);

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
                    Long accountId = Long.parseLong(pathParts[1]);
                    currentAccountService.deleteAccount(accountId);
                    out.println("{\"message\": \"Account deleted successfully\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

