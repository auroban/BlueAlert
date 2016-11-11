package vertex2016.mvjce.edu.bluealert;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    //To get the default Bluetooth adapter on the Android device
    public BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

    //A request code to identify which activity was executed
    private int REQ_CODE = 1;

    //For defining PRESS BACK BUTTON TWICE
    private int backPressedCount;

    //To check the status of the Search Button
    private boolean on = false;

    //The Search button on the main screen
    private Button searchButton;

    //The View that lists all the nearby Bluetooth devices found
    private ListView listBTDevices;


    //Store the recently found Bluetooth devices & pass them on to the ListView
    private ArrayAdapter BTArrayAdapter;

    //Represents an actual remote Bluetooth Device
    private BluetoothDevice btd;

    //A socket for establishing connection to a remote Bluetooth Device
    private BluetoothSocket bSocket;

    //Container for the TextViews
    private ViewGroup containerVG;


    //Intent Filter to detect the discovery of nearby Bluetooth devices
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Lock the rotation of the screen


        searchButton = (Button) findViewById(R.id.searchButton);
        listBTDevices = (ListView) findViewById(R.id.listBTDevices);
        backPressedCount = 1;


        containerVG = (ViewGroup) findViewById(R.id.containerVG);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!on) {
                    connect();

                } else if (on) {
                    stopDiscovery();
                    on = false;
                    searchButton.setText("Search");
                }

            }
        });

        listBTDevices.setOnItemClickListener(clickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //A method that checks if targeted device supports Bluetoth or not
    //In case it does, execute the SearchBTDevices method to search
    public void connect() {
        //Registering the IntentFilter
        this.registerReceiver(receiver, filter);

        //If the device doesn't have Bluetooth, the Bluetooth Adapter BA returns NULL
        if (BA == null)
            Toast.makeText(MainActivity.this, "System Doesn't Support Bluetooth", Toast.LENGTH_SHORT).show();

            //In case the device has Bluetooth, but Bluetooth isn't enabled
            //Enables the Bluetooth on the device
            //startActivityForResult() takes in the Intent & a REQUEST CODE to specifically identify that intent
        else if (!BA.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQ_CODE);
        }
        //In case Bluetooth is enabled on the device, start the discovery
        else {
            searchBTDevices();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "TURNED ON!", Toast.LENGTH_SHORT).show();
            searchBTDevices();
        } else
            Toast.makeText(MainActivity.this, "FAILED TO ENABLE BLUETOOTH", Toast.LENGTH_LONG).show();
    }


    public void searchBTDevices() {
        //As soon as the search starts, the Welcome screen TextView disappears & ListView appears
        containerVG.setVisibility(View.GONE);
        listBTDevices.setVisibility(View.VISIBLE);


        BTArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);


        //In case the discovery fails to begin
        if (!BA.startDiscovery())
            Toast.makeText(MainActivity.this, "Failed to start discovery", Toast.LENGTH_SHORT).show();

        else {
            Toast.makeText(MainActivity.this, "Discovery Started", Toast.LENGTH_SHORT).show();
            on = true;
            searchButton.setText("Stop Discovery");

        }

        listBTDevices.setAdapter(BTArrayAdapter);


    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); //Get the device details

                BTArrayAdapter.add(new BluetoothInfoContainer(btd.getName(),btd.getAddress(),btd));
            }

        }
    };

    private void stopDiscovery() {
        BA.cancelDiscovery();
        Toast.makeText(MainActivity.this, "Discovery Stopped", Toast.LENGTH_SHORT).show();
        this.unregisterReceiver(receiver);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (BTArrayAdapter != null)
            BTArrayAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BA.cancelDiscovery();
        BA.startDiscovery();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(MainActivity.this, "Discovery Resumed", Toast.LENGTH_SHORT).show();
    }

    public final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

            BluetoothInfoContainer BIC = (BluetoothInfoContainer) BTArrayAdapter.getItem(position);

            new ConnectingThread().execute(BIC.getDevice());

        }
    };

    @Override
    public void onBackPressed() {
        if (backPressedCount == 2) {
            super.onBackPressed();
            return;
        }
        else {
            backPressedCount++;
            Toast.makeText(this,"Press BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    //An AsyncTask to take care of establishing connection to the Bluetooth Device
    //UI Thread is not blocked
    public class ConnectingThread extends AsyncTask<BluetoothDevice, Integer, Boolean> {

        private BluetoothSocket bluetoothSocket;
        private Boolean SUCCESS = false;
        private BluetoothDevice btd;




        @Override
        protected Boolean doInBackground(BluetoothDevice... bluetoothDevices) {

            try {

                btd = bluetoothDevices[0];
                Method m = btd.getClass().getMethod("createRfcommSocket", int.class);
                bluetoothSocket = (BluetoothSocket) m.invoke(btd,1);

                bluetoothSocket.connect();

                if(bluetoothSocket.isConnected())
                    SUCCESS = true;

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return SUCCESS;
        }


        @Override
        protected void onPostExecute(Boolean result) {

            if(result)
            {
                Toast.makeText(MainActivity.this,"Successfully Paired With" + btd.getName(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(MainActivity.this,"Couldn't Pair With" + btd.getName(), Toast.LENGTH_SHORT).show();
        }
    }


    //A Class to contain Bluetooth Info
    private class BluetoothInfoContainer {

        private String name;
        private String address;
        private BluetoothDevice device;

        public BluetoothInfoContainer(String devName, String devAddress, BluetoothDevice dev)
        {
            name = devName;
            address = devAddress;
            device = dev;
        }

        public String getName()
        {
            return name;
        }

        public String getAddress()
        {
            return address;
        }

        public BluetoothDevice getDevice()
        {
            return device;
        }

    }
}
