# ──────────────────────────────────────────────
# Stage 1: Build the Spring Boot backend
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS backend-build

WORKDIR /workspace

COPY backend/pom.xml ./
COPY backend/domain-module/pom.xml domain-module/
COPY backend/scenario-engine/pom.xml scenario-engine/
COPY backend/orchestration-engine/pom.xml orchestration-engine/
COPY backend/browser-executor/pom.xml browser-executor/
COPY backend/playwright-mcp-integration/pom.xml playwright-mcp-integration/
COPY backend/ai-assistant-module/pom.xml ai-assistant-module/
COPY backend/recovery-engine/pom.xml recovery-engine/
COPY backend/reporting-module/pom.xml reporting-module/
COPY backend/api-gateway/pom.xml api-gateway/

RUN mvn --settings /root/.m2/settings.xml dependency:go-offline -B 2>/dev/null || true

COPY backend/ .
RUN mvn package -DskipTests -B -pl api-gateway -am

# ──────────────────────────────────────────────
# Stage 2: Build the React frontend
# ──────────────────────────────────────────────
FROM node:20-alpine AS frontend-build

WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ──────────────────────────────────────────────
# Stage 3: Runtime image
# ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

LABEL org.opencontainers.image.title="AegisQA Platform"
LABEL org.opencontainers.image.description="Enterprise AI-Driven ACS UI Test Platform"
LABEL org.opencontainers.image.version="1.0.0"

WORKDIR /app

# Install Chromium for Playwright headless
RUN apk add --no-cache chromium nss freetype harfbuzz ca-certificates ttf-freefont

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
ENV PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH=/usr/bin/chromium-browser

# Copy backend jar
COPY --from=backend-build /workspace/api-gateway/target/api-gateway-*.jar app.jar

# Copy frontend dist as static resources served by Spring Boot
COPY --from=frontend-build /app/dist /app/static

# Reports directory
RUN mkdir -p /app/reports

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Xmx512m", \
  "-jar", "app.jar"]
