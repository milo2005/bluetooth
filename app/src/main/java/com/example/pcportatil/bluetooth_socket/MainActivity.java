package com.example.pcportatil.bluetooth_socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pcportatil.bluetooth_socket.DeviceListActvity;
import com.example.pcportatil.bluetooth_socket.R;

public class MainActivity extends Activity {

    Button btnOn, btnOff, apagar;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        apagar = (Button) findViewById(R.id.buttonOf);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Apagar el LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("2");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Encender el LED", Toast.LENGTH_SHORT).show();
            }
        });




        apagar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("3");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Encender el LED", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActvity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        Log.i("Gina", "adress : " + address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            /*try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }*/
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {


        if (btAdapter.isEnabled()) {
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }


    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmOutStream = tmpOut;
        }

        //write methode

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}

