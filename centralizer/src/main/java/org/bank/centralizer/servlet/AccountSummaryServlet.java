package org.bank.centralizer.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/centralizer/account-summary/*")
public class AccountSummaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Customer ID required\"}");
            return;
        }

        try {
            String customerId = pathInfo.substring(1);

            // This servlet orchestrates calls to multiple modules
            // to get a complete account summary for a customer

            out.println("{");
            out.println("  \"customerId\": " + customerId + ",");
            out.println("  \"message\": \"Account summary orchestration\",");
            out.println("  \"modules\": {");
            out.println("    \"customer\": \"http://localhost:8080/customer/api/customers/" + customerId + "\",");
            out.println("    \"currentAccounts\": \"http://localhost:8080/current/api/current-accounts/customer/" + customerId + "\",");
            out.println("    \"depositAccounts\": \"http://localhost:5000/api/depositaccounts/customer/" + customerId + "\",");
            out.println("    \"loans\": \"http://localhost:8080/loan/api/loans/customer/" + customerId + "\"");
            out.println("  }");
            out.println("}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
