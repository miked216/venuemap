package com.mikerach.venuemap;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 26/09/14.
 */
public class Splitter {

  private static int FILE_COUNT = 3;

  public static void main(String[] args) throws Exception {
    List<String> rows =
        Files.readAllLines(Paths.get("/home/home/projects/venuemap/venues-original.csv"), Charset.defaultCharset());

    List<PrintWriter> writers = new ArrayList<>();

    for (int i = 0; i < FILE_COUNT; i++) {
      writers.add(new PrintWriter("/home/home/projects/venuemap/venues-split-" + i + ".csv", "UTF-8"));
    }

    int i = 0;

    for (String row : rows) {
      int file = i++ % FILE_COUNT;

      String[] fields = row.split(",");
      System.out.println("Row " + fields[2].trim() + " " + fields[6]);
      writers.get(file).println(row);

    }

    for (PrintWriter writer : writers) {
      writer.close();
    }
  }
}
