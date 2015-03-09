package com.effektif.example.cli.command;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.task.Task;
import com.effektif.workflow.api.task.TaskQuery;
import com.effektif.workflow.api.task.TaskService;

import java.io.PrintWriter;

/**
 * Created by pedro on 09-03-2015.
 */
public class TasksCommand implements CommandImpl {

  @Override
  public void execute(CommandLine command, Configuration configuration, PrintWriter out) {
    out.println("Tasks:");
    final TaskService taskService = configuration.getTaskService();
    for (Task task : taskService.findTasks(new TaskQuery())) {
      out.println("  " + task.getId() + ": " + task.getName());
    }
    out.println();
  }
}
