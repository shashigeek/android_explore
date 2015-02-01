package com.example.travelguru;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelguru.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationDisplayActivity extends Activity implements
        OnMarkerClickListener, OnInfoWindowClickListener, OnMapClickListener {

    private GoogleMap googleMap;
    private ArrayList<Place> placeList;

    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_display);
        try {

            if (getIntent() != null && getIntent().hasExtra("placeList")) {
                placeList = getIntent()
                        .getParcelableArrayListExtra("placeList");
            }
            // load map
            initilizeMap();
            // mPlayer = MediaPlayer.create(getApplicationContext(),
            // R.raw.audio_review);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("NewApi")
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            } else {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setZoomControlsEnabled(false);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        addMarkersToMap(placeList);
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMapClickListener(this);
    }

    private void addMarkersToMap(ArrayList<Place> placeList) {

        int i = 0;
        for (Place place : placeList) {
            String name = place.getName();
            String vicinity = place.getVicinity();
            float[] geometry = place.getGeometry();

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(geometry[0], geometry[1]))
                    .title(name)
                    .snippet(vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(i * 360
                            / placeList.size()));
            googleMap.addMarker(marker);

            if (i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(geometry[0], geometry[1])).zoom(15)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
            i++;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null)
            mPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null)
            mPlayer.stop();
        mPlayer = null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mPlayer != null && mPlayer.isPlaying())
            mPlayer.stop();
        mPlayer = MediaPlayer.create(getApplicationContext(),
                R.raw.audio_review);
        mPlayer.start();
        return false;
    }

    class CustomInfoWindowAdapter implements InfoWindowAdapter {

        // These a both viewgroups containing an ImageView with id "badge" and
        // two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        // private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                        titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0,
                        10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12,
                        snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    @Override
    public void onMapClick(LatLng arg0) {
        if (mPlayer != null)
            mPlayer.stop();
    }

}
