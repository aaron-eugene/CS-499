# CS 499 Travlr Enhancement

This repository contains the enhanced Travlr Getaways application for CS 499. The original Travlr project was created in CS 465 as a MEAN-stack application. For the CS 499 capstone, the project is being migrated toward a React, Spring Boot, and PostgreSQL architecture.

## Current Enhancement Status

The current version includes:

* React frontend in `app-client`
* Spring Boot REST API in `app-api`
* PostgreSQL persistence for trip data
* Flyway database migrations
* Spring Data JPA repository implementation
* Database-backed trip search, filtering, sorting, pagination, and summary aggregation
* Read-only public trip API endpoints

The public API remains read-only. Future admin create, update, and delete functionality should be protected with authentication and authorization before being exposed.

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

## Backend Configuration

The backend reads database settings from environment variables when available, with local defaults in `application.properties`.

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
.\mvnw test
```

The tests include service-level tests and database-backed repository tests for the PostgreSQL trip repository behavior.

## API Endpoints

### Get all trips

```http
GET /api/trips
```

### Search trips

```http
GET /api/trips?search=reef
```

### Filter trips by price

```http
GET /api/trips?minPrice=1000&maxPrice=2000
```

### Filter trips by duration

```http
GET /api/trips?minDays=5&maxDays=7
```

### Sort trips

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

### Paginate trips

```http
GET /api/trips?page=0&size=2
```

### Get one trip by code

```http
GET /api/trips/GALREE20270214
```

### Get trip summary metadata

```http
GET /api/trips/summary
```

The summary endpoint returns catalog metadata such as total trip count, distinct resorts, minimum and maximum price, and minimum and maximum duration.

## Notes for Future Enhancement

Planned future work includes:

* Protected admin create, update, and delete endpoints
* Authentication and authorization
* Expanded frontend admin functionality
* Additional tests around write behavior and database constraints
* Further ePortfolio polish for final publication
