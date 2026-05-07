import { type FC } from 'react';
import type { ExecutionStatus } from '../types';

const statusColors: Record<ExecutionStatus, string> = {
  PENDING: 'bg-gray-700 text-gray-300',
  RUNNING: 'bg-blue-700 text-blue-100',
  PAUSED: 'bg-yellow-700 text-yellow-100',
  COMPLETED: 'bg-green-700 text-green-100',
  FAILED: 'bg-red-700 text-red-100',
  RETRYING: 'bg-orange-700 text-orange-100',
  CANCELLED: 'bg-gray-600 text-gray-300',
};

const StatusBadge: FC<{ status: ExecutionStatus }> = ({ status }) => (
  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${statusColors[status]}`}>
    {status}
  </span>
);

export default StatusBadge;
