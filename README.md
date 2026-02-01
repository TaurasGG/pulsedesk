# PulseDesk

PulseDesk is an intelligent customer support ticketing system backend developed as part of an IBM Internship Exercise. It leverages AI (specifically **Llama-3-8B-Instruct** via Hugging Face) to automatically analyze user comments and convert actionable issues into support tickets.

## Features

- **Comment Submission**: Users can submit comments via a REST API or the web UI.
- **AI Analysis**: Comments are analyzed using a Large Language Model (LLM) to determine:
  - If a ticket should be created.
  - The category (BUG, FEATURE, BILLING, ACCOUNT, OTHER).
  - The priority (LOW, MEDIUM, HIGH).
  - A concise summary of the issue.
- **Ticket Management**: Automatically creates tickets linked to original comments.
- **In-Memory Database**: Uses H2 database for data persistence during the session.
- **Web UI**: A simple dashboard to test the flow, submit comments, and view generated tickets.

## Tech Stack

- **Java 17**
- **Spring Boot 3.4.2**
  - Spring Web (REST API)
  - Spring Data JPA (Database Interaction)
  - H2 Database (In-memory storage)
- **Hugging Face Inference API** (AI Integration)
- **Maven** (Build Tool)

## Prerequisites

- Java 17 or higher
- Maven (wrapper included)
- Hugging Face API Token (configured in `application.properties`)

## Getting Started

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd pulsedesk
    ```

2.  **Configuration**:
    Ensure `src/main/resources/application.properties` has a valid Hugging Face API token:
    ```properties
    huggingface.api.token=hf_YourTokenHere
    huggingface.api.model=meta-llama/Meta-Llama-3-8B-Instruct
    ```

3.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Access the application**:
    - **Web UI**: [http://localhost:8080/](http://localhost:8080/)
    - **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
      - JDBC URL: `jdbc:h2:mem:pulsedesk`
      - User: `SA`
      - Password: (empty)

## API Endpoints

### 1. Submit Comment
**POST** `/comments`

**Request Body**:
```json
{
  "author": "Alice",
  "text": "The login page throws a 500 error when I click submit."
}
```

**Response**:
```json
{
  "id": 1,
  "author": "Alice",
  "text": "The login page throws a 500 error when I click submit.",
  "ticketCreated": true
}
```

### 2. Get All Comments
**GET** `/comments`

**Response**:
```json
[
  {
    "id": 1,
    "author": "Alice",
    "text": "...",
    "ticketCreated": true
  }
]
```

### 3. Get All Tickets
**GET** `/tickets`

**Response**:
```json
[
  {
    "id": 1,
    "title": "Login 500 Error",
    "category": "BUG",
    "priority": "HIGH",
    "summary": "User reports 500 error on login page submission.",
    "originalComment": { ... }
  }
]
```

### 4. Get Ticket by ID
**GET** `/tickets/{id}`

## Architecture

- **Controller Layer**: Handles HTTP requests (`CommentController`, `TicketController`).
- **Service Layer**: Contains business logic.
  - `CommentService`: Orchestrates comment saving and AI delegation.
  - `HuggingFaceService`: Communicates with the external AI API.
- **Repository Layer**: Interfaces with the H2 database (`CommentRepository`, `TicketRepository`).
- **Model Layer**: JPA Entities (`Comment`, `Ticket`).

## AI Integration

The system uses the `meta-llama/Meta-Llama-3-8B-Instruct` model hosted on Hugging Face. It sends a prompt instructing the AI to classify the comment and return a structured JSON response conforming to the internal data model.
