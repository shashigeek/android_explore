package com.example.travelguru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.travelguru.model.Place;
import com.example.travelguru.utils.DialogManager;
import com.example.travelguru.utils.NetworkUtil;
import com.example.travelguru.utils.TextUtils;

public class MainActivity extends Activity {

    private String[] mCategories;

    private String[] mCategoriesType;

    private TextView mTitleTxtView;

    LocationTracker locationTracker;

    ProgressDialog progressDialog;

    DialogManager dm = new DialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTitleTxtView = (TextView) findViewById(R.id.titleTxtView);
        TextUtils.setColor(mTitleTxtView, mTitleTxtView.getText().toString(),
                mTitleTxtView.getText().toString().substring(6), Color.BLUE);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        Resources res = getResources();
        mCategories = res.getStringArray(R.array.destination_category);
        mCategoriesType = res.getStringArray(R.array.destination_category_type);

        final String API_KEY = res.getString(R.string.api_key);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.destination_category,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        locationTracker = new LocationTracker(this);
        final boolean canGetLocation = locationTracker.canGetLocation();
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                if (position != 0) {
                    // Toast.makeText(
                    // parent.getContext(),
                    // "Selected Category : \n"
                    // + parent.getItemAtPosition(position)
                    // .toString(), Toast.LENGTH_SHORT)
                    // .show();

                    boolean isConnected = NetworkUtil
                            .isConnectedToNetwork(MainActivity.this);
                    if (!isConnected) {
                        dm.showAlertDialog(
                                MainActivity.this,
                                "Internet Connection Error",
                                "Please connect to working Internet connection",
                                false);
                        return;
                    }

                    if (canGetLocation) {
                        Log.d("Your Location",
                                "latitude:" + locationTracker.getLatitude()
                                        + ", longitude: "
                                        + locationTracker.getLongitude());
                    } else {
                        dm.showAlertDialog(
                                MainActivity.this,
                                "GPS Status",
                                "Couldn't get location information. Please enable GPS",
                                false);
                        return;
                    }
                    String loactionCatType = mCategoriesType[position];
                    StringBuilder sb = new StringBuilder(
                            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + locationTracker.getLatitude() + ","
                            + locationTracker.getLongitude());
                    sb.append("&radius=5000");
                    sb.append("&types=" + loactionCatType);
                    sb.append("&sensor=true");
                    sb.append("&key=" + API_KEY);

                    new PlacesLoaderTask().execute(sb.toString());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private class PlacesLoaderTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(Html.fromHtml("Loading Places..."));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... query) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            InputStream inputStream = null;
            try {
                response = httpclient.execute(new HttpGet(query[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    inputStream = response.getEntity().getContent();
                    if (inputStream != null)
                        responseString = convertInputStreamToString(inputStream);
                    Log.d("TravelAdvisor", responseString);
                } else {
                    // Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("ERROR", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Void, PlaceJSONParser.ParseResult> {

        @Override
        protected PlaceJSONParser.ParseResult doInBackground(
                String... jsonString) {

            PlaceJSONParser parser = new PlaceJSONParser();

            PlaceJSONParser.ParseResult result = null;
            try {

                JSONObject jsonData = new JSONObject(jsonString[0]);
                result = parser.parse(jsonData);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(PlaceJSONParser.ParseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            String status = result.status;
            if (status.equals("OK")) {
                ArrayList<Place> placesList = result.data;

                Intent i = new Intent(getApplicationContext(),
                        LocationDisplayActivity.class);
                i.putParcelableArrayListExtra("placeList", placesList);

                startActivity(i);

            } else if (status.equals("ZERO_RESULTS")) {
                dm.showAlertDialog(
                        MainActivity.this,
                        "Near Places",
                        "Sorry no places found. Try to change the types of places",
                        false);
            } else if (status.equals("UNKNOWN_ERROR")) {
                dm.showAlertDialog(MainActivity.this, "Places Error",
                        "Sorry unknown error occured.", false);
            } else if (status.equals("OVER_QUERY_LIMIT")) {
                dm.showAlertDialog(MainActivity.this, "Places Error",
                        "Sorry query limit to google places is reached", false);
            } else if (status.equals("REQUEST_DENIED")) {
                dm.showAlertDialog(MainActivity.this, "Places Error",
                        "Sorry error occured. Request is denied", false);
            } else if (status.equals("INVALID_REQUEST")) {
                dm.showAlertDialog(MainActivity.this, "Places Error",
                        "Sorry error occured. Invalid Request", false);
            } else {
                dm.showAlertDialog(MainActivity.this, "Places Error",
                        "Sorry error occured.", false);
            }

        }
    }

}
