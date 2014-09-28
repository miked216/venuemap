package com.mikerach.venuemap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.URL;

/**
 * Created by home on 26/09/14.
 */
public class LatLngLookup {

  private static int SHARD = 1;
  private static String FILE = "/home/home/projects/venuemap/venues-split-" + SHARD + ".csv";

  public static void main(String[] args) throws Exception {

    PrintWriter out = new PrintWriter("/home/home/projects/venuemap/venues-with-loc-split-" + SHARD + ".csv");

    Reader in = new FileReader(FILE);
    Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

    for (CSVRecord record : records) {

      String postcode = record.get(6).trim().replaceAll("\\s*", "");
      LatLng latLng = getLatLng(postcode);

      StringBuilder line = new StringBuilder();
      line.append(record.get(0).trim() + ", "); // id
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

  private static LatLng getLatLng(String postcode) throws IOException {
    JsonArray results = getResponse(postcode);
    JsonObject location;
    boolean approximation = false;
    try {
      location = results.getJsonObject(0).getJsonObject("geometry").getJsonObject("location");

    } catch (Exception e) {
      System.out.println("ERROR processing " + postcode);
      String generalPostcode = postcode.substring(0, postcode.length() - 3);
      results = getResponse(generalPostcode);
      try {
        location = results.getJsonObject(0).getJsonObject("geometry").getJsonObject("location");
        approximation = true;
      } catch (Exception ex) {
        System.out.println("ERROR processing " + generalPostcode);
        System.out.println("Bad response " + results);
        return new LatLng(0, 0, approximation);
      }
    }
    double lat = location.getJsonNumber("lat").doubleValue();
    double lng = location.getJsonNumber("lng").doubleValue();
    return new LatLng(lat, lng, approximation);
    }

  private static JsonArray getResponse(String postcode) throws IOException {
    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?output=json?key=AIzaSyDh8logL7iuHXbrZbppM_NgZFmN5G29gnk&&components=postal_code:" + postcode);
    try (InputStream is = url.openStream();
         JsonReader rdr = Json.createReader(is)) {
      JsonObject obj = rdr.readObject();
      return obj.getJsonArray("results");
    }
  }

  private static class LatLng {
    private final double lat;
    private final double lng;
    private final boolean approximation;

    private LatLng(double lat, double lng, boolean approximation) {
      this.lat = lat;
      this.lng = lng;
      this.approximation = approximation;
    }

    public double getLat() {
      return lat;
    }

    public double getLng() {
      return lng;
    }

    public boolean isApproximation() {
      return approximation;
    }
  }
}
