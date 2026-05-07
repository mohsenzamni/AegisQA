import { type FC, type ReactNode } from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  TestTube2,
  Play,
  Bot,
  FileBarChart,
  Shield,
} from 'lucide-react';

interface NavItem {
  to: string;
  icon: ReactNode;
  label: string;
}

const nav: NavItem[] = [
  { to: '/', icon: <LayoutDashboard size={18} />, label: 'Dashboard' },
  { to: '/scenarios', icon: <TestTube2 size={18} />, label: 'Scenarios' },
  { to: '/execution', icon: <Play size={18} />, label: 'Live Execution' },
  { to: '/ai', icon: <Bot size={18} />, label: 'AI Insights' },
  { to: '/reports', icon: <FileBarChart size={18} />, label: 'Reports' },
];

const Layout: FC<{ children: ReactNode }> = ({ children }) => {
  return (
    <div className="flex h-screen bg-gray-950 text-gray-100 overflow-hidden">
      {/* Sidebar */}
      <aside className="w-56 bg-gray-900 border-r border-gray-800 flex flex-col shrink-0">
        {/* Logo */}
        <div className="flex items-center gap-2 px-4 py-4 border-b border-gray-800">
          <Shield size={22} className="text-purple-400" />
          <span className="font-bold text-lg text-white">AegisQA</span>
        </div>

        {/* Nav */}
        <nav className="flex-1 py-4 px-2 space-y-1">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-purple-600 text-white'
                    : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800'
                }`
              }
            >
              {item.icon}
              {item.label}
            </NavLink>
          ))}
        </nav>

        {/* Footer */}
        <div className="px-4 py-3 border-t border-gray-800 text-xs text-gray-500">
          v1.0.0-SNAPSHOT
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-y-auto">
        {children}
      </main>
    </div>
  );
};

export default Layout;
