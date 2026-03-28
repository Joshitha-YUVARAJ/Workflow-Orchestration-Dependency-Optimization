export interface AuthResponse {
  token: string;
}

export interface Workflow {
  id: number;
  name: string;
  description: string;
  status: 'DRAFT' | 'ACTIVE' | 'ARCHIVED';
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: number;
  name: string;
  description: string;
  estimatedDuration: number;
  taskType: 'MANUAL' | 'AUTOMATED';
  outgoingDependencyIds: number[];
  incomingDependencyIds: number[];
}

export interface TaskDependency {
  id: number;
  sourceTask: number;
  targetTask: number;
}

export interface WorkflowRunSummary {
  workflowRunId: number;
  workflowId: number;
  status: 'RUNNING' | 'COMPLETED' | 'WAITING' | 'FAILED';
  startedAt: string;
  endedAt: string;
}

export interface TaskRun {
  taskRunId: number;
  workflowRunId: number;
  taskId: number;
  taskName: string;
  status: 'PENDING' | 'READY' | 'RUNNING' | 'WAITING_FOR_USER' | 'COMPLETED' | 'FAILED';
  startedAt: string;
  endedAt: string;
}

export interface ExecutionOrder {
  workflowId: number;
  message: string;
  orderedTaskIds: number[];
}

export interface WorkflowValidation {
  valid: boolean;
  message: string;
}
