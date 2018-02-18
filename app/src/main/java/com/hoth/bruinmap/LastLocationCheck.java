package com.hoth.bruinmap;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by siddharth on 17-02-2018.
 */

public class LastLocationCheck extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("hey");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }
}