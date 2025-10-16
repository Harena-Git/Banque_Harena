# 🏦 Bank Project

## 📖 Description

**Bank Project** is a **bank management** application based on a **microservices architecture**.
Each module manages a specific type of account or operation, and a **Java centralizer** coordinates all communications between them.

---

## ⚙️ General Architecture

The application consists of **4 main modules** and a **centralizer**:

| Module        | Language / Technology | Main Role                          |
|---------------|----------------------|-------------------------------------|
| **Customer**  | Java (EJB)           | Management of customer profiles     |
| **Deposit**   | C# (ASP.NET)         | Management of deposit accounts      |
| **Current**   | Java (EJB)           | Management of current accounts      |
| **Loan**      | Java (EJB)           | Management of loans                 |
| **Centralizer** | Java (Servlet)     | Communication between microservices |

---

## 🔗 Communication Between Services

- **Java ↔ Java**: via **EJB**
- **Java ↔ C#**: via **HTTP** (C# modules expose **REST APIs**)

---

## 💡 Modules and Features

### 🧑 Customer Module (Java / EJB)
- Creation and management of customer profiles
- Association of accounts (deposit, current, loan) to customers
- Viewing customer details and account summary

---

### 🧾 Deposit Module (C# / ASP.NET)
- Deposit and withdrawal on a deposit account
    - *With a monthly withdrawal limit and annual rate*
- Balance inquiry at a given date
- Transaction history on the deposit account
- Creation of a deposit account for a user

---

### 💳 Current Module (Java / EJB)
- Deposit and withdrawal on a current account
- Balance inquiry at a given date
- Transaction history on the current account
- Creation of a current account for a user

---

### 💰 Loan Module (Java or C#)
- Subscription to a loan (with annual rate)
- Loan repayment
- Inquiry of the total amount to be repaid to the bank

---

## 🧠 Notes

- No functional specifications are imposed: features are freely chosen.
- The **Java centralizer (Servlet)** orchestrates all calls to the microservices.
- Modules can be developed and deployed independently.

---

## 🧩 Technologies Used

- **Java (EJB, Servlet)**
- **C# (ASP.NET)**
- **HTTP / REST API**
- **Microservices architecture**

---

## ✨ Author

**Manakasina Judicaël Jean François**
_Academic project – Distributed architecture_
