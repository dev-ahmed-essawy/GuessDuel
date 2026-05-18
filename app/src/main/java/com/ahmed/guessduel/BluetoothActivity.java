package com.ahmed.guessduel;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends Activity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    BluetoothServerSocket serverSocket;

    OutputStream outputStream;
    InputStream inputStream;

    TextView statusText;

    private final UUID APP_UUID =
            UUID.fromString("12345678-1234-1234-1234-123456789abc");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button enableBtn = findViewById(R.id.enableBtn);
        Button serverBtn = findViewById(R.id.connectBtn); // now SERVER
        Button clientBtn = findViewById(R.id.sendBtn);    // now CLIENT
        statusText = findViewById(R.id.statusText);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkPermissions();

        enableBtn.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        });

        // ✅ SERVER BUTTON
        serverBtn.setText("Start Server");
        serverBtn.setOnClickListener(v -> startServer());

        // ✅ CLIENT BUTTON
        clientBtn.setText("Connect to Device");
        clientBtn.setOnClickListener(v -> connectClient());

        new Thread(this::listenForMessages).start();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                    },
                    1
            );
        }
    }

    // ✅ SERVER MODE
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("GuessDuel", APP_UUID);

                runOnUiThread(() -> statusText.setText("Waiting for connection..."));

                socket = serverSocket.accept();

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                runOnUiThread(() -> statusText.setText("Client connected ✅"));

            } catch (Exception e) {
                runOnUiThread(() -> statusText.setText("Server error"));
                e.printStackTrace();
            }
        }).start();
    }

    // ✅ CLIENT MODE
    private void connectClient() {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;

                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

                if (devices.size() == 0) {
                    runOnUiThread(() -> statusText.setText("No paired device"));
                    return;
                }

                BluetoothDevice device = devices.iterator().next();

                socket = device.createRfcommSocketToServiceRecord(APP_UUID);
                socket.connect();

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                runOnUiThread(() -> statusText.setText("Connected to " + device.getName()));

            } catch (Exception e) {
                runOnUiThread(() -> statusText.setText("Connection failed"));
                e.printStackTrace();
            }
        }).start();
    }

    // ✅ SEND TEST
    private void sendMessage(String msg) {
        new Thread(() -> {
            try {
                if (outputStream != null) {
                    outputStream.write(msg.getBytes());

                    runOnUiThread(() -> statusText.setText("Sent: " + msg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ✅ LISTEN
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
