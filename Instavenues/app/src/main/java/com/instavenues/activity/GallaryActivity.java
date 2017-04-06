package com.instavenues.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.instavenues.adapter.GalleryAdapter;
import com.instavenues.app.AppController;
import com.instavenues.helper.Connectivity;
import com.instavenues.model.Image;
import com.instavenues.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GallaryActivity extends AppCompatActivity {

    private String TAG = GallaryActivity.class.getSimpleName();
    private String endpoint = "https://api.foursquare.com/v2/venues/";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    private String placeID;
    private static int reqNum = 0;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        getExtra();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

         recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if(!Connectivity.isConnect(activity))//if not Connected
        {
            //open internet alert dialog
            openDialog();
        }else
        {
            fetchImages();
        }

    }

    private void getExtra() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeID = extras.getString("placeID", null);

            if(placeID != null)
            {
                endpoint = endpoint + placeID +"/photos";
            }
        }
    }

    private void fetchImages() {
        pDialog.setMessage("Downloading...");
        pDialog.show();

        String uri = String.format(endpoint+"?"
                        +"v=%1$s"
                        +"&client_id=%2$s"
                        +"&client_secret=%3$s"
                ,
                "20161016",
                "WOBAXKHNQZCVKGJHVYDFEU3YRB20H0PAMBHYKR4QAPCCEPZX",
                "P11GQE0CCYCKPAP3MHFLQBP5DSLP2H15FGRMSZHMX4TECIHV"
                );
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                uri, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

                            JSONArray jsonArray = response.getJSONObject("response").getJSONObject("photos")
                                    .getJSONArray("items");
                            images.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject object = (JSONObject) jsonArray.get(i);
                                    Image image = new Image();
                                    image.setLarge(object.getString("prefix")+
                                            "540x960"
                                    +object.getString("suffix"));
                                    image.setMedium(object.getString("prefix")+
                                            "200x200"
                                            +object.getString("suffix"));
                                    image.setSmall(("prefix")+
                                            "100x100"
                                            +object.getString("suffix"));
                                    image.setName(
                                            object.getJSONObject("source")
                                                    .getString("name")
                                            +"\n"
                                    +object.getJSONObject("source")
                                            .getString("url")
                                    );

                                    image.setTimestamp(object.getString("createdAt"));

                                    images.add(image);

                                } catch (JSONException e) {
                                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                            if(images.size() ==0)
                            {
                                emptyImagesDialog();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // disable cache
        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        reqNum++;
        AppController.getInstance().addToRequestQueue(jsonObjReq, reqNum+placeID);
    }


    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.check_internet_connection))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
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

    private void emptyImagesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.empty_images))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
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