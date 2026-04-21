# CarRentalSystem Backend

Hexagonal-style Java backend for a car rental system using the custom IoC/DI framework `thesis-java-framework`.

## Stack
- Java 17
- Maven
- `com.github.Batkann1102:thesis-java-framework:v1.0.4`
- Embedded Tomcat
- Jackson JSON
- PostgreSQL-ready adapters with in-memory fallback

## Implemented Layers
- `core`: domain models, ports, services, strategies, exceptions
- `adapters.inbound.web`: servlets, DTOs, mapper, CORS filter
- `adapters.outbound.persistence`: PostgreSQL-shaped adapters, entities, mapper
- `infrastructure`: application config, database access helper, embedded server
- `shared`: JSON helper and API constants

## Endpoints
- `GET /api/health`
- `GET /api/cars/available`
- `GET /api/rentals`
- `POST /api/rentals`

### Sample rent request
```json
{
  "carId": "CAR-003",
  "customerName": "Bat",
  "rentalType": "HOURLY",
  "duration": 3
}
```

## Run
```powershell
mvn test
mvn exec:java -Dexec.mainClass="mn.edu.num.carrental.App"
```

## PostgreSQL
Default config uses in-memory fallback. To use PostgreSQL, update `src/main/resources/application.properties` and run `src/main/resources/schema.sql` on your database.

