# Proxy Monitoring Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first runnable Spring Boot backend for the proxy-monitoring workshop with three internal services, reusable audit proxies, metrics endpoints, and tests.

**Architecture:** A single Spring Boot application will expose service-execution endpoints that delegate into generic logging proxies instead of direct services. In-memory repositories will hold audit data and demo business data, while metrics will be derived from audit records to support a real-time frontend later.

**Tech Stack:** Java 21, Spring Boot 3, Maven, Spring Web, Spring Validation, Jackson, JUnit 5

---

### Task 1: Project Skeleton

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/revelosn/proxymonitoring/ProxyMonitoringApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `Dockerfile`
- Create: `README.md`

- [ ] Add Maven build, Spring Boot entrypoint, configuration, and delivery docs.

### Task 2: TDD Core Proxy and Payment Failure Logic

**Files:**
- Create: `src/test/java/com/revelosn/proxymonitoring/proxy/LoggingProxyTest.java`
- Create: `src/test/java/com/revelosn/proxymonitoring/service/PaymentServiceTest.java`
- Create: `src/main/java/com/revelosn/proxymonitoring/proxy/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/service/...`

- [ ] Write failing tests for proxy success/error behavior and the 10% payment failure rule.
- [ ] Implement the minimal production code to pass those tests.

### Task 3: TDD Metrics and Repositories

**Files:**
- Create: `src/test/java/com/revelosn/proxymonitoring/metrics/MetricsServiceTest.java`
- Create: `src/main/java/com/revelosn/proxymonitoring/repository/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/metrics/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/model/...`

- [ ] Write failing tests for aggregated metrics and log pagination/filtering inputs.
- [ ] Implement thread-safe in-memory repositories and metrics aggregation.

### Task 4: TDD HTTP Layer

**Files:**
- Create: `src/test/java/com/revelosn/proxymonitoring/controller/ServiceExecutionControllerIntegrationTest.java`
- Create: `src/main/java/com/revelosn/proxymonitoring/controller/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/dto/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/config/...`
- Create: `src/main/java/com/revelosn/proxymonitoring/exception/...`

- [ ] Write failing integration tests for the execution endpoints and response envelope.
- [ ] Implement controllers, validation, CORS, and global error handling.

### Task 5: Finish Demo Data, Load Simulation, and Verification

**Files:**
- Create: `src/main/java/com/revelosn/proxymonitoring/metrics/LoadSimulationService.java`
- Create: `src/main/java/com/revelosn/proxymonitoring/config/DemoDataInitializer.java`
- Update: `README.md`

- [ ] Seed demo data, implement the load simulation endpoint, document usage, and run full verification.

