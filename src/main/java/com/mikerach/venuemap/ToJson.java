package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.*;
import javax.json.stream.JsonGenerator;
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
        int size = record.size();

        //id name postcode lat lng approx
        //0  1    2        3   4   5

        String id = "id" + record.get(0).trim();
        String name = record.get(1).trim();

        int postcodeIndex = record.size() - 4;
        int latIndex = record.size() - 3;
        int lngIndex = record.size() - 2;
        int approximationIndex = record.size() - 1;

        String postcode = record.get(postcodeIndex).trim();
        String lat = record.get(latIndex).trim();
        String lng = record.get(lngIndex).trim();
        String approximation = record.get(approximationIndex).trim();

        // check valid values
        Boolean.parseBoolean(approximation);
        Float.parseFloat(lat);
        Float.parseFloat(lng);

        if ("0.0".equals(lat) && "0.0".equals(lng)) {
          //System.out.println(String.format("Skipping %s %s %s", id, name, postcode));
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

    System.out.println("Expect 55 overrides found " + expectedOverrides);
    JsonWriter writer = Json.createWriter(Files.newOutputStream(Paths.get(OUTPUT)));
    writer.writeObject(object.build());
    writer.close();

    String json = new String(Files.readAllBytes(Paths.get(OUTPUT)));
    String prettyPrint = json.replaceAll("\\Q\"id\":\\E", "\n\"id\":");
//    System.out.println(prettyPrint);
    prettyPrint = "mikerach = {}\n" +
        "mikerach.venues =\n" + prettyPrint;
    Files.write(Paths.get(OUTPUT), prettyPrint.getBytes());
  }
}
