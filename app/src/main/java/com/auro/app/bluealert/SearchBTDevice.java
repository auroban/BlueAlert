package com.auro.app.bluealert;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.UUID;

public class SearchBTDevice extends AppCompatActivity {

    public BluetoothAdapter BlueAdapter = BluetoothAdapter.getDefaultAdapter();
    public ArrayAdapter PairedArrayAdapter;
    public ArrayAdapter BTArrayAdapter;
    BluetoothDevice btd;
    public UUID uuID;

    public ListView devicesFound;


    private final BroadcastReceiver BTReceiver= new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                uuID = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);

                BTArrayAdapter.add(btd.getName() + "\t" + btd.getAddress() + "\n" + uuID);

            }
        }

    };

    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);




    @Override
    protected void onResume() {
        super.onResume();
        BlueAdapter.cancelDiscovery();
        BlueAdapter.startDiscovery();
        BTArrayAdapter.clear();
        this.registerReceiver(BTReceiver, filter1);



    }

    @Override
    protected void onPause() {
        super.onPause();
        BlueAdapter.cancelDiscovery();
        this.unregisterReceiver(BTReceiver);
        Toast.makeText(SearchBTDevice.this, "Discovery Stopped!!", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_btdevice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);




        searchBTDevices();


    }


    public void searchBTDevices()
    {
        if(!BlueAdapter.startDiscovery())
            Toast.makeText(SearchBTDevice.this, "Failed to Start Discovery", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(SearchBTDevice.this, "Discovery Startred", Toast.LENGTH_SHORT).show();
        BTArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        devicesFound = (ListView)findViewById(R.id.searchpagelistView);
        devicesFound.setAdapter(BTArrayAdapter);
        devicesFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent connectedBTintent = new Intent(SearchBTDevice.this, ConnectedBTDevice.class);
                connectedBTintent.putExtra("BluetoothDevice", btd);
                startActivity(connectedBTintent);
            }
        });

    }


}

