This document outlines the requirements for the **Backend Programming** final exam project at **EPICODE Institute of Technology**.

## Project Overview

The objective is to build a complete backend application using **Spring and PostgreSQL**. This practical project accounts for **50% of the final grade**, with the remaining 50% assessed via an oral examination.

The application must demonstrate:

* Robust server-side features and data persistence.

* Validation, authentication, and business logic structuring.

* Seamless interaction with databases and external services.

* Good coding practices and a well-structured codebase.

---

## General Requirements

### 1. Project Theme

* You are free to choose any theme for the application (e.g., e-commerce, task manager, social media dashboard).

### 2. Entities and Domain Model

* The domain model must include at least **eight tables**.

* Relationships must be coherent and meaningful.

* The model must contain at least one **inheritance structure** to justify a domain hierarchy.

### 3. User Management

* A complete user management system is required.

* Each user must have an email, password, and an updatable profile image.

* Profiles must include personal details such as name, surname, and registration date.

### 4. Authentication and Authorization

* Security must be implemented using **JWT (JSON Web Tokens)**.

* The system must include at least **three distinct roles**, each with specific permissions and access rules.

### 5. REST APIs and Error Handling

* The system must expose REST APIs following consistent principles for requests and responses.

* All incoming data must be validated.

* Errors must be handled through structured and meaningful responses.

### 6. Queries and Data Manipulation

* Queries should support real use cases and be implemented efficiently.

* Implementation can use JPA query methods, JPQL, or native SQL.

* Features must include filtering, sorting, aggregations, and multi-condition queries.

### 7. Third-Party Integration

* The backend must interact with at least **two third-party APIs**.

* Retrieved information must be meaningfully incorporated into the application logic or functionality.

---

## Supporting Material

To be eligible for grading, the following materials must be provided:

*

**GitHub Repository:** Must include all code, running instructions, and a comprehensive **README.md** detailing features and environment variables.

*

**Postman Collection:** A JSON file containing all requests needed to test every feature.

* Each request must include example payloads, parameters, and headers.

*

**Note:** Any functionality not included in the Postman collection will not be evaluated.

---

## Evaluation and Optional Features

*

**Mandatory Requirements:** Failure to meet general requirements or security best practices will result in penalties.

*

**Extra Points:** Optional features can be added for extra credit, such as:

* Integration with additional third-party APIs beyond the required two.
* A dedicated **GraphQL** section.
* Particularly complex or highly optimized queries.

>
> **Good luck and happy coding!**
>
>

Would you like me to help you brainstorm a project theme or draft the initial database schema based on these requirements?
