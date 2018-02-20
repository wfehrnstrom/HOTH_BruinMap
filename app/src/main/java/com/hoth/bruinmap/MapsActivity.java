package com.hoth.bruinmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;


import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

    import static com.google.maps.android.PolyUtil.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    Button submit_btn;
    EditText search_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_raw);
        submit_btn = (Button) findViewById(R.id.button1);
        search_bar = (EditText) findViewById(R.id.searchView1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

       search_bar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit_btn.performClick();
                    return true;
                }
                return false;
            }
        });





    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        LatLngBounds UCLA = new LatLngBounds(
                new LatLng(34.057847, -118.447928), new LatLng(34.078220, -118.436024));
        // First is Southwest corner
        // Second is Northeast Corner
        final LatLng cameraPos = new LatLng(34.069496, -118.444823);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(cameraPos)
                        .zoom(15)
                        .tilt(0)
                        .bearing(-20)
                        .build()));
        mMap.setMinZoomPreference(15);

        mMap.setLatLngBoundsForCameraTarget(UCLA);
        DateTime now = new DateTime();
        String origin = "";
        String destination ="";
        String url = "http://api.ucladevx.com/events/events.json";
        LatLng HOTH4 = new LatLng(34.071909, -118.449619);
        mMap.addMarker(new MarkerOptions().position(HOTH4)
                .title("HOTH4"));


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputDestination = search_bar.getText().toString();
                DirectionsResult results = getDirectionsDetails("Boelter Hall, Los Angeles", inputDestination,TravelMode.WALKING);
                if (results != null) {
                    addPolyline(results, mMap);
                    addMarkersToMap(results, mMap);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                            .target(new LatLng(34.069916, -118.443120))
                            .zoom(19)
                                    .tilt(45)
                            .build()));
                }
                results = null;
            }
        });

    }



    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.directionsApiKey)).setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
    }
    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;    }
    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
    }
    private void addPolyline(DirectionsResult results, GoogleMap mMap) {        List<LatLng> decodedPath = decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }


}



