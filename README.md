Customer Rewards Service
1. Overview
   Customer Rewards Service is a Spring Boot REST API that calculates
   customer reward points for transactions within a requested date range.
   The application:
   Retrieves customer and transaction data from an in-memory SQLite database
   using JPA.
   Loads schema and test data using `schema.sql` and `data.sql`.
   Supports an optional customer reward date range.
   Defaults to the previous three months when no dates are supplied.
   Calculates reward points for each transaction.
   Groups reward points by month.
   Returns transaction-level rewards, monthly summaries, and an overall
   summary.
   Exposes OpenAPI/Swagger documentation.
   Uses centralized exception handling with consistent error responses.
   Includes unit tests for the controller, service, reward calculator,
   date-range resolver, LocalDate converter, and exception handling.
---
2. Technology Stack
   Technology        Version / Details
---
Java              17
Spring Boot       3.5.4
Spring Web        Spring MVC
Bean Validation   Jakarta Validation
Lombok            1.18.38
OpenAPI           springdoc-openapi 2.8.9
Swagger UI        Included through springdoc
JUnit             JUnit 5
Mockito           Mockito 5
Data source       In-memory SQLite database (`schema.sql` + `data.sql`)
Logging           SLF4J + Logback
Build tool        Maven
---
3. High-Level Architecture
   The application follows a layered architecture:
```text
                         Client
                           |
                           v
                 +-------------------+
                 | RewardController  |
                 | REST API          |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | RewardService     |
                 | Business Logic    |
                 +-------------------+
                    |       |       |
                    |       |       v
                    |       |  +----------------+
                    |       |  | RewardCalculator|
                    |       |  +----------------+
                    |       |
                    |       v
                    |  +-------------------+
                    |  | DateRangeResolver |
                    |  +-------------------+
                    |
                    v
             +------------------------+
             | JPA Repositories       |
             | CustomerRepository     |
             | TransactionRepository  |
             +------------------------+
                    |
                    v
             +------------------------+
             | In-memory SQLite DB    |
             | schema.sql + data.sql  |
             +------------------------+
```
Layer responsibilities
Controller
`RewardController` is responsible for:
Exposing the REST endpoint.
Reading the customer ID and optional date-range parameters.
Returning `RewardResponse`.
Providing OpenAPI annotations.
Delegating business processing to `RewardService`.
Service
`RewardService` contains the core orchestration and business logic:
Resolves the effective date range.
Retrieves the customer using JPA.
Retrieves transactions using JPA.
Detects the no-transaction scenario.
Calculates transaction rewards.
Groups transactions by month.
Builds monthly rewards.
Builds the overall reward summary.
Date Range Resolver
`DateRangeResolver` owns the date-range rules:
No dates → previous three months through today.
Only `fromDate` → specified start date through today.
Only `toDate` → three months before the specified end date through the specified end date.
Both dates → specified date range.
Invalid range → `InvalidDateRangeException`.
Reward Calculator
`RewardCalculator` contains the reward-point calculation rule:
Amount `<= 50` → `0` points.
Amount `> 50` and `<= 100` → `amount - 50` points.
Amount `> 100` → `50 + ((amount - 100) * 2)` points.
The reward is calculated once per transaction and the calculated value
is reused by the service.
Repository
The application uses Spring Data JPA repositories:
`CustomerRepository` accesses customer records.
`TransactionRepository` accesses transaction records.
Transactions are filtered by customer ID and inclusive date range.
Database Initialization
The POC uses an in-memory SQLite database.
`schema.sql` creates the database tables.
`data.sql` loads representative test data.
JPA/Hibernate is used for application database operations.
The database is recreated when the application restarts.
Exception Handler
`GlobalExceptionHandler` provides centralized handling for:
`CustomerNotFoundException`
`InvalidDateRangeException`
`ConstraintViolationException`
`MethodArgumentTypeMismatchException`
`NoResourceFoundException`
Unexpected exceptions
4. API Details
   Calculate Customer Rewards
   HTTP Method: `GET`
   Endpoint:
```text
/api/v1/customers/{customerId}/rewards
```
Request Parameters
Parameter	Type	Required	Description
`customerId`	String	Yes	Customer identifier in the URL
`fromDate`	LocalDate	No	Start date in ISO format `yyyy-MM-dd`
`toDate`	LocalDate	No	End date in ISO format `yyyy-MM-dd`
Date Range Examples
Both dates provided
```http
GET /api/v1/customers/CUST001/rewards?fromDate=2026-06-01&toDate=2026-08-31
```
The specified date range is used.
No dates provided
```http
GET /api/v1/customers/CUST001/rewards
```
The service calculates rewards for the previous three months through today.
Only `fromDate` provided
```http
GET /api/v1/customers/CUST001/rewards?fromDate=2026-07-01
```
The service calculates rewards from the specified start date through today.
Only `toDate` provided
```http
GET /api/v1/customers/CUST001/rewards?toDate=2026-08-31
```
The service calculates rewards from three months before the specified end date through the specified end date.
Successful Response
HTTP status:
```text
200 OK
```
Response structure:
```json
{
  "customer": {
    "customerId": "CUST001",
    "customerName": "Ravi Kumar"
  },
  "fromDate": "2026-06-01",
  "toDate": "2026-08-31",
  "monthlyRewards": [
    {
      "month": "2026-06",
      "transactions": [
        {
          "transactionId": "TXN-001",
          "transactionDate": "2026-06-10",
          "amount": 120.00,
          "rewardPoints": 90
        }
      ],
      "totalSpent": 120.00,
      "totalRewardPoints": 90
    }
  ],
  "summary": {
    "totalTransactions": 1,
    "totalSpent": 120.00,
    "totalRewardPoints": 90
  }
}
```
5. Reward Calculation Rules
   The reward calculation is implemented in `RewardCalculator`.
   Rule 1: Amount <= 50
``` text
Reward Points = 0
```
Examples:
``` text
$0   → 0 points
$50  → 0 points
```
Rule 2: Amount > 50 and <= 100
``` text
Reward Points = Amount - 50
```
Examples:
``` text
$51  → 1 point
$75  → 25 points
$100 → 50 points
```
Rule 3: Amount > 100
``` text
Reward Points = 50 + ((Amount - 100) * 2)
```
Examples:
``` text
$101 → 52 points
$120 → 90 points
$200 → 250 points
```
---
6. Date Range Handling
   The API accepts ISO date values:
```text
yyyy-MM-dd
```
The date parameters are optional.
No dates provided
```text
fromDate == null
and
toDate == null
```
The effective range is the previous three months through today.
Only start date provided
```text
fromDate != null
toDate == null
```
The effective range is:
```text
fromDate → today
```
Only end date provided
```text
fromDate == null
toDate != null
```
The effective range is:
```text
three months before toDate → toDate
```
Both dates provided
```text
fromDate != null
toDate != null
```
The supplied date range is used.
Invalid date range
If:
```text
fromDate > toDate
```
the service throws `InvalidDateRangeException`.
Result:
```text
400 Bad Request
```
The same date is valid:
```text
fromDate == toDate
```
7. Error Handling
   All application errors are handled centrally through
   `GlobalExceptionHandler`.
   Customer not found
``` text
HTTP 404
```
Error code:
``` text
CUSTOMER_NOT_FOUND
```
Invalid date range
``` text
HTTP 400
```
Error code:
``` text
INVALID_DATE_RANGE
```
Validation error
``` text
HTTP 400
```
Error code:
``` text
VALIDATION_ERROR
```
Invalid request parameter
``` text
HTTP 400
```
Error code:
``` text
INVALID_PARAMETER
```
Resource not found
``` text
HTTP 404
```
Error code:
``` text
RESOURCE_NOT_FOUND
```
Unexpected error
``` text
HTTP 500
```
Error code:
``` text
INTERNAL_SERVER_ERROR
```
Error Response Structure
``` json
{
  "timestamp": "2026-08-22T17:30:00",
  "status": 400,
  "error": "INVALID_DATE_RANGE",
  "message": "fromDate must be before or equal to toDate"
}
```
---
9. Logging
   Logging is implemented across the application layers.
   Controller
   Logs:
   Incoming customer reward request.
   Customer ID.
   Date range.
   Successful completion.
   Service
   Logs:
   Start of reward calculation.
   Number of transactions retrieved.
   No-transaction condition.
   Completion with transaction count and total reward points.
   Repository
   Logs:
   Transaction search criteria.
   Number of matching transactions.
   Database initialization status
   Exception Handler
   Logs:
   Expected business exceptions at `WARN`.
   Unexpected exceptions at `ERROR`.
   No request tracing/filter mechanism is used in the POC.
---
10. OpenAPI / Swagger
    The controller contains OpenAPI annotations for API documentation.
    Swagger/OpenAPI is provided using:
``` text
springdoc-openapi-starter-webmvc-ui
```
OpenAPI JSON
``` text
http://localhost:8080/api-docs
```
Swagger UI
``` text
http://localhost:8080/swagger-ui.html
```
The controller documents:
API operation summary.
API description.
Successful response.
Bad request response.
Not found response.
Internal server error response.
`RewardResponse` schema.
---
11. Database and Persistence
    The application uses an in-memory SQLite database with Spring Data JPA for persistence.

Database initialization is handled using Spring Boot SQL initialization scripts:
- `schema.sql` contains the database schema and table-creation (DDL) statements.
- `data.sql` contains the initial/sample data (DML `INSERT` statements).
- `spring.sql.init.mode` is configured to initialize the database during application startup.
- Hibernate schema generation is disabled because the schema is managed explicitly through `schema.sql`.
- The application does not use JSON files for customer or transaction data.

Repository Layer
The repository layer uses plain Spring Data JPA interfaces to interact with the SQLite database.
- `CustomerRepository` provides persistence operations for `Customer` entities.
- `TransactionRepository` provides persistence operations for `Transaction` entities and supports retrieving transactions for a customer within a specified date range.
- The repositories do not contain JSON data-loading or logging logic.

Error Response
The application uses a consistent error response structure for handled exceptions.

For example, when a requested resource cannot be found:

```json
{
  "timestamp": "2026-08-22T17:30:00",
  "status": 404,
  "error": "NoResourceFoundException",
  "message": "Resource not found"
}
```

The `ErrorResponse` does not contain a `correlationId` field.

Error Handling
The application distinguishes between different failure scenarios:

| Scenario | HTTP Status |
|---|---:|
| Requested endpoint/resource does not exist | `404 NOT_FOUND` |
| Customer does not exist | `404 NOT_FOUND` |
| Invalid request parameters | `400 BAD_REQUEST` |
| Existing customer with no transactions | `200 OK` with an empty rewards result |
| Unexpected server error | `500 INTERNAL_SERVER_ERROR` |

12. Testing Strategy
    The application contains unit and integration-style tests for the major layers.
    RewardCalculatorTest
    Tests:
    Null amount.
    Amount below 50.
    Amount equal to 50.
    Amount between 50 and 100.
    Amount equal to 100.
    Amount greater than 100.
    Large amounts.
    Decimal amounts.
    DateRangeResolverTest
    Tests:
    No dates supplied.
    Only `fromDate` supplied.
    Only `toDate` supplied.
    Both dates supplied.
    Invalid date range.
    RewardServiceTest
    Tests:
    Successful reward calculation.
    Customer not found.
    No transactions.
    Resolved date range passed to the repository.
    Interaction with `RewardCalculator`.
    Interaction with JPA repositories.
    The service is tested using Mockito so repositories and the reward calculator
    can be isolated.
    RewardControllerTest
    Tests:
    Successful HTTP response.
    Optional date parameters.
    Invalid date parameters.
    Customer-not-found response.
    Invalid endpoint/path scenarios.
    JSON response structure.
    The controller uses `@WebMvcTest` and `MockMvc`.
    For Spring Boot 3.5.x, Mockito dependencies are injected using:
```java
@MockitoBean
```
instead of the deprecated `@MockBean`.
LocalDateConverterTest
Tests:
LocalDate to database string conversion.
Database string to LocalDate conversion.
Null handling.
Round-trip conversion.
GlobalExceptionHandlerTest
Tests:
Customer-not-found handling.
Invalid-date-range handling.
Constraint validation errors.
Type mismatch errors.
Resource-not-found handling.
Unexpected exceptions.
Error response structure.
Testing Documentation and Screenshots
Manual API testing is documented with screenshots under:
```text
docs/screenshots/
```
Recommended scenarios include:
Successful reward calculation.
Default three-month date range.
Invalid date range.
Customer not found.
Successful JUnit/Maven test execution.
Swagger/OpenAPI documentation.
13. Project Structure
```text
src
├── main
│   ├── java
│   │   └── com.customer.rewards
│   │       ├── controller
│   │       │   └── RewardController.java
│   │       │
│   │       ├── converter
│   │       │   └── LocalDateConverter.java
│   │       │
│   │       ├── dto
│   │       │   ├── request
│   │       │   │   └── DateRange.java
│   │       │   └── response
│   │       │       ├── CustomerInfo.java
│   │       │       ├── ErrorResponse.java
│   │       │       ├── MonthlyReward.java
│   │       │       ├── RewardResponse.java
│   │       │       ├── RewardSummary.java
│   │       │       └── TransactionReward.java
│   │       │
│   │       ├── exception
│   │       │   ├── CustomerNotFoundException.java
│   │       │   ├── InvalidDateRangeException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       ├── model
│   │       │   ├── Customer.java
│   │       │   └── Transaction.java
│   │       │
│   │       ├── repository
│   │       │   ├── CustomerRepository.java
│   │       │   └── TransactionRepository.java
│   │       │
│   │       └── service
│   │           ├── DateRangeResolver.java
│   │           ├── RewardCalculator.java
│   │           └── RewardService.java
│   │
│   └── resources
│       ├── schema.sql
│       ├── data.sql
│       └── application.yml
│
├── test
│   └── java
│       └── com.customer.rewards
│           ├── controller
│           │   └── RewardControllerTest.java
│           ├── converter
│           │   └── LocalDateConverterTest.java
│           ├── exception
│           │   └── GlobalExceptionHandlerTest.java
│           └── service
│               ├── DateRangeResolverTest.java
│               ├── RewardCalculatorTest.java
│               └── RewardServiceTest.java
│
└── docs
    └── screenshots
        ├── reward-success.png
        ├── reward-default-date-range.png
        ├── invalid-date-range.png
        ├── customer-not-found.png
        ├── junit-test-results.png
        └── swagger-api.png
```
14. Running the Application
    Build
``` bash
mvn clean install
```
Run tests
``` bash
mvn test
```
Run the application
``` bash
mvn spring-boot:run
```
The application starts on:
``` text
http://localhost:8080
```
---
15. API Examples
    Both dates provided
```bash
curl --location \
  'http://localhost:8080/api/v1/customers/CUST001/rewards?fromDate=2026-06-01&toDate=2026-08-31'
```
No dates provided
```bash
curl --location \
  'http://localhost:8080/api/v1/customers/CUST001/rewards'
```
Only start date
```bash
curl --location \
  'http://localhost:8080/api/v1/customers/CUST001/rewards?fromDate=2026-07-01'
```
Only end date
```bash
curl --location \
  'http://localhost:8080/api/v1/customers/CUST001/rewards?toDate=2026-08-31'
```
16. Design Decisions
    Layered architecture
    The application uses clear separation of concerns between:
```text
Controller
   ↓
Service
   ↓
Repository
   ↓
SQLite
```
This makes the application easier to test and maintain.
Reward calculation isolation
Reward calculation is isolated in `RewardCalculator` rather than embedding the
calculation formula directly inside the service.
This makes the business rule independently testable and replaceable.
Date range isolation
Date-range resolution is isolated in `DateRangeResolver`.
This keeps optional-date/default-range behavior out of the controller and keeps
the service focused on reward orchestration.
JPA repository abstraction
The service depends on Spring Data JPA repositories rather than directly
depending on SQLite implementation details.
This allows the underlying database to be replaced later with another relational
database while keeping the service and controller contracts stable.
SQLite initialization
`schema.sql` creates the schema and `data.sql` loads representative POC data.
This provides deterministic test data while keeping the application lightweight
for the POC.
Centralized exception handling
All REST exceptions are handled through `GlobalExceptionHandler`, providing
consistent HTTP status codes and error response structures.
Logging
Logging is implemented across the controller, service, repository/data-access,
and exception-handling layers.
The POC intentionally does not use correlation-ID filtering because it was
removed based on the review feedback.
17. Future Enhancements
    The current implementation is a POC. Potential production enhancements
    include:
    Replace the in-memory SQLite database with a production relational database.
    Add database indexes for customer ID and transaction date.
    Add pagination if transaction volume becomes large.
    Add authentication and authorization.
    Add API rate limiting.
    Add distributed tracing with trace/span IDs.
    Add metrics and health monitoring.
    Add Docker support.
    Add CI/CD pipeline.
    Add integration and contract tests.
    Add structured JSON logging for centralized log platforms.
    Add API versioning strategy as additional API versions are
    introduced.
    Externalize configuration by environment.
    Add resilience and observability when external dependencies are
    introduced.
---
18. Summary
    Customer Rewards Service demonstrates a clean Spring Boot REST
    implementation with:
    Java 17
    Spring Boot 3.5.4
    Layered architecture
    Reward calculation business logic
    In-memory SQLite database
    Spring Data JPA repositories
    `schema.sql` and `data.sql` initialization
    OpenAPI documentation
    Centralized exception handling
    Structured logging without correlation-ID filtering
    JUnit 5 and Mockito tests
    Optional date-range handling
    Input/date validation
    Separation of API DTOs and business logic
    The design keeps the current POC simple while providing clear extension
    points for a production implementation.