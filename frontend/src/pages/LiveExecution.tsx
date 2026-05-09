import { type FC, useState, useEffect, useRef } from 'react';
import { Play, Square, RotateCcw, Zap } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import { api } from '../api/client';
import type { ExecutionState, ScenarioRequest } from '../types';

const DEMO_SCENARIO = `Add RBA adapter
Create risk chain
Set score frictionless 0..100
Do a Visa transaction
Transaction status should be Y`;

const LiveExecution: FC = () => {
  const [scenarioContent, setScenarioContent] = useState(DEMO_SCENARIO);
  const [executionId, setExecutionId] = useState<string | null>(null);
  const [state, setState] = useState<ExecutionState | null>(null);
  const [logs, setLogs] = useState<string[]>([]);
  const [running, setRunning] = useState(false);
  const logsRef = useRef<HTMLDivElement>(null);

  const addLog = (msg: string) => {
    const ts = new Date().toLocaleTimeString();
    setLogs((prev) => [...prev, `[${ts}] ${msg}`]);
  };

  const handleRun = async () => {
    setRunning(true);
    setLogs([]);
    setState(null);
    addLog('Starting execution...');
    try {
      const req: ScenarioRequest = { content: scenarioContent, name: 'Live Test' };
      const res = await api.post<{ executionId: string }>('/executions/run', req);
      setExecutionId(res.executionId);
      addLog(`Execution started: ${res.executionId}`);
    } catch (e: unknown) {
      addLog(`Error: ${e instanceof Error ? e.message : 'Failed to start'}`);
      setRunning(false);
    }
  };

  const handleCancel = async () => {
    if (!executionId) return;
    await api.post(`/executions/${executionId}/cancel`);
    addLog('Execution cancelled');
    setRunning(false);
  };

  // Poll execution state
  useEffect(() => {
    if (!executionId || !running) return;
    const interval = setInterval(async () => {
      try {
        const s = await api.get<ExecutionState>(`/executions/${executionId}`);
        setState(s);
        addLog(`Step ${s.currentStep}/${s.totalSteps} — ${s.status}`);
        if (s.status === 'COMPLETED' || s.status === 'FAILED' || s.status === 'CANCELLED') {
          setRunning(false);
          clearInterval(interval);
          addLog(`Execution ${s.status}`);
        }
      } catch {
        // Backend may not be running in demo mode
      }
    }, 1500);
    return () => clearInterval(interval);
  }, [executionId, running]);

  // Auto-scroll logs
  useEffect(() => {
    if (logsRef.current) {
      logsRef.current.scrollTop = logsRef.current.scrollHeight;
    }
  }, [logs]);

  const progress = state ? (state.currentStep / Math.max(state.totalSteps, 1)) * 100 : 0;

  return (
    <div className="p-6">
      <div className="flex items-center gap-3 mb-6">
        <Play size={24} className="text-purple-400" />
        <h1 className="text-xl font-bold text-white">Live Execution</h1>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        {/* Scenario input */}
        <div className="xl:col-span-1 bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-gray-300 mb-4">Scenario</h2>
          <textarea
            className="w-full h-48 bg-gray-800 border border-gray-700 rounded-lg px-3 py-3 text-sm text-white font-mono focus:outline-none focus:ring-1 focus:ring-purple-500 resize-none"
            value={scenarioContent}
            onChange={(e) => setScenarioContent(e.target.value)}
            disabled={running}
          />
          <div className="flex gap-2 mt-4">
            <button
              onClick={handleRun}
              disabled={running}
              className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <Zap size={14} />
              Run
            </button>
            <button
              onClick={handleCancel}
              disabled={!running}
              className="flex items-center gap-2 bg-red-700 hover:bg-red-800 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <Square size={14} />
              Cancel
            </button>
            <button
              onClick={() => { setLogs([]); setState(null); setExecutionId(null); setRunning(false); }}
              className="flex items-center gap-2 text-gray-400 hover:text-gray-200 px-3 py-2 rounded-lg hover:bg-gray-800 transition-colors text-sm"
            >
              <RotateCcw size={14} />
            </button>
          </div>
        </div>

        {/* State panel */}
        <div className="xl:col-span-2 space-y-4">
          {/* Status card */}
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-sm font-semibold text-gray-300">Execution State</h2>
              {state && <StatusBadge status={state.status} />}
            </div>

            {state ? (
              <div>
                <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
                  <div>
                    <div className="text-gray-500 text-xs">Execution ID</div>
                    <div className="font-mono text-gray-300 text-xs">{state.executionId.slice(0, 16)}…</div>
                  </div>
                  <div>
                    <div className="text-gray-500 text-xs">Step Progress</div>
                    <div className="text-white font-medium">{state.currentStep} / {state.totalSteps}</div>
                  </div>
                </div>
                <div className="w-full bg-gray-700 rounded-full h-2">
                  <div
                    className="bg-purple-500 h-2 rounded-full transition-all duration-500"
                    style={{ width: `${progress}%` }}
                  />
                </div>
                <div className="text-xs text-gray-500 mt-1 text-right">{Math.round(progress)}%</div>
              </div>
            ) : (
              <div className="text-sm text-gray-600 py-8 text-center">
                Run a scenario to see live state
              </div>
            )}
          </div>

          {/* Logs */}
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
            <h2 className="text-sm font-semibold text-gray-300 mb-3">Execution Log</h2>
            <div
              ref={logsRef}
              className="h-48 overflow-y-auto font-mono text-xs text-green-400 bg-gray-950 rounded-lg p-3 space-y-0.5"
            >
              {logs.length === 0 && (
                <div className="text-gray-600">Waiting for execution...</div>
              )}
              {logs.map((log, i) => (
                <div key={i}>{log}</div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LiveExecution;
