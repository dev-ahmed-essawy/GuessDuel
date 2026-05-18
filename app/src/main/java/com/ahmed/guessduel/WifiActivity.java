package com.ahmed.guessduel;package comimport android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import java.net.*;
import java.util.Collections;

public class WifiActivity extends Activity {

    private ServerSocket serverSocket;

    private TextView statusText;
    private EditText ipInput;

    private static final int PORT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        Button hostBtn = findViewById(R.id.hostBtn);
        Button joinBtn = findViewById(R.id.joinBtn);

        statusText = findViewById(R.id.statusText);
        ipInput = findViewById(R.id.ipInput);

        // ✅ HOST GAME
        hostBtn.setOnClickListener(v -> startServer());

        // ✅ JOIN GAME
        joinBtn.setOnClickListener(v -> connectToHost());
    }

    // ✅ HOST
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

                // ✅ CREATE SERVER
                serverSocket = new ServerSocket(PORT);

                // ✅ WAIT FOR CLIENT
                NetworkManager.socket =
                        serverSocket.accept();

                // ✅ SETUP STREAMS
                NetworkManager.setupStreams();

                // ✅ MULTIPLAYER SESSION
                GameSession.isHost = true;
                GameSession.isMultiplayer = true;

                runOnUiThread(() -> {

                    statusText.setText(
                            "Player connected ✅"
                    );

                    // ✅ HOST SETS SECRET
                    Intent i = new Intent(
                            WifiActivity.this,
                            SetupActivity.class
                    );

                    startActivity(i);
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        statusText.setText(
                                "Hosting failed"
                        )
                );

                e.printStackTrace();
            }

        }).start();
    }

    // ✅ CLIENT
    private void connectToHost() {

        new Thread(() -> {

            try {

                String ip = ipInput
                        .getText()
                        .toString()
                        .trim();

                if (ip.isEmpty()) {

                    runOnUiThread(() ->
                            statusText.setText(
                                    "Enter Host IP"
                            )
                    );

                    return;
                }

                // ✅ CONNECT TO HOST
                NetworkManager.socket =
                        new Socket(ip, PORT);

                // ✅ SETUP STREAMS
                NetworkManager.setupStreams();

                // ✅ MULTIPLAYER SESSION
                GameSession.isHost = false;
                GameSession.isMultiplayer = true;

                runOnUiThread(() -> {

                    statusText.setText(
                            "Connected ✅"
                    );

                    // ✅ CLIENT GOES TO GAME
                    Intent i = new Intent(
                            WifiActivity.this,
                            GameActivity.class
                    );

                    startActivity(i);
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        statusText.setText(
                                "Connection failed"
                        )
                );

                e.printStackTrace();
            }

        }).start();
    }

    // ✅ GET LOCAL IP
    private String getLocalIpAddress() {

        try {

            for (NetworkInterface networkInterface :
                    Collections.list(
                            NetworkInterface.getNetworkInterfaces())) {

                for (InetAddress address :
                        Collections.list(
                                networkInterface.getInetAddresses())) {

                    if (!address.isLoopbackAddress()
                            && address instanceof Inet4Address) {

                        return address.getHostAddress();
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

        try {

            if (serverSocket != null)
                serverSocket.close();

        } catch (Exception ignored) {}
    }
}
