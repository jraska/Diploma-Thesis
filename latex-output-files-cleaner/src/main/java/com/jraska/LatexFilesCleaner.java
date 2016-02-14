package com.jraska;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Locale.US;

/**
 * Simple util to delete periodically crap LaTeX files which are causing Typeset to fail.
 */
public class LatexFilesCleaner {
  //region Constants

  public static final String[] SUFFIXES_TO_DELETE = {".aux", ".out"};
  private static final DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.FULL);
  private static final int REQUIRED_ARGS_COUNT = 2;

  //endregion

  //region Main methods

  public static void main(String[] args) throws IOException {
    LatexFilesCleaner latexFilesCleaner = new LatexFilesCleaner();
    latexFilesCleaner.run(args);
  }

  //endregion

  //region Methods

  private void run(String[] args) throws IOException {
    if (args.length != REQUIRED_ARGS_COUNT) {
      String message = String.format(US, "Exactly %d arguments is required", REQUIRED_ARGS_COUNT);
      throw new IllegalArgumentException(message);
    }

    File targetDir = new File(args[0]);
    if (!targetDir.exists()) {
      throw new IllegalArgumentException("Target dir " + targetDir.getAbsolutePath() + " does not exist.");
    }

    int seconds = Integer.parseInt(args[1]);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(new CleanLatexCrap(targetDir, System.out), 0, seconds, TimeUnit.SECONDS);

    int read = System.in.read();
    System.out.println(read);

    executor.shutdownNow();
  }

  //endregion

  //region Nested classes

  static class LatexCrapFilter implements FileFilter {
    @Override public boolean accept(File pathname) {
      if (pathname.isDirectory()) {
        return false;
      }

      String lowerCaseName = pathname.getName().toLowerCase();
      for (String suffix : SUFFIXES_TO_DELETE) {
        if (lowerCaseName.endsWith(suffix)) {
          return true;
        }
      }

      return false;
    }
  }

  static class CleanLatexCrap implements Runnable {
    private final File _targetDir;
    private final PrintStream _out;

    public CleanLatexCrap(File targetDir, PrintStream out) {
      _targetDir = targetDir;
      _out = out;
    }

    @Override
    public void run() {
      _out.print("---");
      _out.print(DATE_FORMAT.format(new Date()));
      _out.print(": ");
      _out.println("FileCleaner check");
      File[] filesToDelete = _targetDir.listFiles(new LatexCrapFilter());

      for (File toDelete : filesToDelete) {
        if (toDelete.delete()) {
          _out.print("File ");
          _out.print(toDelete.getName());
          _out.println(" deleted");
        }
      }
    }
  }

  //endregion
}
