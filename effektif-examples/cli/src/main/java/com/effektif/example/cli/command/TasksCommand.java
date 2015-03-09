package com.effektif.example.cli.command;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.api.model.Message;
import com.effektif.workflow.api.query.WorkflowInstanceQuery;
import com.effektif.workflow.api.task.Task;
import com.effektif.workflow.api.task.TaskQuery;
import com.effektif.workflow.api.task.TaskService;
import com.effektif.workflow.api.workflowinstance.ActivityInstance;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;
import com.effektif.workflow.impl.WorkflowInstanceStore;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;

import java.io.PrintWriter;
import java.util.List;

/**
 * Returns a list of open tasks for the running workflows.
 * TODO List the correct tasks
 */
public class TasksCommand implements CommandImpl {

  @Override
  public void execute(CommandLine command, Configuration configuration, PrintWriter out) {
    final TaskService taskService = configuration.getTaskService();
    final WorkflowInstanceStore instanceStore = configuration.get(WorkflowInstanceStore.class);
    final List<WorkflowInstanceImpl> instances = instanceStore.findWorkflowInstances(new WorkflowInstanceQuery());

    out.println("Open tasks:");
    for (WorkflowInstanceImpl instance : instances) {
      WorkflowInstance workflowInstance = instance.toWorkflowInstance();
      for (ActivityInstance activity : workflowInstance.getActivityInstances()) {
        if (!activity.isEnded()) {
          final Task task = taskService.findTaskById(activity.getTaskId());
          out.println("  " + task.getId() + "(" + activity.getTaskId() + "): " + task.getName());
          // TODO Figure out why activity.getTaskId() != task.getId()
        }
      }
    }
    out.println();

    out.println("All tasks:");
    for (Task task : taskService.findTasks(new TaskQuery())) {
      out.println("  " + task.getId() + ": " + task.getName());
    }
    out.println();
  }
}
