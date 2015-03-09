package com.effektif.example.cli.command;

import com.effektif.workflow.api.Configuration;

import java.io.PrintWriter;

/**
 * Created by pedro on 09-03-2015.
 */
public class QuitCommand implements CommandImpl {

  @Override
  public void execute(CommandLine command, Configuration configuration, PrintWriter out) {
    out.println("Goodbye!");
  }
}
