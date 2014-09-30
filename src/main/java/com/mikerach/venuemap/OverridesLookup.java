package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;

/**
 * Created by home on 30/09/14.
 */
public class OverridesLookup {

  private static final String INPUT_FILE = "/home/home/projects/venuemap/bad-locations.csv";
  private static final String OUTPUT_FILE = "/home/home/projects/venuemap/venues-loc-overrides.csv";

  public static void main(String[] args) throws Exception {
    LatLngResolver latLngResolver = new LatLngResolver();
    PrintWriter out = new PrintWriter(new FileOutputStream(new File(OUTPUT_FILE),
        true /* append = true */));

    Reader in = new FileReader(INPUT_FILE);
    Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

    for (CSVRecord record : records) {
      String id = record.get(0).trim();
      String postcode = record.get(1).trim().replaceAll("\\s*", "");
      LatLngResolver.LatLng latLng = latLngResolver.getLatLng(postcode);

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