package com.effektif.example.cli.command;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.query.WorkflowInstanceQuery;
import com.effektif.workflow.api.workflowinstance.ActivityInstance;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;
import com.effektif.workflow.impl.WorkflowInstanceStore;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;

import java.io.PrintWriter;
import java.util.List;

/**
 * Returns a list of open tasks for the running workflows.
 */
public class TasksCommand implements CommandImpl {

  @Override
  public void execute(CommandLine command, Configuration configuration, PrintWriter out) {
    final WorkflowInstanceStore instanceStore = configuration.get(WorkflowInstanceStore.class);
    final List<WorkflowInstanceImpl> instances = instanceStore.findWorkflowInstances(new WorkflowInstanceQuery());

    out.println("Open tasks:");
    for (WorkflowInstanceImpl instance : instances) {
      WorkflowInstance workflowInstance = instance.toWorkflowInstance();
      for (ActivityInstance activity : workflowInstance.getActivityInstances()) {
        if (!activity.isEnded()) {
          out.println("  " + activity.getTaskId() + ": " + activity.getActivityId());
        }
      }
    }
    out.println();
  }
}
