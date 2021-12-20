package com.example.android_rosbridgeclient;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android_rosbridgeclient.databinding.ActivityMainBinding;
import com.jilk.ros.ROSClient;
import com.jilk.ros.Service;
import com.jilk.ros.rosapi.message.Empty;
import com.jilk.ros.rosapi.message.GetTime;
import com.jilk.ros.rosapi.message.MessageDetails;
import com.jilk.ros.rosapi.message.Topics;
import com.jilk.ros.rosapi.message.Type;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.implementation.JSON;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.util.Arrays;

public class MainActivity extends Activity {
    private ROSBridgeClient client;
    private ActivityMainBinding binding;

    private EditText indexInput;

    private String serverName;
    private int waypointCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        indexInput = (EditText)findViewById(R.id.indexInput);

        // ip for using the emulator
        //String ip = "10.0.2.2";
        // ip for hardware
        // https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
        String ip = "192.168.0.139";

        String port = "9090";
        connect(ip, port);
        client.setDebug(true);

        // Test checking what's being run right now
        System.out.println("Node List");
        System.out.println(Arrays.toString(getNodes()));
        System.out.println("Topic List");
        System.out.println(Arrays.toString(getTopics()));

        // Check ROSBridgeWebSocketClient OnMessage method for details
        // Run: rostopic pub /listener ss/String "Hebbo, SMEEbLER"
        // You can change the message and the new published message will be printed, ignore the error for now
        // This is a formatting issue with the strictly typed reception, but the communications themselves are fine
        serverName = "/chatter";
        String addReq = "{\"op\":\"subscribe\",\"topic\":\"" + serverName + "\"}";
        client.send(addReq);

        waypointCount = 0;
    }

    public void addWayPoint(View view) {
        String topicName = "/addWayPoint";
        String index = indexInput.getText().toString();
        String data = "{\"data\":\"" + index + "\"}";

        String message = "{\"op\":\"publish\",\"topic\":\"" + topicName + "\", \"msg\":" + data + "}";
        //System.out.println(message);

        client.send(message);
    }

    public void deleteWayPoint(View view) {
        String topicName = "/deleteWayPoint";

        String index = indexInput.getText().toString();
        String data = "{\"data\":\"" + index + "\"}";

        String message = "{\"op\":\"publish\",\"topic\":\"" + topicName + "\", \"msg\":" + data + "}";
        //System.out.println(message);

        client.send(message);
    }

    public void executeWaypoints(View view) {
        String topicName = "/executeCallback";
        String data = "Execute";
        publish(topicName, data);
    }

    public void publish(String topic, String data) {
        String dataObject = "{\"data\":\"" + data + "\"}";
        String message = "{\"op\":\"publish\",\"topic\":\"" + topic + "\", \"msg\":" + dataObject + "}";
        //System.out.println(message);

        client.send(message);
    }

    private String[] getNodes() {
        String[] nodes = null;
        try {
            nodes = client.getNodes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nodes;
    }

    public void printNodes(View view) {
        String[] nodes = null;
        try {
            nodes = client.getNodes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(nodes.toString());
    }

    private String[] getServices() {
        String[] services = null;
        try {
            services = client.getServices();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return services;
    }

    private String[] getTopics() {
        String[] topics = null;
        try {
            topics = client.getTopics();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return topics;
    }

    private void connect(String ip, String port) {
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        //client = new ROSBridgeClient("http://" + ip + ":" + port);

        boolean connectSuccess = client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(true);
                ((ROSClientApplication)getApplication()).setRosClient(client);
                Log.d(TAG,"Connect ROS success");
                //startActivity(new Intent(MainActivity.this,NodesActivity.class));
            }

            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                Log.d(TAG,"ROS disconnect");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                Log.d(TAG,"ROS communication error");
            }
        });

        client.setDebug(true);
    }
}