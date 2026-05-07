import { type FC } from 'react';
import { Shield, Activity, CheckCircle, XCircle, Clock, TrendingUp } from 'lucide-react';

interface StatCardProps {
  label: string;
  value: string | number;
  icon: React.ReactNode;
  trend?: string;
  color: string;
}

const StatCard: FC<StatCardProps> = ({ label, value, icon, trend, color }) => (
  <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
    <div className="flex items-center justify-between mb-3">
      <span className="text-sm text-gray-400">{label}</span>
      <span className={`p-2 rounded-lg ${color}`}>{icon}</span>
    </div>
    <div className="text-2xl font-bold text-white mb-1">{value}</div>
    {trend && <div className="text-xs text-gray-500">{trend}</div>}
  </div>
);

const Dashboard: FC = () => {
  // In production these would come from API via React Query
  const stats = [
    { label: 'Total Executions', value: 147, icon: <Activity size={16} />, color: 'bg-purple-900 text-purple-300', trend: '+12 today' },
    { label: 'Passed', value: 128, icon: <CheckCircle size={16} />, color: 'bg-green-900 text-green-300', trend: '87% pass rate' },
    { label: 'Failed', value: 19, icon: <XCircle size={16} />, color: 'bg-red-900 text-red-300', trend: '13% fail rate' },
    { label: 'Avg Duration', value: '4.2s', icon: <Clock size={16} />, color: 'bg-blue-900 text-blue-300', trend: 'per step' },
  ];

  const recentRuns = [
    { id: 'exec-001', name: 'Frictionless Visa 2.2', status: 'COMPLETED', steps: '8/8', duration: '22.1s', ts: '2 min ago' },
    { id: 'exec-002', name: 'Challenge MC 2.1', status: 'FAILED', steps: '5/8', duration: '14.3s', ts: '8 min ago' },
    { id: 'exec-003', name: 'Whitelist EMV 2.3', status: 'RUNNING', steps: '3/7', duration: '...',ts: 'now' },
    { id: 'exec-004', name: 'RBA Amex 2.2', status: 'COMPLETED', steps: '6/6', duration: '18.9s', ts: '21 min ago' },
  ];

  const statusBg: Record<string, string> = {
    COMPLETED: 'text-green-400',
    FAILED: 'text-red-400',
    RUNNING: 'text-blue-400',
    PENDING: 'text-gray-400',
  };

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <Shield size={28} className="text-purple-400" />
        <div>
          <h1 className="text-2xl font-bold text-white">AegisQA Dashboard</h1>
          <p className="text-sm text-gray-400">Enterprise AI-Driven ACS Test Platform</p>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {stats.map((s) => (
          <StatCard key={s.label} {...s} />
        ))}
      </div>

      {/* Recent runs */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl">
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-800">
          <h2 className="text-sm font-semibold text-gray-200">Recent Executions</h2>
          <TrendingUp size={16} className="text-gray-500" />
        </div>
        <div className="divide-y divide-gray-800">
          {recentRuns.map((run) => (
            <div key={run.id} className="flex items-center px-5 py-3 hover:bg-gray-800/50 transition-colors">
              <div className="flex-1 min-w-0">
                <div className="text-sm font-medium text-white truncate">{run.name}</div>
                <div className="text-xs text-gray-500">{run.id}</div>
              </div>
              <div className={`text-xs font-medium mx-4 ${statusBg[run.status]}`}>{run.status}</div>
              <div className="text-xs text-gray-400 w-16 text-right">{run.steps}</div>
              <div className="text-xs text-gray-500 w-16 text-right">{run.duration}</div>
              <div className="text-xs text-gray-600 w-20 text-right">{run.ts}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
