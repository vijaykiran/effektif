package com.effektif.example.cli;

import com.effektif.workflow.api.task.Task;
import com.effektif.workflow.api.workflow.Workflow;
import com.effektif.workflow.api.workflowinstance.WorkflowInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * A workflow engine example with a command-line interface.
 * <p/>
 * Based on <a href="https://github.com/codurance/task-list">Task List</a> by Codurance.
 */
public class Application implements Runnable {

  private final BufferedReader in;
  private final PrintWriter out;
  private final WorkflowEngineFacade engine;

  protected final static String PROMPT = "> ";

  public Application(BufferedReader in, PrintWriter out) {
    this.in = in;
    this.out = out;
    engine = new WorkflowEngineFacade();
  }

  /**
   * Starts the application interactively.
   */
  public static void main(String[] args) throws Exception {
    final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    final PrintWriter out = new PrintWriter(System.out);
    new Application(in, out).run();
  }

  /**
   * Runs the application, using the defined input and output.
   */
  @Override
  public void run() {
    out.println("Command line workflow example (enter ‘help’ to list commends).");
    while (true) {
      out.print("> ");
      out.flush();
      String commandLineString = null;
      try {
        commandLineString = in.readLine().trim();
        if (!commandLineString.isEmpty()) {
          final CommandLine commandLine = CommandLine.parse(commandLineString);
          if (commandLine.is(Command.QUIT)) {
            break;
          }
          execute(commandLine);
        }
      } catch (IllegalArgumentException e) {
        unknownCommand(commandLineString);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
//    out.close();
  }


  private void execute(CommandLine commandLine) {
    switch (commandLine.getCommand()) {
      case COMPLETE:
        complete(commandLine.getArguments()[0]);
        break;
      case HELP:
        help();
        break;
      case START:
        start(commandLine.getArguments()[0]);
        break;
      case TASKS:
        listTasks();
        break;
      case WORKFLOWS:
        listWorkflows();
        break;
      default:
        unknownCommand(commandLine.getCommand().toString());
    }
  }


  // COMMANDS

  /**
   * Marks the task with the given ID as complete.
   */
  private void complete(String taskId) {
    engine.complete(taskId);
  }

  private void help() {
    out.println("Commands:");
    out.println("  complete [ID]   Mark the task with the given ID complete");
    out.println("  help            List commands");
    out.println("  start [ID]      Start the workflow with the given ID");
    out.println("  tasks           List outstanding tasks (ID and name)");
    out.println("  workflows       List deployed workflows (ID)");
    out.println("  quit            Exit command line");
    out.println("");
  }

  private void listTasks() {
    out.println("Tasks:");
    for (Task task : engine.tasks()) {
      out.println("  " + task.getId() + ": " + task.getName());
    }
    out.println();
  }

  private void listWorkflows() {
    out.println("Workflows:");
    for (Workflow workflow : engine.workflows()) {
      out.println("  " + workflow.getSourceWorkflowId());
    }
    out.println();
  }

  private void start(String workflowId) {
    WorkflowInstance startedWorkflowInstance = engine.startWorkflow(workflowId);
    if (startedWorkflowInstance == null) {
      out.println("Workflow not found");
    }
    else {
      out.println("Workflow started");
    }
    out.println();
  }

  private void unknownCommand(String commandLine) {
    out.println("Unknown command: " + commandLine);
    out.println();
  }

}
