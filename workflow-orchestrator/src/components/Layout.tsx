import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { LogOut, LayoutDashboard } from 'lucide-react';

export default function Layout() {
  const { logout } = useAuth();

  return (
    <div className="min-h-screen bg-slate-50">
      <nav className="bg-white border-b border-slate-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              <Link to="/" className="flex items-center px-2 py-2 text-slate-900 font-semibold text-lg">
                <LayoutDashboard className="w-5 h-5 mr-2 text-indigo-600" />
                Workflow Orchestrator
              </Link>
            </div>
            <div className="flex items-center">
              <button
                onClick={logout}
                className="flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-slate-500 hover:text-slate-700 focus:outline-none transition ease-in-out duration-150"
              >
                <LogOut className="w-4 h-4 mr-2" />
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <Outlet />
      </main>
    </div>
  );
}
