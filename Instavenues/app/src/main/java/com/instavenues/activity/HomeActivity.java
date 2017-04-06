package com.instavenues.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                "-26.00425,28.11336"//,
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
}