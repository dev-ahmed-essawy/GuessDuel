package com.ahmed.guessduel;

import java.io.*;
import java.net.*;

public class NetworkManager {

    public static Socket socket;

    public static PrintWriter writer;
    public static BufferedReader reader;

    // ✅ SETUP STREAMS
    public static void setupStreams() throws Exception {

        writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()),
                true
        );

        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
    }

    // ✅ SEND MESSAGE
    public static void send(String msg) {

        try {

            if (writer != null) {
                writer.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ RECEIVE MESSAGE
    public static String receive() {

        try {

            if (reader != null) {
                return reader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
