# 🛡️ AegisQA — Enterprise AI-Driven ACS UI Test Platform

> **Automate. Adapt. Assert.** — An enterprise-grade, AI-augmented test automation platform purpose-built for ACS (Access Control Server) 3DS UI testing.

## 📋 Overview

AegisQA is an enterprise test automation platform for **ACS (Access Control Server)** UI testing across all major payment schemes (Visa, Mastercard, Amex, JCB). It supports the full EMV 3DS 2.x specification and provides:

- **Multi-format scenario ingestion** — YAML, Markdown, Excel, Plain Text
- **Rule-based + AI scenario interpretation** — Maps natural language to canonical actions
- **Playwright-powered browser automation** — Isolated, repeatable browser sessions
- **MCP-style tool abstractions** — Validated, structured browser tool invocations
- **AI-assisted recovery** — Heals broken selectors, dismisses modals, restores sessions
- **Spring AI / LiteLLM integration** — Connect any LLM (GPT-4o, Claude, Mistral, local)
- **Self-contained HTML + JSON reports** — Per-execution artifacts with screenshots and videos
- **React + TypeScript dashboard** — Live execution view, scenario explorer, AI insights

## 🏗 Architecture

```
┌───────────────────────────────────────────────────────────┐
│                      AegisQA Platform                     │
│                                                           │
│  React Dashboard (Vite + Tailwind)                        │
│          │                                                │
│  Spring Boot REST API (api-gateway)                       │
│          │                                                │
│  ┌───────┴────────────────────────────────────┐           │
│  │           Orchestration Engine             │           │
│  │  WorkflowExecutor → StepDispatcher         │           │
│  └──────┬──────────────┬──────────────┬───────┘           │
│         │              │              │                   │
│  Scenario Engine  Browser Executor  AI Assistant          │
│  (YAML/MD/XLSX)   (Playwright +     (Spring AI +          │
│  Rule Engine      MCP Tools)        LiteLLM)              │
│                                                           │
│  Recovery Engine │ Reporting Module │ Domain Model        │
│                                                           │
│          PostgreSQL / H2  +  Flyway Migrations            │
└───────────────────────────────────────────────────────────┘
```

## 📦 Modules

| Module | Description |
|--------|-------------|
| `domain-module` | ACS enums, canonical action types, scenario/execution/assertion models |
| `scenario-engine` | YAML/Markdown/Excel/PlainText parsers, rule-based normalizer |
| `orchestration-engine` | Workflow executor, state management, retry with exponential backoff |
| `browser-executor` | Playwright lifecycle, page objects (RiskManagement, Transaction), selector registry |
| `playwright-mcp-integration` | MCP tool wrappers, registry, validator |
| `ai-assistant-module` | Spring AI + LiteLLM; scenario interpreter, healer, summarizer, recovery |
| `recovery-engine` | Modal/session/stall detectors, recovery strategies |
| `reporting-module` | HTML and JSON report generators |
| `api-gateway` | Spring Boot REST API, Swagger UI, Flyway migrations |

## 🚀 Getting Started

### Prerequisites

- Java 17 (Eclipse Temurin)
- Maven 3.9+
- Node.js 20+
- Docker (optional)

### Backend

```bash
mvn -f backend/pom.xml compile
# Start the API (H2 in-memory by default)
mvn -f backend/pom.xml spring-boot:run -pl api-gateway
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend

```bash
cd frontend
npm install
npm run dev   # http://localhost:5173
```

### Docker Compose

```bash
docker compose up -d postgres backend
# With AI:
OPENAI_API_KEY=your-key docker compose --profile with-ai up -d
```

## ⚙️ Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `LITELLM_BASE_URL` | `http://localhost:4000` | LLM endpoint |
| `LITELLM_API_KEY` | `dummy-key` | LLM API key |
| `LITELLM_MODEL` | `gpt-4o` | Model name |
| `BROWSER_HEADLESS` | `true` | Headless Chromium |
| `AEGISQA_DB_URL` | H2 in-memory | JDBC URL |

## 📝 Scenario Formats

### YAML
```yaml
name: Frictionless Visa 2.2
steps:
  - action: add_rba_adapter
  - action: create_risk_chain
  - action: set_risk_score
    min: "0"
    max: "100"
    result: frictionless
  - action: run_transaction
    scheme: visa
  - action: verify_transaction_status
    expected: "Y"
```

### Plain Text
```
Add RBA adapter
Create risk chain
Set score frictionless 0..100
Do a Visa transaction
Transaction status should be Y
```

## 🌐 REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/scenarios/parse` | Parse a scenario |
| `POST` | `/api/v1/executions/run` | Execute a scenario |
| `GET` | `/api/v1/executions/{id}` | Get execution state |
| `GET` | `/api/v1/mcp/tools` | List MCP tools |
| `POST` | `/api/v1/ai/summarize-failure` | AI failure analysis |
| `POST` | `/api/v1/ai/heal-selector` | AI selector healing |

## 📁 Project Structure

```
AegisQA/
├── backend/          # Java 17 Spring Boot multi-module backend
├── frontend/         # React + TypeScript + Vite + Tailwind UI
├── skills/           # YAML skill definitions (browser, acs, recovery)
├── prompts/          # LLM prompt templates
├── scenarios/        # Sample ACS test scenarios
├── infrastructure/   # Nginx configuration
├── Dockerfile        # Multi-stage Docker build
└── docker-compose.yml
```

## 🤝 Contributing

- All new ACS actions → `CanonicalActionType.java` + `acs.skill.yaml`
- New parsers → implement `ScenarioParser`, register with Spring
- New recovery strategies → implement `RecoveryStrategy` + `RecoveryDetector`
- Backend: `mvn -f backend/pom.xml compile`
- Frontend: `cd frontend && npm run build`

---
*AegisQA — Built for ACS QA teams*
