# CS 499 Travlr Enhancement

This repository contains the enhanced Travlr Getaways application for CS 499. The original Travlr project was created in CS 465 as a MEAN-stack application. For the CS 499 capstone, the project was enhanced into a React, Spring Boot, and PostgreSQL full-stack application with improved architecture, database persistence, trip search behavior, administrative trip management, and protected admin endpoints.

## Current Enhancement Status

The current version includes:

* React frontend in `app-client`
* Spring Boot REST API in `app-api`
* PostgreSQL persistence for trip data
* Flyway database migrations
* Spring Data JPA repository implementation
* Database-backed trip search, filtering, sorting, pagination, and summary aggregation
* Public read-only trip browsing endpoints
* Protected admin create, update, and delete endpoints
* React admin interface for managing trips
* Spring Security HTTP Basic authentication for admin API requests
* Configurable CORS for local frontend/backend integration
* Backend tests for service behavior, defensive input handling, admin CRUD behavior, and admin endpoint security

## Project Structure

```text
app-api/      Spring Boot REST API
app-client/   React frontend
```

## Prerequisites

* Java 21
* Node.js and npm
* PostgreSQL 17
* Maven wrapper included with the backend project

## PostgreSQL Setup

The backend expects a local PostgreSQL database with the following default connection settings:

```text
Database: travlr
Username: travlr_app
Password: travlr_app
Port: 5432
```

Create the database and user from `psql` as the PostgreSQL superuser:

```sql
CREATE DATABASE travlr;

CREATE USER travlr_app WITH PASSWORD 'travlr_app';

GRANT ALL PRIVILEGES ON DATABASE travlr TO travlr_app;

\c travlr

GRANT ALL ON SCHEMA public TO travlr_app;
```

The Spring Boot API uses Flyway to create and seed the database. When the backend starts, Flyway applies the migration files in:

```text
app-api/src/main/resources/db/migration/
```

Current migrations:

```text
V1__create_trips_table.sql
V2__seed_trips.sql
```

## Development Database Reset

A manual development reset script is included for restoring the local trip catalog to the original seed data after testing admin create, update, and delete behavior.

From the backend folder:

```powershell
cd app-api
psql -U travlr_app -d travlr -f .\src\main\resources\db\dev\reset_trips.sql
```

When prompted, use the default local database password:

```text
travlr_app
```

This reset script is intended for local development only.

## Backend Configuration

The backend reads settings from environment variables when available, with local defaults in `application.properties`.

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/travlr}
spring.datasource.username=${DB_USERNAME:travlr_app}
spring.datasource.password=${DB_PASSWORD:travlr_app}
```

Flyway manages schema creation, and Hibernate validates the entity mapping against the database schema.

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

The backend API port defaults to `8080`:

```properties
server.port=${SERVER_PORT:8080}
```

The allowed frontend origin for browser API requests defaults to the local Vite development server:

```properties
app.cors.allowed-origin=${APP_CORS_ALLOWED_ORIGIN:http://localhost:5173}
```

## Admin Authentication

Administrative trip-management endpoints are protected with Spring Security HTTP Basic authentication. Public trip browsing endpoints are available without authentication, while create, update, and delete operations under `/api/admin/**` require an authenticated user with the `ADMIN` role.

For local development, the default admin credentials are:

```text
Username: admin
Password: changeme
```

These defaults are for local development and capstone demonstration only. The application supports overriding them with environment variables:

```text
ADMIN_USERNAME
ADMIN_PASSWORD
```

In a deployed environment, admin credentials should be overridden and protected with HTTPS/TLS.

## Running the Backend

From the project root:

```powershell
cd app-api
.\mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

## Running the Frontend

From the project root:

```powershell
cd app-client
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

## Running Tests

From the backend folder:

```powershell
cd app-api
.\mvnw clean test
```

The tests include service-level tests, defensive input tests, database-backed repository behavior, admin CRUD tests, and admin endpoint security tests.

To verify the frontend production build:

```powershell
cd app-client
npm run build
```

## API Endpoints

### Public Trip Endpoints

#### Get all trips

```http
GET /api/trips
```

#### Search trips

```http
GET /api/trips?search=reef
```

#### Filter trips by price

```http
GET /api/trips?minPrice=1000&maxPrice=2000
```

#### Filter trips by duration

```http
GET /api/trips?minDays=5&maxDays=7
```

#### Sort trips

```http
GET /api/trips?sort=price&direction=desc
```

Supported sort fields include:

```text
name
code
price
startDate
duration
```

#### Paginate trips

```http
GET /api/trips?page=0&size=2
```

#### Get one trip by code

```http
GET /api/trips/GALREE20270214
```

#### Get trip summary metadata

```http
GET /api/trips/summary
```

The summary endpoint returns catalog metadata such as total trip count, distinct resorts, minimum and maximum price, and minimum and maximum duration.

### Protected Admin Endpoints

The following endpoints require admin authentication.

#### Create a trip

```http
POST /api/admin/trips
```

#### Update a trip

```http
PUT /api/admin/trips/{code}
```

#### Delete a trip

```http
DELETE /api/admin/trips/{code}
```

## Frontend Admin Workflow

The React frontend includes an admin management view for creating, editing, and deleting trips. Admin credentials are kept in React component state and are sent only when a protected admin request is made. They are not stored in `localStorage` or `sessionStorage`.

The admin interface uses the protected backend endpoints and displays user-safe error messages for validation errors, authentication failures, authorization failures, missing trips, and duplicate trip codes.

## Security Notes

This capstone version separates public browsing behavior from protected administrative write behavior. Public users can browse and search trips without authentication. Administrative create, update, and delete requests require an authenticated admin user.

Security-related improvements include:

* Spring Security protection for `/api/admin/**`
* Role-based access requirement for admin endpoints
* Configurable development admin credentials
* CORS restricted to a configured frontend origin
* Server-side validation for create and update requests
* Defensive service-layer checks
* Filename validation for trip image names
* User-safe frontend error messages for admin API failures
* Backend tests covering unauthenticated and authenticated admin behavior

HTTP Basic authentication is used as a lightweight development and capstone demonstration mechanism. A production deployment would use stronger credential management, HTTPS/TLS, and a more complete authentication/session strategy.

## Capstone Enhancement Summary

This project demonstrates the three CS 499 enhancement categories:

* **Software Design and Engineering:** The original application was restructured into a React frontend and Spring Boot backend with clearer separation between presentation, controller, service, repository, configuration, and DTO responsibilities.
* **Algorithms and Data Structures:** The enhanced API supports trip search, filtering, sorting, pagination, lookup by stable trip code, and summary aggregation.
* **Databases:** The enhanced backend uses PostgreSQL, Flyway migrations, JPA entity mapping, Spring Data repository behavior, and database-backed trip persistence.
