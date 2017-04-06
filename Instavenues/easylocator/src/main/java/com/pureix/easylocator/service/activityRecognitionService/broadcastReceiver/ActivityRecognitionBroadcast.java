package com.pureix.easylocator.service.activityRecognitionService.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.pureix.easylocator.controller.service.ActivityRecognitionAPI;
import com.pureix.easylocator.model.ObservableHandler;
import com.pureix.easylocator.service.activityRecognitionService.Constants;
import com.pureix.easylocator.service.activityRecognitionService.listener.ActivityRecognitionListener;

import java.util.ArrayList;

public class ActivityRecognitionBroadcast extends BroadcastReceiver
{
    protected static final String TAG = "activity-detection-response-receiver";

    public static ObservableHandler activitiesRecognitionObservable = new ObservableHandler();

//    private ArrayList<DetectedActivity> mDetectedActivities;

    public ActivityRecognitionBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ArrayList<DetectedActivity> updatedActivities =
                intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

//        mDetectedActivities = new ArrayList<DetectedActivity>();
//
//        // Set the confidence level of each monitored activity to zero.
//        for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
//            mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
//        }

        activitiesRecognitionObservable.setChange(updatedActivities);
    }
}
