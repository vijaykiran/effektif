package com.effektif.example.cli.command;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.WorkflowEngine;
import com.effektif.workflow.api.query.WorkflowQuery;
import com.effektif.workflow.api.workflow.Workflow;

import java.io.PrintWriter;

/**
 * Created by pedro on 09-03-2015.
 */
public class WorkflowsCommand implements CommandImpl {

  @Override
  public void execute(CommandLine command, Configuration configuration, PrintWriter out) {
    out.println("Workflows:");
    final WorkflowEngine engine = configuration.getWorkflowEngine();
    for (Workflow workflow : engine.findWorkflows(new WorkflowQuery())) {
      out.println("  " + workflow.getSourceWorkflowId());
    }
    out.println();
  }
}
