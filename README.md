# Simple RAG QA System with Spring Boot

A **Retrieval-Augmented Generation (RAG) Question Answering System** built with **Spring Boot**, **Ollama**, and **Pinecone** that allows users to upload documents and ask questions based on their content.

The system retrieves relevant information from uploaded documents using **vector search** and generates context-aware answers using a **local LLM via Ollama**.

---

# Features

### Authentication

* User Registration
* Secure Login
* Password validation rules

### Document Management

* Upload documents (PDF, Word, Text)
* View uploaded documents
* Delete documents
* Sort documents:

  * By Name
  * By Created Date
  * By Last Modified

### AI Question Answering

* Ask questions about uploaded documents
* Context retrieval from vector database
* AI-generated answers using Ollama
* Chat-style interface

### Chat Interface

* Interactive question input
* AI thinking indicator
* Context-based responses

---

# System Architecture

```
User
 │
 ▼
Upload Document
 │
 ▼
Text Extraction
 │
 ▼
Text Chunking
 │
 ▼
Embeddings Generation
 │
 ▼
Pinecone Vector Database
 │
 ▼
User Question
 │
 ▼
Vector Similarity Search
 │
 ▼
Relevant Context
 │
 ▼
Ollama LLM
 │
 ▼
Generated Answer
```

---

# Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Maven

### AI / RAG

* Ollama (Local LLM)
* Pinecone Vector Database
* Embeddings
* Retrieval-Augmented Generation (RAG)

### Frontend

* HTML
* CSS
* JavaScript
* JSP

---

# Project Structure

```
simple-rag-springboot
│
├── src
│   └── main
│       ├── java/com/example/rag_qa
│       │   ├── config              # Application configuration
│       │   ├── controller          # Handles HTTP requests
│       │   ├── entity              # Database entities
│       │   ├── repository          # Data access layer
│       │   ├── service             # Business logic and RAG processing
│       │   └── RagQaApplication.java
│       │
│       └── resources
│           ├── static              # Static assets (CSS, JS)
│           ├── application.properties
│           └── tokenizer.model     # Tokenizer model used for text processing
│
│       └── webapp/WEB-INF/jsp
│           ├── chat.jsp            # Chat interface
│           ├── home.jsp            # Dashboard / upload page
│           ├── login.jsp           # Login page
│           └── register.jsp        # Registration page
│
├── .env.example                    # Example environment variables
├── pom.xml                         # Maven dependencies
├── mvnw / mvnw.cmd                 # Maven wrapper
└── README.md
```

---

# Prerequisites

Make sure you have installed:

* Java 17+
* Maven
* Ollama
* MySQL
* Pinecone account

---

# Setup Ollama

This project uses **Ollama** to run AI models locally for both **embeddings** and **answer generation**.

### 1️⃣ Install Ollama

Download and install Ollama from:

https://ollama.com

---

### 2️⃣ Pull Required Models

Run the following commands:

```bash
ollama pull gemma3:4b
ollama pull embeddinggemma:latest
```

**Model Usage**

| Model                 | Purpose              |
| --------------------- | -------------------- |
| gemma3:4b             | Generates answers    |
| embeddinggemma:latest | Generates embeddings |

---

### 3️⃣ Start Ollama Server

```bash
ollama serve
```

Ollama runs locally at:

```
http://localhost:11434
```

Make sure the server is running before starting the Spring Boot application.

---

# Setup Pinecone

1. Create an account at Pinecone.
2. Create a vector index.
3. Add your API key to the application configuration.

Example:

```
spring.ai.vectorstore.pinecone.api-key=${PINECONE_KEY}
spring.ai.vectorstore.pinecone.index-name=DB
```

---

# Application Configuration

Configure the following properties in `application.properties`.

Example:

```
spring.application.name=rag-qa
server.port=8001

# Pinecone
spring.ai.vectorstore.pinecone.api-key=${PINECONE_KEY}
spring.ai.vectorstore.pinecone.index-name=DB

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/rag_app_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Spring Security
spring.security.user.name=admin
spring.security.user.password=YOUR_PASSWORD
```

---

# Run the Application

### Clone Repository

```
git clone https://github.com/yourusername/simple-rag-springboot.git
cd simple-rag-springboot
```

### Build Project

```
mvn clean install
```

### Run Application

```
mvn spring-boot:run
```

Application will start at:

```
http://localhost:8001
```

---

# Example Question

```
What is a controller layer?
```

Example Response:

```
The controller layer in a Spring Boot application handles HTTP requests and responses. 
It exposes REST endpoints and communicates with the service layer.
```

---

# RAG Workflow

1. User uploads a document.
2. Document text is extracted and split into chunks.
3. Each chunk is converted into embeddings.
4. Embeddings are stored in Pinecone.
5. When a user asks a question:

   * The question is converted into embeddings
   * Pinecone retrieves relevant document chunks
   * The context is sent to Ollama
   * Ollama generates the final answer

---

# Screenshots

<p align="center">
  <img src="screenshots/login.png" width="45%" />
  <img src="screenshots/register.png" width="45%" />
</p>

<p align="center">
  <img src="screenshots/upload.png" width="45%" />
  <img src="screenshots/chat.png" width="45%" />
</p>

<p align="center">
  <img src="screenshots/documents.png" width="60%" />
</p>
---

# 🎯 Future Improvements

* Multi-document retrieval
* Streaming AI responses
* Document preview
* Docker deployment
* UI improvements

---
