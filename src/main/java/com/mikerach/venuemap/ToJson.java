package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.*;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by home on 28/09/14.
 */
public class ToJson {

  public static final String OUTPUT = "/home/home/projects/venuemap/venues-json.js";

  public static void main(String[] args) throws Exception {

    List<String> inputFiles = new ArrayList<>();
    inputFiles.add("/home/home/projects/venuemap/venues-with-loc-split-0.csv");
    inputFiles.add("/home/home/projects/venuemap/venues-with-loc-split-1.csv");
    inputFiles.add("/home/home/projects/venuemap/venues-with-loc-split-2.csv");
    inputFiles.add("/home/home/projects/venuemap/venues-loc-overrides.csv");

    JsonObjectBuilder object = Json.createObjectBuilder();
    int expectedOverrides = 0;

    for (String inputFile : inputFiles) {
      Reader in = new FileReader(inputFile);
      Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

      for (CSVRecord record : records) {
        String id = "id" + record.get(0).trim();
        String name = record.get(1).trim();
        String postcode = record.get(2).trim();

        String lat = record.get(3).trim();
        String lng = record.get(4).trim();

        //TODO remove this once shard 0 has been re-run
        String approximation = record.size() == 6 ? record.get(5).trim() : "false";

        if ("0.0".equals(lat) && "0.0".equals(lng)) {
          System.out.println(String.format("Skipping %s %s %s", id, name, postcode));
          expectedOverrides++;
          continue;
        }
        object.add(id, Json.createObjectBuilder()
            .add("id", id)
            .add("name", name)
            .add("postcode", postcode)
            .add("location", Json.createObjectBuilder()
                .add("lat", lat)
                .add("lng", lng))
            .add("approximation", approximation));
      }
    }

    JsonWriter writer = Json.createWriter(Files.newOutputStream(Paths.get(OUTPUT)));
    writer.writeObject(object.build());
    writer.close();
    System.out.println("Overrides " + expectedOverrides);
  }
}
