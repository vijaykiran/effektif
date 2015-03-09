package com.effektif.example.cli;

/**
 * Created by pedro on 09-03-2015.
 */
public class CommandLine {

  private final Command command;
  private final String[] arguments;

  public CommandLine(Command command, String... arguments) {
    this.command = command;
    this.arguments = arguments;
  }

  public String[] getArguments() {
    return arguments;
  }

  public Command getCommand() {
    return command;
  }

  public boolean is(Command comparison) {
    return comparison != null && comparison.equals(command);
  }

  public static CommandLine parse(String commandLine) {
    final String[] commandAndArguments = commandLine.trim().split("\\s+", 2);
    final Command parsedCommand = Command.valueOf(commandAndArguments[0].toUpperCase());
    if (commandAndArguments.length > 1) {
      final String[] arguments = commandAndArguments[1].split("\\s+");
      return new CommandLine(parsedCommand, arguments);
    }
    else {
      return new CommandLine(parsedCommand);
    }
  }
}
