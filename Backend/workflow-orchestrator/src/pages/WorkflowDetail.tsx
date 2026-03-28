import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { apiClient } from '../api/client';
import { Workflow, Task, TaskDependency, WorkflowRunSummary, ExecutionOrder, WorkflowValidation, TaskRun } from '../types';
import { Play, CheckCircle, AlertCircle, Clock, Plus, Trash2, ArrowRight } from 'lucide-react';
import { format } from 'date-fns';
import WorkflowGraph from '../components/WorkflowGraph';

export default function WorkflowDetail() {
  const { id } = useParams<{ id: string }>();
  const [workflow, setWorkflow] = useState<Workflow | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [dependencies, setDependencies] = useState<TaskDependency[]>([]);
  const [runs, setRuns] = useState<WorkflowRunSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [validation, setValidation] = useState<WorkflowValidation | null>(null);
  const [executionOrder, setExecutionOrder] = useState<ExecutionOrder | null>(null);

  // Modals state
  const [isAddingTask, setIsAddingTask] = useState(false);
  const [newTask, setNewTask] = useState({ name: '', description: '', estimatedDuration: 0, taskType: 'AUTOMATED' });
  
  const [isAddingDep, setIsAddingDep] = useState(false);
  const [newDep, setNewDep] = useState({ sourceTaskId: '', targetTaskId: '' });

  const [activeRunId, setActiveRunId] = useState<number | null>(null);
  const [taskRuns, setTaskRuns] = useState<TaskRun[]>([]);

  const fetchAll = async () => {
    try {
      const [wfRes, tasksRes, depsRes, runsRes] = await Promise.all([
        apiClient.get<Workflow>(`/workflows/${id}`),
        apiClient.get<Task[]>(`/workflows/${id}/tasks`),
        apiClient.get<TaskDependency[]>(`/workflows/${id}/dependencies`),
        apiClient.get<WorkflowRunSummary[]>(`/workflow-runs/${id}/runs`),
      ]);
      setWorkflow(wfRes.data);
      setTasks(tasksRes.data);
      setDependencies(depsRes.data);
      setRuns(runsRes.data);
    } catch (error) {
      console.error('Failed to fetch data', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, [id]);

  const handleAddTask = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post(`/workflows/${id}/tasks`, newTask);
      setIsAddingTask(false);
      setNewTask({ name: '', description: '', estimatedDuration: 0, taskType: 'AUTOMATED' });
      fetchAll();
    } catch (error) {
      console.error('Failed to add task', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (!confirm('Delete this task?')) return;
    try {
      await apiClient.delete(`/workflows/${id}/tasks/${taskId}`);
      fetchAll();
    } catch (error) {
      console.error('Failed to delete task', error);
    }
  };

  const handleAddDependency = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient.post(`/workflows/${id}/dependencies`, {
        sourceTaskId: Number(newDep.sourceTaskId),
        targetTaskId: Number(newDep.targetTaskId),
      });
      setIsAddingDep(false);
      setNewDep({ sourceTaskId: '', targetTaskId: '' });
      fetchAll();
    } catch (error) {
      console.error('Failed to add dependency', error);
    }
  };

  const handleDeleteDependency = async (depId: number) => {
    if (!confirm('Delete this dependency?')) return;
    try {
      await apiClient.delete(`/workflows/${id}/dependencies/${depId}`);
      fetchAll();
    } catch (error) {
      console.error('Failed to delete dependency', error);
    }
  };

  const validateWorkflow = async () => {
    try {
      const { data } = await apiClient.get<WorkflowValidation>(`/workflows/${id}/validate`);
      setValidation(data);
      if (data.valid) {
        const orderRes = await apiClient.get<ExecutionOrder>(`/workflows/${id}/execution-order`);
        setExecutionOrder(orderRes.data);
      }
    } catch (error) {
      console.error('Failed to validate', error);
    }
  };

  const executeWorkflow = async () => {
    try {
      await apiClient.post(`/workflows/${id}/execute`);
      fetchAll();
      alert('Workflow execution started!');
    } catch (error: any) {
      alert('Failed to execute: ' + (error.response?.data?.message || error.message));
    }
  };

  const fetchTaskRuns = async (runId: number) => {
    try {
      const { data } = await apiClient.get<TaskRun[]>(`/workflow-runs/${runId}/task-runs`);
      setTaskRuns(data);
      setActiveRunId(runId);
    } catch (error) {
      console.error('Failed to fetch task runs', error);
    }
  };

  const completeManualTask = async (taskRunId: number) => {
    try {
      await apiClient.patch(`/task-runs/${taskRunId}/complete`);
      if (activeRunId) fetchTaskRuns(activeRunId);
      fetchAll();
    } catch (error) {
      console.error('Failed to complete manual task', error);
    }
  };

  if (loading) return <div className="p-8 text-center text-slate-500">Loading details...</div>;
  if (!workflow) return <div className="p-8 text-center text-red-500">Workflow not found.</div>;

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-2xl font-bold text-slate-900">{workflow.name}</h1>
            <p className="mt-2 text-slate-600">{workflow.description}</p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={validateWorkflow}
              className="inline-flex items-center px-4 py-2 border border-slate-300 shadow-sm text-sm font-medium rounded-md text-slate-700 bg-white hover:bg-slate-50"
            >
              <CheckCircle className="w-4 h-4 mr-2 text-green-500" />
              Validate
            </button>
            <button
              onClick={executeWorkflow}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <Play className="w-4 h-4 mr-2" />
              Execute
            </button>
          </div>
        </div>

        {validation && (
          <div className={`mt-4 p-4 rounded-md ${validation.valid ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>
            <div className="flex">
              {validation.valid ? <CheckCircle className="h-5 w-5 mr-2" /> : <AlertCircle className="h-5 w-5 mr-2" />}
              <span>{validation.message}</span>
            </div>
            {validation.valid && executionOrder && (
              <div className="mt-2 text-sm">
                <strong>Execution Order:</strong> {executionOrder.orderedTaskIds.map(id => tasks.find(t => t.id === id)?.name || id).join(' → ')}
              </div>
            )}
          </div>
        )}
      </div>

      {/* Workflow Graph Section */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-slate-900">Workflow Visualization</h2>
        <WorkflowGraph tasks={tasks} dependencies={dependencies} executionOrder={executionOrder} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Tasks Section */}
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-bold text-slate-900">Tasks</h2>
            <button
              onClick={() => setIsAddingTask(true)}
              className="inline-flex items-center px-3 py-1.5 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200"
            >
              <Plus className="w-4 h-4 mr-1" /> Add Task
            </button>
          </div>

          {isAddingTask && (
            <form onSubmit={handleAddTask} className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 space-y-3">
              <input
                type="text" required placeholder="Task Name"
                value={newTask.name} onChange={e => setNewTask({...newTask, name: e.target.value})}
                className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
              />
              <input
                type="text" required placeholder="Description"
                value={newTask.description} onChange={e => setNewTask({...newTask, description: e.target.value})}
                className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
              />
              <div className="flex space-x-3">
                <input
                  type="number" placeholder="Duration (ms)"
                  value={newTask.estimatedDuration} onChange={e => setNewTask({...newTask, estimatedDuration: Number(e.target.value)})}
                  className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                />
                <select
                  value={newTask.taskType} onChange={e => setNewTask({...newTask, taskType: e.target.value})}
                  className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                >
                  <option value="AUTOMATED">Automated</option>
                  <option value="MANUAL">Manual</option>
                </select>
              </div>
              <div className="flex justify-end space-x-2">
                <button type="button" onClick={() => setIsAddingTask(false)} className="px-3 py-1.5 text-sm text-slate-600 hover:text-slate-900">Cancel</button>
                <button type="submit" className="px-3 py-1.5 bg-indigo-600 text-white rounded-md text-sm hover:bg-indigo-700">Save</button>
              </div>
            </form>
          )}

          <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <ul className="divide-y divide-slate-200">
              {tasks.map(task => (
                <li key={task.id} className="p-4 flex justify-between items-center hover:bg-slate-50">
                  <div>
                    <div className="flex items-center">
                      <span className="font-medium text-slate-900 mr-2">{task.name}</span>
                      <span className={`px-2 py-0.5 rounded text-xs font-medium ${task.taskType === 'MANUAL' ? 'bg-orange-100 text-orange-800' : 'bg-blue-100 text-blue-800'}`}>
                        {task.taskType}
                      </span>
                    </div>
                    <p className="text-sm text-slate-500 mt-1">{task.description}</p>
                    <p className="text-xs text-slate-400 mt-1">ID: {task.id} | Duration: {task.estimatedDuration}ms</p>
                  </div>
                  <button onClick={() => handleDeleteTask(task.id)} className="text-red-500 hover:text-red-700 p-2">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </li>
              ))}
              {tasks.length === 0 && <li className="p-4 text-center text-slate-500">No tasks added yet.</li>}
            </ul>
          </div>
        </div>

        {/* Dependencies Section */}
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-bold text-slate-900">Dependencies</h2>
            <button
              onClick={() => setIsAddingDep(true)}
              className="inline-flex items-center px-3 py-1.5 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200"
            >
              <Plus className="w-4 h-4 mr-1" /> Add Dependency
            </button>
          </div>

          {isAddingDep && (
            <form onSubmit={handleAddDependency} className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 space-y-3">
              <div className="flex space-x-3 items-center">
                <select
                  required value={newDep.sourceTaskId} onChange={e => setNewDep({...newDep, sourceTaskId: e.target.value})}
                  className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                >
                  <option value="">Select Source Task</option>
                  {tasks.map(t => <option key={t.id} value={t.id}>{t.name} (ID: {t.id})</option>)}
                </select>
                <ArrowRight className="w-5 h-5 text-slate-400 flex-shrink-0" />
                <select
                  required value={newDep.targetTaskId} onChange={e => setNewDep({...newDep, targetTaskId: e.target.value})}
                  className="block w-full rounded-md border-slate-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                >
                  <option value="">Select Target Task</option>
                  {tasks.map(t => <option key={t.id} value={t.id}>{t.name} (ID: {t.id})</option>)}
                </select>
              </div>
              <div className="flex justify-end space-x-2">
                <button type="button" onClick={() => setIsAddingDep(false)} className="px-3 py-1.5 text-sm text-slate-600 hover:text-slate-900">Cancel</button>
                <button type="submit" className="px-3 py-1.5 bg-indigo-600 text-white rounded-md text-sm hover:bg-indigo-700">Save</button>
              </div>
            </form>
          )}

          <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <ul className="divide-y divide-slate-200">
              {dependencies.map(dep => {
                const source = tasks.find(t => t.id === dep.sourceTask);
                const target = tasks.find(t => t.id === dep.targetTask);
                return (
                  <li key={dep.id} className="p-4 flex justify-between items-center hover:bg-slate-50">
                    <div className="flex items-center space-x-3">
                      <span className="font-medium text-slate-700">{source?.name || dep.sourceTask}</span>
                      <ArrowRight className="w-4 h-4 text-slate-400" />
                      <span className="font-medium text-slate-700">{target?.name || dep.targetTask}</span>
                    </div>
                    <button onClick={() => handleDeleteDependency(dep.id)} className="text-red-500 hover:text-red-700 p-2">
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </li>
                );
              })}
              {dependencies.length === 0 && <li className="p-4 text-center text-slate-500">No dependencies added yet.</li>}
            </ul>
          </div>
        </div>
      </div>

      {/* Workflow Runs Section */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-slate-900">Execution History</h2>
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <ul className="divide-y divide-slate-200">
            {runs.map(run => (
              <li key={run.workflowRunId} className="p-4 hover:bg-slate-50">
                <div className="flex justify-between items-center cursor-pointer" onClick={() => fetchTaskRuns(run.workflowRunId)}>
                  <div>
                    <div className="flex items-center">
                      <span className="font-medium text-slate-900 mr-3">Run #{run.workflowRunId}</span>
                      <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        run.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        run.status === 'FAILED' ? 'bg-red-100 text-red-800' :
                        run.status === 'WAITING' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-blue-100 text-blue-800'
                      }`}>
                        {run.status}
                      </span>
                    </div>
                    <div className="text-sm text-slate-500 mt-1 flex items-center">
                      <Clock className="w-4 h-4 mr-1" />
                      Started: {run.startedAt ? format(new Date(run.startedAt), 'PPpp') : 'N/A'}
                    </div>
                  </div>
                  <button className="text-indigo-600 hover:text-indigo-900 text-sm font-medium">
                    {activeRunId === run.workflowRunId ? 'Hide Details' : 'View Details'}
                  </button>
                </div>
                
                {activeRunId === run.workflowRunId && (
                  <div className="mt-4 pl-4 border-l-2 border-indigo-200 space-y-3">
                    <h4 className="text-sm font-semibold text-slate-700 uppercase tracking-wider">Task Executions</h4>
                    {taskRuns.length === 0 ? (
                      <p className="text-sm text-slate-500">No task runs found.</p>
                    ) : (
                      <div className="grid gap-3">
                        {taskRuns.map(tr => (
                          <div key={tr.taskRunId} className="bg-slate-50 p-3 rounded border border-slate-200 flex justify-between items-center">
                            <div>
                              <span className="font-medium text-slate-800">{tr.taskName}</span>
                              <span className="ml-2 text-xs text-slate-500">ID: {tr.taskId}</span>
                              <div className="text-xs text-slate-500 mt-1">
                                Status: <span className={`font-semibold ${
                                  tr.status === 'COMPLETED' ? 'text-green-600' :
                                  tr.status === 'FAILED' ? 'text-red-600' :
                                  tr.status === 'WAITING_FOR_USER' ? 'text-orange-600' :
                                  'text-blue-600'
                                }`}>{tr.status}</span>
                              </div>
                            </div>
                            {tr.status === 'WAITING_FOR_USER' && (
                              <button
                                onClick={() => completeManualTask(tr.taskRunId)}
                                className="px-3 py-1.5 bg-orange-500 text-white text-sm font-medium rounded hover:bg-orange-600"
                              >
                                Complete Manual Task
                              </button>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </li>
            ))}
            {runs.length === 0 && <li className="p-4 text-center text-slate-500">No execution history.</li>}
          </ul>
        </div>
      </div>
    </div>
  );
}
