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
- **WildFly Application Server**
- **PostgreSQL Database**
- **Maven Build Tool**

---

## 🗄️ Database

### Database Structure

The application uses a **single PostgreSQL database** (`s5-bank`) shared by all microservices.

**Documentation:**
- **📄 SQL Script**: `Create.sql` - Complete database creation script
- **📖 Detailed Explanation**: `DATABASE-EXPLANATION.md` - Full documentation of tables and relationships
- **📊 Visual Diagram**: `DATABASE-DIAGRAM.txt` - ASCII diagram of the database structure

**Quick Setup:**
```sql
-- Execute the complete script
psql -U postgres -d s5-bank -f Create.sql
```

### Database Overview

| Microservice | Tables | Description |
|--------------|--------|-------------|
| **Customer** | 4 tables | Customer profiles and account associations |
| **Current** | 2 tables | Current accounts and transactions |
| **Loan** | 2 tables | Loans and repayments |
| **Deposit** | 2 tables | Savings accounts and transactions |

**Total**: 10 tables + 2 views + 2 functions

---

## 🚀 Deployment

### Quick Start

1. **Prerequisites**
   - Java JDK 11+
   - Maven
   - PostgreSQL
   - WildFly 27+
   - PostgreSQL JDBC Driver (postgresql-42.7.3.jar)

2. **Download PostgreSQL Driver**
   - Download from: https://jdbc.postgresql.org/download/
   - Save to: `C:\temp\postgresql-42.7.3.jar`

3. **Create Database**
   ```sql
   -- Option 1: Simple creation
   CREATE DATABASE "s5-bank" WITH ENCODING 'UTF8';
   
   -- Option 2: Use the complete script
   psql -U postgres -d s5-bank -f Create.sql
   ```

4. **Configure and Deploy**
   - Edit `wildfly-scripts\setup-wildfly.bat` with your paths
   - Run the automated script:
   ```cmd
   wildfly-scripts\setup-wildfly.bat
   ```

5. **Access Application**
   - URL: http://localhost:8080/centralizer

### Documentation

- **📋 Deployment Checklist**: `DEPLOYMENT-CHECKLIST.md`
- **📖 Complete Setup Guide**: `WILDFLY-SETUP-GUIDE.md`
- **🔧 Configuration Scripts**: `wildfly-scripts/` folder
- **🗄️ Database Documentation**: `DATABASE-EXPLANATION.md`

### Project Structure

```
Banque_Harena/
├── current/                    # Current accounts module (Java EJB)
├── loan/                       # Loans module (Java EJB)
├── customer/                   # Customers module (Java EJB)
├── deposit/                    # Deposit accounts module (C# ASP.NET)
├── centralizer/                # Web interface (Java Servlet)
├── Bank-ear/                   # EAR packaging module
├── wildfly-scripts/            # Deployment automation scripts
├── pom.xml                     # Maven parent POM
├── Create.sql                  # Complete database creation script
├── DATABASE-EXPLANATION.md     # Database documentation
├── DATABASE-DIAGRAM.txt        # Visual database diagram
├── DEPLOYMENT-CHECKLIST.md     # Deployment checklist
└── WILDFLY-SETUP-GUIDE.md      # Complete setup guide
```

---

## ✨ Author

**Manakasina Judicaël Jean François**
_Academic project – Distributed architecture_
