package com.mikerach.venuemap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Resolves a postcode into a latlng
 *
 * Created by home on 30/09/14.
 */
public class LatLngResolver {

  public LatLng getLatLng(String postcode) throws IOException {
    if (postcode.length() < 3) {
      return new LatLng(0, 0, true);
    }
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
    //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?output=json?key=AIzaSyDh8logL7iuHXbrZbppM_NgZFmN5G29gnk&&components=postal_code:" + postcode);
    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?output=json?key=AIzaSyDh8logL7iuHXbrZbppM_NgZFmN5G29gnk&components=postal_code:" + postcode);
    try (InputStream is = url.openStream();
         JsonReader rdr = Json.createReader(is)) {
      JsonObject obj = rdr.readObject();
      return obj.getJsonArray("results");
    }
  }

  static class LatLng {
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
