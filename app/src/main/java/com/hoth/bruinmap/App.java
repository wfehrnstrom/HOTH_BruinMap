package com.hoth.bruinmap;

import android.app.Application;
import android.content.Intent;

/**
 * Created by siddharth on 17-02-2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, LastLocationCheck.class));
    }
}
