# PulseDesk – Comment to Ticket Triage

Spring Boot backend application that converts user comments into support tickets using Hugging Face AI.

## Tech Stack
- Java 25
- Spring Boot
- H2 Database
- Hugging Face Inference API

## Run Instructions
1. Add Hugging Face API key to application.properties
2. Run `mvn spring-boot:run`

## Endpoints
- POST /comments
- GET /comments
- GET /tickets
- GET /tickets/{id}
