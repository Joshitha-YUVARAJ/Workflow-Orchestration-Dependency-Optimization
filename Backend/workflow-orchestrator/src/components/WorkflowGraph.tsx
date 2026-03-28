import React, { useEffect } from 'react';
import {
  ReactFlow,
  useNodesState,
  useEdgesState,
  MiniMap,
  Controls,
  Background,
  MarkerType,
  Handle,
  Position,
  Panel
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import dagre from 'dagre';
import { Task, TaskDependency, ExecutionOrder } from '../types';

const dagreGraph = new dagre.graphlib.Graph();
dagreGraph.setDefaultEdgeLabel(() => ({}));

const nodeWidth = 220;
const nodeHeight = 90;

const getLayoutedElements = (nodes: any[], edges: any[], direction = 'TB') => {
  dagreGraph.setGraph({ rankdir: direction });

  nodes.forEach((node) => {
    dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
  });

  edges.forEach((edge) => {
    dagreGraph.setEdge(edge.source, edge.target);
  });

  dagre.layout(dagreGraph);

  const newNodes = nodes.map((node) => {
    const nodeWithPosition = dagreGraph.node(node.id);
    return {
      ...node,
      targetPosition: 'top',
      sourcePosition: 'bottom',
      position: {
        x: nodeWithPosition.x - nodeWidth / 2,
        y: nodeWithPosition.y - nodeHeight / 2,
      },
    };
  });

  return { nodes: newNodes, edges };
};

const CustomNode = ({ data }: any) => {
  const hasIndex = data.executionIndex !== undefined;
  return (
    <div className={`px-4 py-3 shadow-md rounded-lg bg-white border-2 min-w-[200px] ${hasIndex ? 'border-indigo-500 ring-2 ring-indigo-200' : 'border-slate-200'}`}>
      <Handle type="target" position={Position.Top} className="w-3 h-3 !bg-indigo-500" />
      <div className="flex items-center mb-1">
        {hasIndex && (
          <div className="rounded-full w-6 h-6 flex items-center justify-center bg-indigo-100 text-indigo-800 text-xs font-bold mr-2 border border-indigo-200 flex-shrink-0">
            {data.executionIndex + 1}
          </div>
        )}
        <div className="font-bold text-sm text-slate-800 truncate">{data.label}</div>
      </div>
      <div className="text-xs text-slate-500 font-medium">
        Type: <span className={data.taskType === 'MANUAL' ? 'text-orange-600' : 'text-blue-600'}>{data.taskType}</span>
      </div>
      <div className="text-xs text-slate-400 mt-1">Duration: {data.duration}ms</div>
      <Handle type="source" position={Position.Bottom} className="w-3 h-3 !bg-indigo-500" />
    </div>
  );
};

const nodeTypes = {
  custom: CustomNode,
};

interface WorkflowGraphProps {
  tasks: Task[];
  dependencies: TaskDependency[];
  executionOrder: ExecutionOrder | null;
}

export default function WorkflowGraph({ tasks, dependencies, executionOrder }: WorkflowGraphProps) {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  useEffect(() => {
    const initialNodes = tasks.map(task => {
      const execIndex = executionOrder?.orderedTaskIds.indexOf(task.id);
      const hasExecOrder = execIndex !== undefined && execIndex !== -1;
      return {
        id: task.id.toString(),
        type: 'custom',
        data: {
          label: task.name,
          taskType: task.taskType,
          duration: task.estimatedDuration,
          executionIndex: hasExecOrder ? execIndex : undefined
        },
        position: { x: 0, y: 0 }
      };
    });

    const initialEdges = dependencies.map(dep => {
      return {
        id: `e${dep.sourceTask}-${dep.targetTask}`,
        source: dep.sourceTask.toString(),
        target: dep.targetTask.toString(),
        animated: executionOrder !== null,
        markerEnd: {
          type: MarkerType.ArrowClosed,
          width: 20,
          height: 20,
          color: executionOrder !== null ? '#6366f1' : '#94a3b8',
        },
        style: {
          strokeWidth: 2,
          stroke: executionOrder !== null ? '#6366f1' : '#94a3b8',
        },
      };
    });

    const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutedElements(
      initialNodes,
      initialEdges
    );

    setNodes(layoutedNodes);
    setEdges(layoutedEdges);
  }, [tasks, dependencies, executionOrder, setNodes, setEdges]);

  if (tasks.length === 0) {
    return (
      <div className="w-full h-[400px] bg-slate-50 rounded-xl border border-slate-200 border-dashed flex items-center justify-center">
        <p className="text-slate-500">No tasks to visualize. Add tasks and dependencies first.</p>
      </div>
    );
  }

  return (
    <div className="w-full h-[500px] bg-slate-50 rounded-xl border border-slate-200 overflow-hidden relative">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        nodeTypes={nodeTypes}
        fitView
        attributionPosition="bottom-right"
      >
        <Controls />
        <MiniMap zoomable pannable nodeClassName={() => 'bg-indigo-500'} />
        <Background color="#cbd5e1" gap={16} />
        {executionOrder && (
          <Panel position="top-right" className="bg-white p-3 rounded-md shadow-md border border-slate-200 m-4">
            <div className="text-sm font-semibold text-slate-800 mb-1">Execution Order Highlighted</div>
            <div className="text-xs text-slate-500">Nodes are numbered in execution sequence.</div>
          </Panel>
        )}
      </ReactFlow>
    </div>
  );
}
