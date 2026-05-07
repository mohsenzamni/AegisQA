# AegisQA Help

This guide explains how to run AegisQA and use it quickly.

## 1) Prerequisites

- Java 17
- Maven 3.9+
- Node.js 20+
- npm
- Docker (optional, for containerized run)

## 2) Run Locally (recommended for development)

### Backend

From repository root:

```bash
mvn -f backend/pom.xml compile
mvn -f backend/pom.xml spring-boot:run -pl api-gateway
```

Backend API: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend UI: `http://localhost:5173`

## 3) Run with Docker

From repository root:

```bash
docker compose up -d postgres backend
```

Backend API will be available on `http://localhost:8080`.

### Docker with AI profile

```bash
OPENAI_API_KEY=your-key docker compose --profile with-ai up -d
```

## 4) Basic Usage Flow

1. Start backend (and frontend if needed).
2. Create or choose a scenario file (examples are in `/scenarios`).
3. Parse scenario through API.
4. Run execution through API.
5. Check execution status and reports.

## 5) Useful API Endpoints

- `POST /api/v1/scenarios/parse`
- `POST /api/v1/executions/run`
- `GET /api/v1/executions/{id}`
- `GET /api/v1/mcp/tools`
- `POST /api/v1/ai/summarize-failure`
- `POST /api/v1/ai/heal-selector`

Base URL: `http://localhost:8080`

## 6) Quick API Example

Parse a sample scenario:

```bash
curl -X POST "http://localhost:8080/api/v1/scenarios/parse" \
  -H "Content-Type: text/plain" \
  --data-binary "@/home/runner/work/AegisQA/AegisQA/scenarios/frictionless-visa-2.2.yaml"
```

> If your API expects a different payload shape (JSON wrapper), use Swagger UI to copy the exact request body.

## 7) Common Troubleshooting

- **Backend fails to start**: ensure Java 17 is active.
- **Frontend build/dev issues**: run `npm install` again in `/frontend`.
- **DB connection problems in Docker**: ensure `postgres` container is healthy.
- **AI endpoints fail**: verify `LITELLM_BASE_URL`, `LITELLM_API_KEY`, and provider keys.

