# Library Management System

A console-based library management system built with **Java 17** and **raw JDBC** (no ORM) on **MySQL**, focused on layered architecture, real business rules, and automated testing with **JUnit 5** and **Mockito**.

## Overview

The system manages three core entities — **Users**, **Books**, and **Loans** — and enforces real business rules such as preventing a book from being loaned twice, blocking a duplicate email at signup, and calculating whether a loan is overdue.

This project is a direct successor to an earlier version built with Python and raw SQL, adapted here to explore JDBC and Java's object-oriented approach to persistence.

## Tech Stack

- **Java 17**
- **JDBC** (raw, no ORM/framework)
- **MySQL 8**
- **Maven**
- **JUnit 5** — unit tests
- **Mockito** — mocking for service-layer tests

## Features

- Register users and books, with validation (blank fields, email format, duplicate email, plausible publication year)
- Create a loan, blocking the operation if the book is already on loan
- Return a book, blocking re-returning an already-returned loan or a non-existent loan id
- List all users, books, and active loans
- Compute whether an active loan is overdue (`estaAtrasado()`), based on the due date — this status is **derived at runtime, never stored** in the database
- Interactive console menu (`Scanner`-based) for all operations above

## Architecture

The project follows a layered structure:

```
Main (console menu)
   ↓
Service layer (business rules, validation, custom exceptions)
   ↓
DAO layer (JDBC, SQL, ResultSet-to-object mapping)
   ↓
MySQL database
```

- **`model/`** — plain Java objects representing `Usuario`, `Livro`, and `Emprestimo`.
- **`dao/`** — JDBC access, using `PreparedStatement` and `try-with-resources` throughout. `EmprestimoDAO` uses a `JOIN` query (instead of N+1 separate queries) to fetch a loan together with its related user and book in a single round-trip.
- **`service/`** — business rules that don't belong in the DAO (e.g. "a book can't be loaned twice") or in the model (e.g. "this email is already registered"). Each service depends on its DAO through constructor injection, which is also what makes the service layer mockable in tests.
- **`exception/`** — custom unchecked exceptions (`LivroIndisponivelException`, `EmprestimoNaoEncontradoException`, `EmailDuplicadoException`, etc.) so callers can distinguish *why* an operation was rejected.
- **`enums/`** — `StatusEmprestimo` (`ATIVO`, `DEVOLVIDO`), persisted as `VARCHAR` rather than by ordinal position, so reordering the enum later can't silently corrupt existing data.
- **`util/`** — `ConexaoBD`, reading DB credentials from environment variables (`DB_USUARIO`, `DB_SENHA`) rather than hardcoding them.

## Project Structure

```
src/main/java/com/seuprojeto/library/
├── model/        Usuario, Livro, Emprestimo
├── dao/          UsuarioDAO, LivroDAO, EmprestimoDAO
├── service/      UsuarioService, LivroService, EmprestimoService
├── exception/    custom unchecked exceptions
├── enums/        StatusEmprestimo
├── util/         ConexaoBD
└── Main.java     console menu / entry point

src/test/java/com/seuprojeto/library/
├── model/        EmprestimoTest
└── service/      EmprestimoServiceTest
```

## Setup

1. Create the database and tables:
   ```bash
   mysql -u root -p < schema.sql
   ```
2. Set the required environment variables (Windows example):
   ```powershell
   setx DB_USUARIO "root"
   setx DB_SENHA "your_real_mysql_password"
   ```
   Restart your IDE/terminal afterward — environment variables are only read when the process starts.
3. Build and run:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="com.seuprojeto.library.Main"
   ```

## Running the tests

```bash
mvn test
```

Tests are split into two kinds:
- **`EmprestimoTest`** — pure unit tests for `Emprestimo.estaAtrasado()`, no database or mocking involved.
- **`EmprestimoServiceTest`** — service-layer tests using Mockito to mock `EmprestimoDAO`, verifying both the successful path and the exception path (`LivroIndisponivelException`) without touching the database.

## What I Practiced

- Object-oriented programming with Java
- Layered architecture (model / DAO / service)
- JDBC and SQL
- `PreparedStatement` and `ResultSet` mapping
- SQL `JOIN`s
- Constructor-based dependency injection
- Custom exceptions
- Enum persistence
- JUnit 5
- Mockito
- Environment-based configuration

## Known limitations

- **Race condition in `realizarEmprestimo`**: the availability check (`livroEstaEmprestado`) and the insert are two separate, non-atomic operations. Under concurrent access, two threads could both pass the check before either inserts, resulting in two active loans for the same book. This is a known, accepted limitation for now — a console, single-user application — and is planned to be addressed later with proper transaction/locking support (e.g. `SELECT ... FOR UPDATE`, or Spring's `@Transactional` once the project moves to Spring Boot).
- No frontend — this is intentionally a console application, focused on JDBC and layered architecture rather than UI.

## Author

[Arthur-Isidoro](https://github.com/Arthur-Isidoro)
