import { type FC, useState } from 'react';
import { Bot, Wand2, MessageSquare, AlertCircle } from 'lucide-react';
import { api } from '../api/client';

const AiInsights: FC = () => {
  // Failure summarizer
  const [failureForm, setFailureForm] = useState({
    executionId: 'exec-001',
    failedAction: 'run_transaction',
    errorMessage: 'Timeout waiting for transaction result after 30s',
    context: 'Visa 2.2 frictionless path',
  });
  const [failureSummary, setFailureSummary] = useState('');
  const [failureLoading, setFailureLoading] = useState(false);

  // Selector healer
  const [healForm, setHealForm] = useState({
    brokenSelector: 'button.create-chain-btn',
    context: 'Create risk chain button in Risk Management module',
  });
  const [healedSelector, setHealedSelector] = useState('');
  const [healLoading, setHealLoading] = useState(false);

  const handleSummarize = async () => {
    setFailureLoading(true);
    setFailureSummary('');
    try {
      const res = await api.post<{ summary: string }>('/ai/summarize-failure', failureForm);
      setFailureSummary(res.summary);
    } catch {
      setFailureSummary('AI service unavailable. Check LITELLM_BASE_URL configuration.');
    } finally {
      setFailureLoading(false);
    }
  };

  const handleHeal = async () => {
    setHealLoading(true);
    setHealedSelector('');
    try {
      const res = await api.post<{ suggestedSelector: string }>('/ai/heal-selector', healForm);
      setHealedSelector(res.suggestedSelector || 'No suggestion returned');
    } catch {
      setHealedSelector('AI service unavailable');
    } finally {
      setHealLoading(false);
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center gap-3 mb-6">
        <Bot size={24} className="text-purple-400" />
        <h1 className="text-xl font-bold text-white">AI Insights</h1>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Failure Summarizer */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <div className="flex items-center gap-2 mb-4">
            <AlertCircle size={16} className="text-red-400" />
            <h2 className="text-sm font-semibold text-gray-300">Failure Summarizer</h2>
          </div>

          <div className="space-y-3 mb-4">
            {[
              { key: 'executionId', label: 'Execution ID' },
              { key: 'failedAction', label: 'Failed Action' },
              { key: 'errorMessage', label: 'Error Message' },
              { key: 'context', label: 'Context' },
            ].map(({ key, label }) => (
              <div key={key}>
                <label className="text-xs text-gray-500 mb-1 block">{label}</label>
                <input
                  type="text"
                  className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-1 focus:ring-purple-500"
                  value={failureForm[key as keyof typeof failureForm]}
                  onChange={(e) => setFailureForm((f) => ({ ...f, [key]: e.target.value }))}
                />
              </div>
            ))}
          </div>

          <button
            onClick={handleSummarize}
            disabled={failureLoading}
            className="flex items-center gap-2 bg-red-700 hover:bg-red-800 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            <MessageSquare size={14} />
            {failureLoading ? 'Analyzing...' : 'Summarize Failure'}
          </button>

          {failureSummary && (
            <div className="mt-4 p-3 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-300 whitespace-pre-wrap">
              {failureSummary}
            </div>
          )}
        </div>

        {/* Selector Healer */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <div className="flex items-center gap-2 mb-4">
            <Wand2 size={16} className="text-purple-400" />
            <h2 className="text-sm font-semibold text-gray-300">Selector Healer</h2>
          </div>

          <div className="space-y-3 mb-4">
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Broken Selector</label>
              <input
                type="text"
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm font-mono text-white focus:outline-none focus:ring-1 focus:ring-purple-500"
                value={healForm.brokenSelector}
                onChange={(e) => setHealForm((f) => ({ ...f, brokenSelector: e.target.value }))}
              />
            </div>
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Element Context</label>
              <input
                type="text"
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:ring-1 focus:ring-purple-500"
                value={healForm.context}
                onChange={(e) => setHealForm((f) => ({ ...f, context: e.target.value }))}
              />
            </div>
          </div>

          <button
            onClick={handleHeal}
            disabled={healLoading}
            className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            <Wand2 size={14} />
            {healLoading ? 'Healing...' : 'Suggest Alternative'}
          </button>

          {healedSelector && (
            <div className="mt-4">
              <div className="text-xs text-gray-500 mb-1">Suggested Selector</div>
              <code className="block p-3 bg-gray-800 border border-purple-800/50 rounded-lg text-sm text-purple-300 font-mono">
                {healedSelector}
              </code>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AiInsights;
