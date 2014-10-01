package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.URL;

import static com.mikerach.venuemap.LatLngResolver.LatLng;

/**
 * Created by home on 26/09/14.
 */
public class LatLngLookup {

  private static final int SHARD = 0;
  private static final String INPUT_FILE = "/home/home/projects/venuemap/venues-split-" + SHARD + ".csv";
  private static final String OUTPUT_FILE = "/home/home/projects/venuemap/venues-with-loc-split-" + SHARD + ".csv";

  public static void main(String[] args) throws Exception {
    LatLngResolver latLngResolver = new LatLngResolver();
    PrintWriter out = new PrintWriter(new FileOutputStream(new File(OUTPUT_FILE),
            false /* append = true */));

    Reader in = new FileReader(INPUT_FILE);
    Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

    for (CSVRecord record : records) {
      String id = record.get(0).trim();

      int i = 0;
      if (Integer.parseInt(id) <= 2720) {
        System.out.println("skipping " + id + " " + i++);
        continue;
      }

      String postcode = record.get(6).trim().replaceAll("\\s*", "");
      LatLng latLng = latLngResolver.getLatLng(postcode);
      StringBuilder line = new StringBuilder();
      line.append(id + ", "); // id
      line.append(record.get(2).trim() + ", "); // name
      line.append(postcode + ", "); // postcode
      line.append(latLng.getLat() + ", "); // lat
      line.append(latLng.getLng() + ", "); // lng
      line.append(latLng.isApproximation()); // approximate

      System.out.println(line.toString());
      out.println(line.toString());
      Thread.sleep(1 * 1000);
      out.flush();
    }

    out.close();
  }
}
