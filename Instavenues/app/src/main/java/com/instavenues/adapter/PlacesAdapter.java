package com.instavenues.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.instavenues.R;
import com.instavenues.activity.GallaryActivity;
import com.instavenues.model.Place;

import java.util.List;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.baseViewHolder>
{
    private final Activity      activity;
    private final List<Place>   list;
    private final int           rowLayout;

    private RecyclerView.Adapter    adapter;

    public PlacesAdapter(Activity activity, List<Place> list, int rowLayout)
    {
        this.activity       = activity;
        this.list           = list;
        this.rowLayout      = rowLayout;

        adapter                 = this;
    }

    public static class baseViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout row;

        TextView placeName;

        public baseViewHolder(View v)
        {
            super(v);
            row         = (LinearLayout) v.findViewById(R.id.row);

            placeName   = (TextView)     v.findViewById(R.id.placeName);
         }

        public void bind(final Activity activity, final RecyclerView.Adapter adapter,
                         final List<Place> list, Object rowObject,
                         final int position)
        {
            final Place place = (Place) rowObject;

            placeName.setText(place.getPlace());

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, GallaryActivity.class);
                    intent.putExtra("placeID", place.getID());
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public baseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new baseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.baseViewHolder holder, int position)
    {
        final Object rowObject = list.get(position);
        holder.bind(activity, adapter, list, rowObject, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
