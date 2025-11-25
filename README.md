# Smart Parking System

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring-boot)
![Gradle](https://img.shields.io/badge/Gradle-Build-blue?style=flat-square&logo=gradle)
![Database](https://img.shields.io/badge/Database-H2-lightgrey?style=flat-square)

**Smart Parking System** is a robust RESTful application built with Spring Boot designed to manage parking facilities
efficiently. It handles parking structure management, vehicle check-in/out processes, dynamic billing strategies, and
real-time availability tracking.

---

### Key Components

| Component                     | Description                                                                        |
|:------------------------------|:-----------------------------------------------------------------------------------|
| **ParkingLot / Level / Slot** | Represents the physical hierarchical structure of the facility.                    |
| **ParkingSession**            | Central entity for tracking entry/exit times, billing status, and vehicle history. |
| **PricingStrategy**           | Pluggable logic for fee calculation (utilizing the **Strategy Pattern**).          |
| **DTO Layer**                 | Data Transfer Objects ensuring a clean and secure API contract.                    |
| **Global Exception Handler**  | Centralized logic (`@ControllerAdvice`) for unified error responses.               |

---

## Design Rationale

### 1. Strategy Pattern for Pricing

* **Why:** The requirements demanded a flexible billing system capable of supporting various fee models.
* **Benefit:** Allows switching between **Hourly**, **Flat Rate**, or future **Dynamic** pricing without modifying core
  Service logic.

### 2. Hierarchical Structure (Lot → Level → Slot)

* **Why:** To accurately model real-world parking facilities.
* **Benefit:**
    * **Scalability:** Supports multiple independent parking lots.
    * **Granularity:** Enables flexible management of parking slots and levels. Supports precise tracking of slot
      availability, individual configuration of levels, and adaptable pricing per slot type, while maintaining overall
      scalability of the parking facility.

### 3. Session-Based Tracking

* **Why:** Separation of the "Slot" state from the "Visit" history.
* **Benefit:**
    * Preserves historical data even after a vehicle departs.
    * Enables precise duration calculation using `java.time.Duration`.
    * Provides a foundation for future analytics and revenue reporting.

---

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3 (Web, Data JPA, Validation)
* **Build Tool:** Gradle
* **Database:** H2 (In-Memory)
* **Testing:** JUnit 5, Mockito

---

## Getting Started

### Prerequisites

* Java 21 or higher
* Gradle installed (or use the included wrapper).

#### Build the Application

```
./gradlew clean build
```

#### Run the Application

```
./gradlew bootRun
```

---

## Enpoints

### Parking Management Controller(require authorization)

| Method | Endpoint                                    | Description                       | Constraints                            |
|--------|---------------------------------------------|-----------------------------------|----------------------------------------|
| POST   | `/api/admin/parking-lots`                   | Create a new parking lot          | Lot name required                      |
| DELETE | `/api/admin/parking-lots/{lotId}`           | Delete parking lot                | Cannot delete if active sessions exist |
| POST   | `/api/admin/parking-lots/{lotId}/levels`    | Create parking level inside a lot | Level name required                    |
| DELETE | `/api/admin/parking-levels/{levelId}`       | Delete parking level              | Cannot delete if active sessions exist |
| POST   | `/api/admin/parking-levels/{levelId}/slots` | Create parking slot               | Slot name must be unique per level     |
| PATCH  | `/api/admin/slots/{slotId}`                 | Update slot attributes            | Partial update allowed                 |
| DELETE | `/api/admin/slots/{slotId}`                 | Delete slot                       | Slot must not be occupied              |

### Parking Info

| Method | Endpoint                 | Description                                 | Constraints                         |
|--------|--------------------------|---------------------------------------------|-------------------------------------|
| GET    | `/api/parking-lots`      | Get all parking lots with availability info | —                                   |
| GET    | `/api/check/{vehicleId}` | Get active parking session for the vehicle  | Vehicle must have an active session |

### Vehicle Controller

| Method | Endpoint                             | Description                                | Constraints                                                                                                                           |
|--------|--------------------------------------|--------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/parking/check-in`              | Register vehicle entry                     | Vehicle must not have an active session.You cannot park in spaces under repair or in handicapped spaces without the necessary permit. |
| POST   | `/api/parking/check-out/{vehicleId}` | Complete parking session and calculate fee | Vehicle must have an active session                                                                                                   |

### Example Responses

#### Check-In Response(Success)

```json
{
  "vehicleId": "003e88dd-ff87-437d-b3c5-b5d17ccb0b16",
  "licensePlate": "KA7778MI",
  "slot": "1A-Handicapped",
  "Level": "first floor",
  "parkingLotName": "temporary parking lot",
  "parkingLotLocation": "Historical Boulevard, Sevastopol",
  "entryTime": "2025-11-25T13:00:20.614391100Z"
}
```

#### Check-In Response(Failure)

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Vehicle KA7778MI is already parked",
  "path": "/api/parking/check-in",
  "timestamp": "2025-11-25T13:01:16.892600800Z"
}
```

---

### Postman

```
Import postman collection under postman-collection folder
```

---

## 👨‍💻 Author

> Stanislav Shelest https://github.com/Stik27
