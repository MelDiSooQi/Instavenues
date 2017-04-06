package com.instavenues.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instavenues.R;
import com.instavenues.adapter.PlacesAdapter;
import com.instavenues.app.AppController;
import com.instavenues.helper.Connectivity;
import com.instavenues.model.Image;
import com.instavenues.model.Place;
import com.pureix.easylocator.controller.service.LocationAPI;
import com.pureix.easylocator.service.locatonService.Listener.LocationReceiverListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MelDiSooQi on 4/5/2017.
 */

public class HomeActivity extends AppCompatActivity {

    private String TAG = GallaryActivity.class.getSimpleName();
    private static final String endpoint = "https://api.foursquare.com/v2/venues/search";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;

    private List<Place>        list;
    private PlacesAdapter    adapter;
    private RecyclerView recyclerView;

    private LocationAPI locationAPI;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkLocationService();

        locationAPI = new LocationAPI(HomeActivity.this);
        locationAPI.setLocationReceiverListener(new LocationReceiverListener() {
            @Override
            public void getLastKnownLocation(Location location) {
                currentLocation = location;
                Toast.makeText(HomeActivity.this, "Location updated....", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                Toast.makeText(HomeActivity.this, "Location updated....", Toast.LENGTH_SHORT).show();
            }
        });

        // initialize refresh fab button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Action for Refresh fab button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call API to get all contacts
                if(!Connectivity.isConnect(HomeActivity.this))//if not Connected
                {
                    //close loading dialog
                    pDialog.hide();
                    //open internet alert dialog
                    openDialog();
                }else
                {
                    fetchImages();
                }
            }
        });

        pDialog = new ProgressDialog(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<Place>();

        list.clear();

        adapter = new PlacesAdapter(this, list, R.layout.activity_home_place_row);
        recyclerView.setAdapter(adapter);

        if(!Connectivity.isConnect(this))//if not Connected
        {
            //close loading dialog
            pDialog.hide();
            //open internet alert dialog
            openDialog();
        }else
        {
            fetchImages();
        }

    }

    private void fetchImages() {

        pDialog.setMessage("Downloading...");
        pDialog.show();

        String strLocation = null;
        if(currentLocation == null)
        {
            strLocation = "-26.00425,28.11336";
        }else
        {
            strLocation = currentLocation.getLatitude()+","+currentLocation.getLongitude();
        }
        String uri = String.format(endpoint+"?"
                        +"v=%1$s"
                        +"&intent=%2$s"
                        +"&client_id=%3$s"
                        +"&client_secret=%4$s"
                        //+"&query=%5$s"
                        +"&ll=%5$s"
                        //+"&limit=%6$s"
                ,
                "20161016",
                "checkin",
                "WOBAXKHNQZCVKGJHVYDFEU3YRB20H0PAMBHYKR4QAPCCEPZX",
                "P11GQE0CCYCKPAP3MHFLQBP5DSLP2H15FGRMSZHMX4TECIHV",
                //"coffee",
                strLocation//,
                //"3"
        );
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                uri, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        list.clear();
                        try {

                        JSONArray jsonArray = response.getJSONObject("response").getJSONArray("venues");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) jsonArray.get(i);
                                Place place = new Place();
                                place.setID(object.getString("id"));
                                place.setPlace(object.getString("name"));

                                list.add(place);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                        pDialog.hide();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.check_internet_connection))
                .setCancelable(false)
                .setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alert = builder.create();
        // now setup to change color of the button
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#2a64af"));
            }
        });

        try {
            alert.show();
        }catch (Exception e)
        {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationAPI.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationAPI.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        locationAPI.requestPermission(HomeActivity.this);
        locationAPI.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    private void checkLocationService() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            //dialog.show();

            final AlertDialog alert = dialog.create();
            // now setup to change color of the button
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(Color.parseColor("#2a64af"));

                    alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(Color.parseColor("#2a64af"));
                }
            });

            try {
                alert.show();
            }catch (Exception e)
            {}
        }
    }
}