// Domain types mirroring the backend Java models

export type TransactionStatus = 'Y' | 'C' | 'N' | 'U' | 'R' | 'A';
export type ExecutionStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'PAUSED'
  | 'COMPLETED'
  | 'FAILED'
  | 'RETRYING'
  | 'CANCELLED';

export type ScenarioSource = 'YAML' | 'MARKDOWN' | 'EXCEL' | 'PLAIN_TEXT' | 'JSON';

export interface ScenarioStep {
  stepNumber: number;
  rawText: string;
  actionType: string;
  parameters?: Record<string, string>;
}

export interface Scenario {
  id: string;
  name: string;
  description?: string;
  source: ScenarioSource;
  steps: ScenarioStep[];
}

export interface ExecutionState {
  executionId: string;
  scenarioId: string;
  status: ExecutionStatus;
  currentStep: number;
  totalSteps: number;
  transactionId?: string;
  startedAt?: string;
  completedAt?: string;
  lastError?: string;
  retryCount: number;
}

export interface StepResult {
  stepNumber: number;
  actionType: string;
  status: ExecutionStatus;
  durationMs: number;
  startedAt?: string;
  completedAt?: string;
  errorMessage?: string;
  screenshotPath?: string;
}

export interface AssertionResult {
  assertion: string;
  expected?: string;
  actual?: string;
  status: 'PASS' | 'FAIL' | 'SKIPPED' | 'PENDING' | 'ERROR';
  message?: string;
}

export interface ExecutionReport {
  reportId: string;
  executionId: string;
  scenarioId: string;
  scenarioName: string;
  finalStatus: ExecutionStatus;
  startedAt?: string;
  completedAt?: string;
  durationMs: number;
  totalSteps: number;
  passedSteps: number;
  failedSteps: number;
  stepResults: StepResult[];
  allAssertions: AssertionResult[];
  aiSummary?: string;
  htmlReportPath?: string;
  jsonReportPath?: string;
}

export interface ScenarioRequest {
  content: string;
  name: string;
  source?: ScenarioSource;
}
