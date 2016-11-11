package com.auro.app.bluealert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.UUID;

public class ConnectedBTDevice extends AppCompatActivity {

    public BluetoothDevice btd;
    public BluetoothSocket btSocket, tempSocket;
    public UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") ;
    ArrayAdapter arr;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_btdevice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


        arr  = new ArrayAdapter(this, android.R.layout.simple_list_item_2);

        btd = getIntent().getParcelableExtra("BluetoothDevice");

        connectBT();
        displayStuff();

    }

    public void connectBT() {
        Thread myThread = new Thread() {

            public void run() {
                tempSocket = null;
                btSocket = null;

                try {
                    tempSocket = btd.createRfcommSocketToServiceRecord(myUUID);
                    System.out.println(btd.getName());

                    btSocket = tempSocket;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                try {
                    btSocket.connect();
                    System.out.println("Connected65495334343");
                    arr.add("CONNECTED TO-->" + btd.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                       btSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }


            }

        };
        myThread.start();
    }




    public void displayStuff()
    {

        lv = (ListView)findViewById(R.id.connectedBTlistView);
        System.out.println("HIBIJIBIHIBIJIBI");
        lv.setAdapter(arr);
    }

}
