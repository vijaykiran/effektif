package com.effektif.example.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 * Tests the command line application by controlling the intput and output streams.
 */
public class ApplicationTest {

  private final PipedOutputStream inStream = new PipedOutputStream();
  private final PrintWriter inWriter = new PrintWriter(inStream, true);

  private final PipedInputStream outStream = new PipedInputStream();
  private final BufferedReader outReader = new BufferedReader(new InputStreamReader(outStream));

  private Thread application;

  public ApplicationTest() throws IOException {
    final BufferedReader in = new BufferedReader(new InputStreamReader(new PipedInputStream(inStream)));
    final PrintWriter out = new PrintWriter(new PipedOutputStream(outStream), true);
    final Application cli = new Application(in, out);
    application = new Thread(cli);
  }

  @Before
  public void start() {
    application.start();
  }

  @After
  public void stop() throws IOException, InterruptedException {
    if (application == null || !application.isAlive()) {
      return;
    }

    application.interrupt();
//    throw new IllegalStateException("The application is still running.");
  }

  @Test(timeout = 1000)
  public void test() throws IOException {

    // Start-up and deploy
    readLines(Application.WELCOME);

    // List workflows
    execute("workflows");
    readLines(
      "Deployed workflows:",
      "  release",
      "",
      "Running workflows:",
      "");

    // List tasks (none)
    execute("tasks");
    readLines("Open tasks:", "");

    // Start workflow
    execute("start release");

    // List tasks (first task)
    execute("tasks");
    readLines(
      "Open tasks:",
      "  1: Move open issues",
      "");

    // Complete first task
    execute("complete 1");

    // List tasks (first task)
    execute("tasks");
    readLines(
      "Open tasks:",
      "  2: Check continuous integration",
      "");

    execute("quit");
  }

  private void execute(String command) throws IOException {
    read(Application.PROMPT);
    write(command);
  }

  private void read(String expectedOutput) throws IOException {
    int length = expectedOutput.length();
    char[] buffer = new char[length];
    outReader.read(buffer, 0, length);
    assertEquals(String.valueOf(buffer), expectedOutput);
  }

  private void readLines(String... expectedOutput) throws IOException {
    for (String line : expectedOutput) {
      read(line + System.lineSeparator());
    }
  }

  private void write(String input) {
    inWriter.println(input);
  }

}
