package com.example.android_rosbridgeclient;

import android.app.Application;
import com.jilk.ros.rosbridge.ROSBridgeClient;

public class ROSClientApplication extends Application {
    ROSBridgeClient client;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        if(client != null)
            client.disconnect();
        super.onTerminate();
    }

    public ROSBridgeClient getRosClient() {
        return client;
    }

    public void setRosClient(ROSBridgeClient client) {
        this.client = client;
    }
}
