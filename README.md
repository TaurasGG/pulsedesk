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

- **Java 21**
- **Spring Boot 4.0.2**
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

## Web UI

- Side-by-side layout shows:
  - **Recent Tickets** (left) and **Incoming Comments** (right)
- Comments show their **ID** (e.g., `#5`)
- Each ticket includes an **Expand** button to reveal:
  - Original author
  - Original comment text
  - Original comment ID

## Deployment

### Localhost

- Prerequisites:
  - Java (recommended 21 or 23)
  - Maven wrapper (included)
- Configure environment variables (recommended for security):
  - Windows PowerShell:
    ```powershell
    $env:HUGGINGFACE_API_TOKEN="hf_your_token"
    $env:HUGGINGFACE_API_MODEL="meta-llama/Meta-Llama-3-8B-Instruct"
    ```
  - macOS/Linux:
    ```bash
    export HUGGINGFACE_API_TOKEN="hf_your_token"
    export HUGGINGFACE_API_MODEL="meta-llama/Meta-Llama-3-8B-Instruct"
    ```
- Run:
  ```bash
  ./mvnw spring-boot:run
  ```
- Open:
  - UI: http://localhost:8080/
  - H2 Console: http://localhost:8080/h2-console

Note: The app reads configuration from environment variables, defined in [application.properties](file:///c:/Users/taura/OneDrive/Documents/Code/Miscellaneous/IBM/pulsedesk/src/main/resources/application.properties). Keys:
- `server.port=${PORT:8080}`
- `huggingface.api.token=${HUGGINGFACE_API_TOKEN}`
- `huggingface.api.model=${HUGGINGFACE_API_MODEL:meta-llama/Meta-Llama-3-8B-Instruct}`

### Google Cloud (Cloud Run)

- Recommended: Deploy via “source deploy” (no Dockerfile required). Cloud Build uses Buildpacks to build the container.
- Setup:
  ```bash
  gcloud auth login
  gcloud config set project YOUR_PROJECT_ID
  gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com secretmanager.googleapis.com
  ```
- Create the secret with your Hugging Face token:
  ```bash
  echo -n "hf_your_token" > hf_token.txt
  gcloud secrets create HF_TOKEN --data-file=hf_token.txt
  ```
- Grant Secret Manager access to Cloud Run runtime service account:
  ```bash
  # Secret-level permission (recommended)
  gcloud secrets add-iam-policy-binding HF_TOKEN \
    --member="serviceAccount:YOUR_SA@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
  # Or project-level permission:
  gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:YOUR_SA@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
  ```
  Tip: To see the service account used by Cloud Run:
  ```bash
  gcloud run services describe pulsedesk --region us-central1 --format='value(spec.template.spec.serviceAccountName)'
  ```
- Deploy (source deploy):
  ```bash
  gcloud run deploy pulsedesk \
    --source . \
    --region us-central1 \
    --allow-unauthenticated \
    --set-build-env-vars BP_JVM_VERSION=21 \
    --set-env-vars HUGGINGFACE_API_MODEL=meta-llama/Meta-Llama-3-8B-Instruct \
    --set-secrets HUGGINGFACE_API_TOKEN=HF_TOKEN:latest \
    --memory 512Mi --cpu 1 --min-instances 0 --max-instances 3
  ```
- After deploy:
  - Visit the Cloud Run URL shown in the output
  - Test:
    ```bash
    curl -s https://YOUR_SERVICE_URL/comments
    curl -s -X POST https://YOUR_SERVICE_URL/comments \
      -H "Content-Type: application/json" \
      -d '{"author":"Alice","text":"Main button to home is not working"}'
    curl -s https://YOUR_SERVICE_URL/tickets
    ```

#### Notes
- Keep `HUGGINGFACE_API_TOKEN` out of source; use Secret Manager.
- `BP_JVM_VERSION=21` ensures Buildpacks build successfully with Spring Boot 4.
- The app respects Cloud Run `PORT` via `server.port=${PORT:8080}`.

## AI Integration

The system uses the `meta-llama/Meta-Llama-3-8B-Instruct` model hosted on Hugging Face. It sends a prompt instructing the AI to classify the comment and return a structured JSON response conforming to the internal data model.
