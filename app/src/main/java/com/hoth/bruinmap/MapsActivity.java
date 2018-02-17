package com.hoth.bruinmap;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.res.Resources;
import android.util.Log;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_raw);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng cameraPos = new LatLng(34.069496, -118.444823);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(cameraPos)
                .zoom(15)
                .tilt(0)
                .bearing(-20)
                .build()));

        mMap.setLatLngBoundsForCameraTarget(UCLA);
        makeCall("", "");
    }

    public void makeCall(String endpoint, String course){
        RequestQueue rq = Volley.newRequestQueue(this);
        String url ="http://api.ucladevx.com/courses/winter/computer/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string
                        try {
                            JSONArray responseArr = new JSONArray(response);
                            for(int i = 0; i < responseArr.length(); i++){
                                JSONObject objects = responseArr.getJSONObject(i);
                                String course = DevXAPIUtility.getCourseNum(objects.getString("course"));
                                System.out.println(course);
                                if(course.equals("32")){
                                    Course current = new Course(objects.getString("subject"), course, DevXAPIUtility.getLocation(objects.getString("locations")));
                                    System.out.println(current.subject);
                                    System.out.println(current.location);
                                    break;
                                }
                            }
                        }catch(JSONException e){
                            throw new RuntimeException(e);
                        }
                        Context context = getApplicationContext();
                        CharSequence text = "Success!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Context context = getApplicationContext();
                CharSequence text = "Error!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
// Add the request to the RequestQueue.
        rq.add(stringRequest);
    }
}

