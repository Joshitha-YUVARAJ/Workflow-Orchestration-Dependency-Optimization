import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/client';
import { Workflow } from '../types';
import { Plus, Trash2, ArrowRight } from 'lucide-react';
import { format } from 'date-fns';

export default function Dashboard() {
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [newWorkflow, setNewWorkflow] = useState({ name: '', description: '' });

  const fetchWorkflows = async () => {
    try {
      const { data } = await apiClient.get<Workflow[]>('/workflows');
      setWorkflows(data);
    } catch (error) {
      console.error('Failed to fetch workflows', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWorkflows();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post('/workflows', newWorkflow);
      setIsCreating(false);
      setNewWorkflow({ name: '', description: '' });
      fetchWorkflows();
    } catch (error) {
      console.error('Failed to create workflow', error);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this workflow?')) return;
    try {
      await apiClient.delete(`/workflows/${id}`);
      fetchWorkflows();
    } catch (error) {
      console.error('Failed to delete workflow', error);
    }
  };

  if (loading) return <div className="p-8 text-center text-slate-500">Loading workflows...</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-slate-900">Workflows</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          <Plus className="w-4 h-4 mr-2" />
          New Workflow
        </button>
      </div>

      {isCreating && (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
          <h2 className="text-lg font-medium text-slate-900 mb-4">Create New Workflow</h2>
          <form onSubmit={handleCreate} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700">Name</label>
              <input
                type="text"
                required
                value={newWorkflow.name}
                onChange={(e) => setNewWorkflow({ ...newWorkflow, name: e.target.value })}
                className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700">Description</label>
              <textarea
                required
                value={newWorkflow.description}
                onChange={(e) => setNewWorkflow({ ...newWorkflow, description: e.target.value })}
                className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
              />
            </div>
            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => setIsCreating(false)}
                className="px-4 py-2 border border-slate-300 shadow-sm text-sm font-medium rounded-md text-slate-700 bg-white hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
              >
                Create
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {workflows.map((workflow) => (
          <div key={workflow.id} className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden flex flex-col">
            <div className="p-6 flex-1">
              <div className="flex justify-between items-start">
                <h3 className="text-lg font-medium text-slate-900 truncate">{workflow.name}</h3>
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  workflow.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                  workflow.status === 'DRAFT' ? 'bg-slate-100 text-slate-800' :
                  'bg-yellow-100 text-yellow-800'
                }`}>
                  {workflow.status}
                </span>
              </div>
              <p className="mt-2 text-sm text-slate-500 line-clamp-2">{workflow.description}</p>
              {workflow.createdAt && (
                <p className="mt-4 text-xs text-slate-400">
                  Created: {format(new Date(workflow.createdAt), 'MMM d, yyyy')}
                </p>
              )}
            </div>
            <div className="bg-slate-50 px-6 py-3 border-t border-slate-200 flex justify-between items-center">
              <button
                onClick={() => handleDelete(workflow.id)}
                className="text-red-600 hover:text-red-900 p-1 rounded-md hover:bg-red-50"
                title="Delete Workflow"
              >
                <Trash2 className="w-4 h-4" />
              </button>
              <Link
                to={`/workflows/${workflow.id}`}
                className="inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-900"
              >
                View Details
                <ArrowRight className="ml-1 w-4 h-4" />
              </Link>
            </div>
          </div>
        ))}
        {workflows.length === 0 && !isCreating && (
          <div className="col-span-full text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
            <p className="text-slate-500">No workflows found. Create one to get started.</p>
          </div>
        )}
      </div>
    </div>
  );
}
