import { type FC, useState } from 'react';
import { TestTube2, Upload, Check, AlertCircle } from 'lucide-react';
import { api } from '../api/client';
import type { Scenario, ScenarioRequest } from '../types';

const SAMPLE_SCENARIO = `Add RBA adapter
Create risk chain
Set score frictionless 0..100
Do a Visa transaction
Transaction status should be Y`;

const SAMPLE_YAML = `name: Frictionless Visa 2.2
description: Test frictionless path for Visa scheme
steps:
  - action: add_rba_adapter
  - action: create_risk_chain
    name: default
  - action: set_risk_score
    min: "0"
    max: "100"
    result: frictionless
  - action: run_transaction
    scheme: visa
  - action: verify_transaction_status
    expected: "Y"`;

const ScenarioExplorer: FC = () => {
  const [content, setContent] = useState(SAMPLE_SCENARIO);
  const [name, setName] = useState('My ACS Scenario');
  const [source, setSource] = useState('');
  const [result, setResult] = useState<Scenario | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleParse = async () => {
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const req: ScenarioRequest = { content, name };
      if (source) req.source = source as ScenarioRequest['source'];
      const scenario = await api.post<Scenario>('/scenarios/parse', req);
      setResult(scenario);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Parse failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center gap-3 mb-6">
        <TestTube2 size={24} className="text-purple-400" />
        <h1 className="text-xl font-bold text-white">Scenario Explorer</h1>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Editor */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-gray-300 mb-4">Input Scenario</h2>

          <div className="flex gap-3 mb-4">
            <input
              type="text"
              className="flex-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white placeholder-gray-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
              placeholder="Scenario name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            <select
              className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-gray-300 focus:outline-none focus:ring-1 focus:ring-purple-500"
              value={source}
              onChange={(e) => setSource(e.target.value)}
            >
              <option value="">Auto-detect</option>
              <option value="PLAIN_TEXT">Plain Text</option>
              <option value="YAML">YAML</option>
              <option value="MARKDOWN">Markdown</option>
            </select>
          </div>

          <textarea
            className="w-full h-64 bg-gray-800 border border-gray-700 rounded-lg px-3 py-3 text-sm text-white font-mono placeholder-gray-600 focus:outline-none focus:ring-1 focus:ring-purple-500 resize-none"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Enter your ACS test scenario..."
          />

          <div className="flex gap-2 mt-4">
            <button
              onClick={handleParse}
              disabled={loading}
              className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              <Upload size={14} />
              {loading ? 'Parsing...' : 'Parse Scenario'}
            </button>
            <button
              onClick={() => setContent(SAMPLE_YAML)}
              className="text-sm text-gray-400 hover:text-gray-200 px-3 py-2 rounded-lg hover:bg-gray-800 transition-colors"
            >
              Load YAML Example
            </button>
          </div>

          {error && (
            <div className="mt-3 flex items-center gap-2 text-red-400 text-sm">
              <AlertCircle size={14} />
              {error}
            </div>
          )}
        </div>

        {/* Parse result */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-gray-300 mb-4">Parsed Steps</h2>

          {!result && !loading && (
            <div className="text-sm text-gray-600 text-center py-16">
              Parse a scenario to see its canonical action steps
            </div>
          )}

          {result && (
            <div>
              <div className="mb-4 pb-3 border-b border-gray-800">
                <div className="text-sm font-medium text-white">{result.name}</div>
                <div className="text-xs text-gray-500 mt-1">
                  Source: {result.source} · ID: {result.id.slice(0, 8)}…
                </div>
              </div>

              <div className="space-y-2 max-h-[400px] overflow-y-auto">
                {result.steps.map((step) => (
                  <div
                    key={step.stepNumber}
                    className={`flex items-start gap-3 p-3 rounded-lg border ${
                      step.actionType === 'unknown'
                        ? 'border-red-800/60 bg-red-900/10'
                        : 'border-gray-700/60 bg-gray-800/30'
                    }`}
                  >
                    <div className="text-xs font-mono text-gray-600 w-5 text-center mt-0.5">
                      {step.stepNumber}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        {step.actionType === 'unknown' ? (
                          <AlertCircle size={12} className="text-red-400 shrink-0" />
                        ) : (
                          <Check size={12} className="text-green-400 shrink-0" />
                        )}
                        <code className="text-xs text-purple-300 font-mono">{step.actionType}</code>
                      </div>
                      <div className="text-xs text-gray-500 mt-0.5 truncate">{step.rawText}</div>
                      {step.parameters && Object.keys(step.parameters).length > 0 && (
                        <div className="mt-1 flex flex-wrap gap-1">
                          {Object.entries(step.parameters).map(([k, v]) => (
                            <span key={k} className="text-xs bg-gray-700 text-gray-300 px-1.5 py-0.5 rounded">
                              {k}: {v}
                            </span>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ScenarioExplorer;
