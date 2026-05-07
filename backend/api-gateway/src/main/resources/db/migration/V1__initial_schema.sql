-- AegisQA Initial Schema
-- V1__initial_schema.sql

CREATE TABLE IF NOT EXISTS scenario_run (
    id              VARCHAR(36) PRIMARY KEY,
    scenario_id     VARCHAR(36) NOT NULL,
    scenario_name   VARCHAR(255) NOT NULL,
    status          VARCHAR(50) NOT NULL,
    total_steps     INT NOT NULL DEFAULT 0,
    passed_steps    INT NOT NULL DEFAULT 0,
    failed_steps    INT NOT NULL DEFAULT 0,
    started_at      TIMESTAMP,
    completed_at    TIMESTAMP,
    duration_ms     BIGINT,
    html_report     VARCHAR(500),
    json_report     VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_scenario_run_status ON scenario_run(status);
CREATE INDEX IF NOT EXISTS idx_scenario_run_scenario_id ON scenario_run(scenario_id);
