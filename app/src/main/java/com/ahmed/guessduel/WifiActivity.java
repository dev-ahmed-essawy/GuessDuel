package com.ahmed.guessduel;

import android.app.Activity;
import java.io.*;
import android.os.Bundle;
import java.net.*;
import android.widget.*;
import java.util.Collections;

public class WifiActivity extends Activity {

    ServerSocket serverSocket;
    Socket socket;

    PrintWriter writer;
    BufferedReader reader;

    TextView statusText;
    EditText ipInput;

    boolean running = true;

    final int PORT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        Button hostBtn = findViewById(R.id.hostBtn);
        Button joinBtn = findViewById(R.id.joinBtn);
        Button sendBtn = findViewById(R.id.sendBtn);

        statusText = findViewById(R.id.statusText);
        ipInput = findViewById(R.id.ipInput);

        // ✅ HOST GAME
        hostBtn.setOnClickListener(v -> startServer());

        // ✅ JOIN GAME
        joinBtn.setOnClickListener(v -> connectToHost());

        // ✅ SEND TEST MESSAGE
        sendBtn.setOnClickListener(v -> sendMessage("HELLO"));
    }

    // ✅ START SERVER
    private void startServer() {

        new Thread(() -> {

            try {

                String hostIP = getLocalIpAddress();

                runOnUiThread(() ->
                        statusText.setText(
                                "Host IP: " + hostIP +
                                "\nWaiting for player..."
                        )
                );

                serverSocket = new ServerSocket(PORT);

                socket = serverSocket.accept();

                setupStreams();

                // ✅ START LISTENING AFTER CONNECTION
                new Thread(this::listenForMessages).start();

                runOnUiThread(() ->
                        statusText.setText("Player connected ✅")
                );

            } catch (Exception e) {

                runOnUiThread(() ->
                        statusText.setText("Server failed")
                );

                e.printStackTrace();
            }

        }).start();
    }

    // ✅ CONNECT TO HOST
    private void connectToHost() {

        new Thread(() -> {

            try {

                String ip = ipInput.getText().toString().trim();

                if (ip.isEmpty()) {

                    runOnUiThread(() ->
                            statusText.setText("Enter host IP!")
                    );

                    return;
                }

                socket = new Socket(ip, PORT);

                setupStreams();

                // ✅ START LISTENING AFTER CONNECTION
                new Thread(this::listenForMessages).start();

                runOnUiThread(() ->
                        statusText.setText("Connected ✅")
                );

            } catch (Exception e) {

                runOnUiThread(() ->
                        statusText.setText("Connection failed")
                );

                e.printStackTrace();
            }

        }).start();
    }

    // ✅ SETUP STREAMS
    private void setupStreams() throws Exception {

        writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()),
                true
        );

        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
    }

    // ✅ SEND MESSAGE
    private void sendMessage(String msg) {

        new Thread(() -> {

            try {

                if (writer != null) {

                    writer.println(msg);

                    runOnUiThread(() ->
                            statusText.setText("Sent: " + msg)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // ✅ RECEIVE MESSAGES
    private void listenForMessages() {

        while (running) {

            try {

                if (reader != null) {

                    String msg = reader.readLine();

                    if (msg != null) {

                        runOnUiThread(() ->
                                statusText.setText("Received: " + msg)
                        );
                    }
                }

            } catch (Exception ignored) {}
        }
    }

    // ✅ GET LOCAL WIFI/HOTSPOT IP
    private String getLocalIpAddress() {

        try {

            for (NetworkInterface networkInterface :
                    Collections.list(NetworkInterface.getNetworkInterfaces())) {

                for (InetAddress address :
                        Collections.list(networkInterface.getInetAddresses())) {

                    if (!address.isLoopbackAddress()
                            && address instanceof Inet4Address) {

                        String ip = address.getHostAddress();

                        // ✅ Prefer local hotspot/WiFi IP
                        if (ip.startsWith("192.168")) {
                            return ip;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unavailable";
    }

    // ✅ CLEANUP
    @Override
    protected void onDestroy() {
        super.onDestroy();

        running = false;

        try {

            if (reader != null)
                reader.close();

            if (writer != null)
                writer.close();

            if (socket != null)
                socket.close();

            if (serverSocket != null)
                serverSocket.close();

        } catch (Exception ignored) {}
    }
}
