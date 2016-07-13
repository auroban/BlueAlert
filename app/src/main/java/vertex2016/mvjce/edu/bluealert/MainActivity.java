package vertex2016.mvjce.edu.bluealert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.UUID;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    //To get the default Bluetooth adapter on the Android device
    public BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

    //A request code to identify which activity was executed
    private int REQ_CODE = 1;

    private int backPressedCount;

    private boolean on = false;

    //The Search button on the main screen
    private Button searchButton;

    //The View that lists all the nearby Bluetooth devices found
    private ListView listBTDevices;


    private UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //Store the recently found Bluetooth devices & pass them on to the ListView
    private ArrayAdapter BTArrayAdapter;

    //A variable that points to the actual Bluetooth on the device
    private BluetoothDevice btd;
    private BluetoothSocket bSocket;

    //Container for the TextViews
    private ViewGroup containerVG;

    //UUID to specify the services it can provide


    //Intent Filter to detect the discovery of nearby Bluetooth devices
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Lock the rotation of the screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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


        //Setting the onItemClick for selecting a Bluetooth device to connect to


    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); //Get the device details

                BTArrayAdapter.add(btd.getName() + "\t\t" + btd.getAddress());
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
            /*
            Intent connectedBT = new Intent(MainActivity.this, Connected.class);
            connectedBT.putExtra("Bluetooth Device", btd);
            startActivity(connectedBT);
            */

            new ConnectingThread(btd).run();
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

    public class ConnectingThread extends Thread{

        private final BluetoothDevice bluetoothDevice;
        private final BluetoothSocket bluetoothSocket;

        boolean ConnectionFlag = false;


        public ConnectingThread(BluetoothDevice btd)
        {
            BluetoothSocket temp = null;
            bluetoothDevice = btd;

            try {
                temp = btd.createInsecureRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            bluetoothSocket = temp;

        }

        public void run()
        {
            BA.cancelDiscovery();

            try {
                Toast.makeText(MainActivity.this,"REACHED BEFORE SOCKET CONNECTION", Toast.LENGTH_SHORT).show();
                System.out.println("#####THIS IS BEFORE SOCKET CONNECT#####");
                bluetoothSocket.connect();
                System.out.println("#####THIS IS AFTER SOCKET CONNECT#####");
                Toast.makeText(MainActivity.this,"REACHED AFTER SOCKET CONNECTION", Toast.LENGTH_SHORT).show();
                ConnectionFlag = true;

            } catch (IOException e) {

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();}
                return;
            }

            if(ConnectionFlag)
                Toast.makeText(MainActivity.this, "Connected to" + bluetoothDevice.getName(),Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Failed to connect",Toast.LENGTH_SHORT).show();


        }



    }
}
