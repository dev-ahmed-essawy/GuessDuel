package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import java.io.*;
import java.net.*;

public class WifiActivity extends Activity {

    ServerSocket serverSocket;
    Socket socket;

    PrintWriter writer;
    BufferedReader reader;

    TextView statusText;
    EditText ipInput;

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

        // ✅ HOST
        hostBtn.setOnClickListener(v -> startServer());

        // ✅ JOIN
        joinBtn.setOnClickListener(v -> connectToHost());

        // ✅ SEND
        sendBtn.setOnClickListener(v -> sendMessage("HELLO"));

        // ✅ LISTEN
        new Thread(this::listenForMessages).start();
    }

    // ✅ START SERVER
    private void startServer() {

        new Thread(() -> {
            try {

                serverSocket = new ServerSocket(PORT);

                runOnUiThread(() ->
                        statusText.setText("Waiting for player...")
                );

                socket = serverSocket.accept();

                setupStreams();

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

    // ✅ CONNECT CLIENT
    private void connectToHost() {

        new Thread(() -> {

            try {

                String ip = ipInput.getText().toString().trim();

                socket = new Socket(ip, PORT);

                setupStreams();

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

    // ✅ STREAMS
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

    // ✅ RECEIVE MESSAGE
    private void listenForMessages() {

        while (true) {

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
}
