package com.example.travelguru;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.travelguru.model.Place;

public class PlaceJSONParser {

    ParseResult result;

    public PlaceJSONParser() {
        result = new ParseResult();
        result.status = null;
        result.data = null;
    }

    public ParseResult parse(JSONObject jsonObject) {

        try {
            String status = jsonObject.getString("status");
            result.status = status;
            if (!status.equals("OK"))
                return result;

            JSONArray results = jsonObject.getJSONArray("results");

            ArrayList<Place> placesList = new ArrayList<Place>();

            for (int i = 0; i < results.length(); i++) {
                Place place = new Place();
                String name = results.getJSONObject(i).getString("name");
                String vicinity = results.getJSONObject(i)
                        .getString("vicinity");
                String reference = results.getJSONObject(i).getString(
                        "reference");
                float lat = Float.parseFloat(results.getJSONObject(i)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getString("lat"));
                float lng = Float.parseFloat(results.getJSONObject(i)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getString("lng"));
                float[] geometry = new float[] { lat, lng };

                place.setName(name);
                place.setReference(reference);
                place.setVicinity(vicinity);
                place.setGeometry(geometry);

                placesList.add(place);
            }

            result.data = placesList;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    class ParseResult {
        String status;
        ArrayList<Place> data;
    }
}
