package com.ahmed.guessduel;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends Activity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream inputStream;

    TextView statusText;

    // same UUID for both devices
    private final UUID APP_UUID =
            UUID.fromString("12345678-1234-1234-1234-123456789abc");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button enableBtn = findViewById(R.id.enableBtn);
        Button connectBtn = findViewById(R.id.connectBtn);
        Button sendBtn = findViewById(R.id.sendBtn);
        statusText = findViewById(R.id.statusText);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Enable Bluetooth
        enableBtn.setOnClickListener(v -> {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(i);
            }
        });

        // Connect to first paired device
        connectBtn.setOnClickListener(v -> connectDevice());

        // Send test message
        sendBtn.setOnClickListener(v -> sendMessage("HELLO"));

        // Listen for incoming messages
        new Thread(this::listenForMessages).start();
    }

    private void connectDevice() {
        try {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

            if (devices.size() == 0) {
                statusText.setText("No paired devices!");
                return;
            }

            BluetoothDevice device = devices.iterator().next(); // pick first

            socket = device.createRfcommSocketToServiceRecord(APP_UUID);
            socket.connect();

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            statusText.setText("Connected to " + device.getName());

        } catch (Exception e) {
            statusText.setText("Connection failed");
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {
        try {
            if (outputStream != null) {
                outputStream.write(msg.getBytes());
                statusText.setText("Sent: " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        byte[] buffer = new byte[1024];

        while (true) {
            try {
                if (inputStream != null) {
                    int bytes = inputStream.read(buffer);

                    String msg = new String(buffer, 0, bytes);

                    runOnUiThread(() ->
                            statusText.setText("Received: " + msg)
                    );
                }
            } catch (Exception ignored) {}
        }
    }
}
