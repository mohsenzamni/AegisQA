import { type FC } from 'react';
import { FileBarChart, CheckCircle, XCircle, Clock } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import type { ExecutionStatus } from '../types';

interface ReportEntry {
  id: string;
  scenarioName: string;
  status: ExecutionStatus;
  totalSteps: number;
  passedSteps: number;
  failedSteps: number;
  durationMs: number;
  completedAt: string;
}

// Demo data for the report viewer
const DEMO_REPORTS: ReportEntry[] = [
  { id: 'rpt-001', scenarioName: 'Frictionless Visa 2.2', status: 'COMPLETED', totalSteps: 8, passedSteps: 8, failedSteps: 0, durationMs: 22100, completedAt: '2026-05-07 08:45:12' },
  { id: 'rpt-002', scenarioName: 'Challenge Mastercard 2.1', status: 'FAILED', totalSteps: 8, passedSteps: 5, failedSteps: 3, durationMs: 14300, completedAt: '2026-05-07 08:52:44' },
  { id: 'rpt-003', scenarioName: 'Whitelist EMV 2.3', status: 'COMPLETED', totalSteps: 7, passedSteps: 7, failedSteps: 0, durationMs: 19800, completedAt: '2026-05-07 09:01:07' },
  { id: 'rpt-004', scenarioName: 'RBA Amex 2.2 Decoupled', status: 'COMPLETED', totalSteps: 6, passedSteps: 6, failedSteps: 0, durationMs: 18900, completedAt: '2026-05-07 09:05:33' },
  { id: 'rpt-005', scenarioName: 'Session Expire Recovery', status: 'FAILED', totalSteps: 5, passedSteps: 2, failedSteps: 3, durationMs: 8200, completedAt: '2026-05-06 17:22:11' },
];

const ReportViewer: FC = () => {
  const totalRuns = DEMO_REPORTS.length;
  const passed = DEMO_REPORTS.filter((r) => r.status === 'COMPLETED').length;
  const failed = DEMO_REPORTS.filter((r) => r.status === 'FAILED').length;
  const avgDuration = DEMO_REPORTS.reduce((acc, r) => acc + r.durationMs, 0) / DEMO_REPORTS.length;

  return (
    <div className="p-6">
      <div className="flex items-center gap-3 mb-6">
        <FileBarChart size={24} className="text-purple-400" />
        <h1 className="text-xl font-bold text-white">Report Viewer</h1>
      </div>

      {/* Summary stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <div className="text-xs text-gray-500">Total Runs</div>
          <div className="text-2xl font-bold text-white mt-1">{totalRuns}</div>
        </div>
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <div className="flex items-center gap-1 text-xs text-green-400">
            <CheckCircle size={12} /> Passed
          </div>
          <div className="text-2xl font-bold text-green-400 mt-1">{passed}</div>
        </div>
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <div className="flex items-center gap-1 text-xs text-red-400">
            <XCircle size={12} /> Failed
          </div>
          <div className="text-2xl font-bold text-red-400 mt-1">{failed}</div>
        </div>
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <div className="flex items-center gap-1 text-xs text-gray-400">
            <Clock size={12} /> Avg Duration
          </div>
          <div className="text-2xl font-bold text-white mt-1">{(avgDuration / 1000).toFixed(1)}s</div>
        </div>
      </div>

      {/* Reports table */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800 text-xs text-gray-500 uppercase tracking-wider">
              <th className="text-left px-5 py-3">Scenario</th>
              <th className="text-left px-5 py-3">Status</th>
              <th className="text-right px-5 py-3">Steps</th>
              <th className="text-right px-5 py-3">Pass Rate</th>
              <th className="text-right px-5 py-3">Duration</th>
              <th className="text-right px-5 py-3">Completed</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {DEMO_REPORTS.map((r) => {
              const passRate = Math.round((r.passedSteps / r.totalSteps) * 100);
              return (
                <tr key={r.id} className="hover:bg-gray-800/50 transition-colors cursor-pointer">
                  <td className="px-5 py-3">
                    <div className="font-medium text-white">{r.scenarioName}</div>
                    <div className="text-xs text-gray-600 font-mono">{r.id}</div>
                  </td>
                  <td className="px-5 py-3">
                    <StatusBadge status={r.status} />
                  </td>
                  <td className="px-5 py-3 text-right text-gray-300">
                    {r.passedSteps}/{r.totalSteps}
                  </td>
                  <td className="px-5 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <div className="w-16 bg-gray-700 rounded-full h-1.5">
                        <div
                          className={`h-1.5 rounded-full ${passRate === 100 ? 'bg-green-500' : 'bg-red-500'}`}
                          style={{ width: `${passRate}%` }}
                        />
                      </div>
                      <span className="text-xs text-gray-400">{passRate}%</span>
                    </div>
                  </td>
                  <td className="px-5 py-3 text-right text-gray-400">
                    {(r.durationMs / 1000).toFixed(1)}s
                  </td>
                  <td className="px-5 py-3 text-right text-xs text-gray-500">
                    {r.completedAt}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ReportViewer;
