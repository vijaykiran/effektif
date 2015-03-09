package com.effektif.example.cli;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.api.model.Deployment;
import com.effektif.workflow.api.model.Message;
import com.effektif.workflow.api.model.TriggerInstance;
import com.effektif.workflow.api.query.WorkflowInstanceQuery;
import com.effektif.workflow.api.query.WorkflowQuery;
import com.effektif.workflow.api.task.Task;
import com.effektif.workflow.api.task.TaskQuery;
import com.effektif.workflow.api.task.TaskService;
import com.effektif.workflow.api.workflow.Activity;
import com.effektif.workflow.api.workflow.Workflow;
import com.effektif.workflow.api.workflowinstance.ActivityInstance;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;
import com.effektif.workflow.impl.TaskStore;
import com.effektif.workflow.impl.WorkflowInstanceStore;
import com.effektif.workflow.impl.WorkflowStore;
import com.effektif.workflow.impl.job.Job;
import com.effektif.workflow.impl.job.JobQuery;
import com.effektif.workflow.impl.job.JobStore;
import com.effektif.workflow.impl.json.JsonService;
import com.effektif.workflow.impl.memory.MemoryConfiguration;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;

import java.util.List;

/**
 * Facade for the Effektif workflow engine, for use by the application.
 */
public class WorkflowEngineFacade {

  private final Configuration configuration;
  private final WorkflowEngine engine;
  private final TaskService taskService;
  private final WorkflowInstanceStore workflowInstanceStore;

  /**
   * Sets up the engine and deploys workflows.
   */
  public WorkflowEngineFacade() {
    configuration = new MemoryConfiguration();
    engine = configuration.getWorkflowEngine();
    taskService = configuration.getTaskService();
    workflowInstanceStore = configuration.get(WorkflowInstanceStore.class);

    Deployment deployment = engine.deployWorkflow(SoftwareRelease.workflow()).checkNoErrorsAndNoWarnings();
    System.out.println("Deployed workflow " + deployment.getWorkflowId());
  }

  /**
   * Completes a running activity for the given task.
   */
  public void complete(String taskId) {
    for (WorkflowInstanceImpl instance : workflowInstances()) {
      WorkflowInstance workflowInstance = instance.toWorkflowInstance();
      for (ActivityInstance activity : workflowInstance.getActivityInstances()) {
        if (activity.getTaskId().equals(taskId)) {
          engine.send(new Message().
            workflowInstanceId(workflowInstance.getId()).
            activityInstanceId(activity.getId()));
        }
      }
    }
  }

  /**
   * Starts the workflow with the given source ID.
   */
  public WorkflowInstance startWorkflow(String sourceWorkflowId) {
    for (Workflow workflow : workflows()) {
      if (workflow.getSourceWorkflowId().equals(sourceWorkflowId)) {
        final TriggerInstance trigger = new TriggerInstance().workflowId(workflow.getId());
        return engine.start(trigger);
      }
    }
    return null;
  }

  /**
   * Returns a list of active tasks.
   */
  public List<Task> tasks() {
    return taskService.findTasks(new TaskQuery());
  }

  public List<WorkflowInstanceImpl> workflowInstances() {
    return workflowInstanceStore.findWorkflowInstances(new WorkflowInstanceQuery());
  }

  /**
   * Retruns a list of deployed workflows.
   */
  public List<Workflow> workflows() {
    return engine.findWorkflows(new WorkflowQuery());
  }

  protected void log() {

    JsonService jsonService = configuration.get(JsonService.class);
    WorkflowStore workflowStore = configuration.get(WorkflowStore.class);
    WorkflowInstanceStore workflowInstanceStore = configuration.get(WorkflowInstanceStore.class);
    TaskStore taskStore = configuration.get(TaskStore.class);

    StringBuilder cleanLog = new StringBuilder();
    cleanLog.append("Workflow engine contents\n");

    List<Task> tasks = taskStore.findTasks(new TaskQuery());
    if (tasks != null && !tasks.isEmpty()) {
      int i = 0;
      cleanLog.append("\n### tasks ######################################################## \n");
      for (Task task : tasks) {
        cleanLog.append("--- Task ");
        cleanLog.append(i);
        cleanLog.append(" ---\n");
        cleanLog.append(jsonService.objectToJsonStringPretty(task));
        cleanLog.append("\n");
        i++;
      }
    }

    List<WorkflowInstanceImpl> workflowInstances = workflowInstanceStore.findWorkflowInstances(new WorkflowInstanceQuery());
    if (workflowInstances != null && !workflowInstances.isEmpty()) {
      int i = 0;
      cleanLog.append("\n\n### workflowInstances ################################################ \n");
      for (WorkflowInstanceImpl workflowInstance : workflowInstances) {
        cleanLog.append("--- Workflow instance ");
        cleanLog.append(i);
        cleanLog.append(" ---\n");
        cleanLog.append(jsonService.objectToJsonStringPretty(workflowInstance.toWorkflowInstance()));
        cleanLog.append("\n");
        i++;
      }
    }

    List<Workflow> workflows = workflowStore.findWorkflows(new WorkflowQuery());
    if (workflows != null && !workflows.isEmpty()) {
      int i = 0;
      cleanLog.append("\n### workflows ######################################################## \n");
      for (Workflow workflow : workflows) {
        cleanLog.append("--- Deleted workflow ");
        cleanLog.append(i);
        cleanLog.append(" ---\n");
        cleanLog.append(jsonService.objectToJsonStringPretty(workflow));
        cleanLog.append("\n");
        i++;
      }
    }
    System.out.println(cleanLog.toString());
  }}
