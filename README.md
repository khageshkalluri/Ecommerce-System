# 🛒 Ecommerce System

A scalable, microservices-based ecommerce backend built with **Spring Boot**, **Java**, **Docker**, and **Kafka**. This system is designed to handle core ecommerce functionalities such as user management, product catalog, order processing, and real-time communication between services.

---

## 🚀 Features

- **Microservices Architecture**: Modular services for users, products, orders, and gateway.
- **Spring Boot**: Robust backend framework for building RESTful APIs.
- **Kafka Integration**: Event-driven communication between services.
- **Dockerized Deployment**: Containerized services for easy orchestration.
- **Reactive API Gateway**: Built with Spring Cloud Gateway for routing and load balancing.
- **CI/CD Ready**: Structured for integration with Jenkins or GitHub Actions.

---

## 🧱 Tech Stack

| Layer        | Technology                     |
|--------------|--------------------------------|
| Language     | Java                           |
| Framework    | Spring Boot                    |
| Messaging    | Apache Kafka,gRPC              |
| Container    | Docker                         |
| API Gateway  | Spring Cloud Gateway (Reactive)|
| Build Tool   | Maven                          |
| Versioning   | Git                            |

---

## 📁 Project Structure

```
Ecommerce-System/ 
├── Analytics-service/ 
├── api-gateway/ 
├── Notification-service/ 
├── users-service/
```

## 🛠️ Setup Instructions

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

### Run Locally

```bash
# Clone the repository
git clone https://github.com/khageshkalluri/Ecommerce-System.git
cd Ecommerce-System

# Build all services
mvn clean install

# Start services using Docker
docker-compose up --build
```

Future Enhancements

Add authentication with JWT

Integrate Redis for caching

Implement payment gateway service

Add monitoring with ELK stack

Build frontend with React or Angular

Add GenAI-powered chatbot for customer support
