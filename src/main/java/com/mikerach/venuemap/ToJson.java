package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.*;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by home on 28/09/14.
 */
public class ToJson {

  private static final int SHARD = 0;
  public static final String INPUT = "/home/home/projects/venuemap/venues-with-loc-split-" + SHARD + ".csv";
  public static final String OUTPUT = "/home/home/projects/venuemap/venues-json.js";

  public static void main(String[] args) throws Exception {

    JsonObjectBuilder object = Json.createObjectBuilder();

    Reader in = new FileReader(INPUT);
    Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

    for (CSVRecord record : records) {
      String id = "id" + record.get(0);
      object.add(id, Json.createObjectBuilder()
          .add("id", id)
          .add("name", record.get(1))
          .add("postcode", record.get(2))
          .add("location", Json.createObjectBuilder()
              .add("lat", record.get(3))
              .add("lng", record.get(4))));
    }

    JsonWriter writer = Json.createWriter(Files.newOutputStream(Paths.get(OUTPUT)));
    writer.writeObject(object.build());
    writer.close();
  }
}
