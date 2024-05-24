package ro.pub.cs.systems.eim.practivaltest02v6.network;


import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practivaltest02v6.general.Constants;
import ro.pub.cs.systems.eim.practivaltest02v6.general.Utilities;

public class ClientThread extends Thread {

    private int port;
    private String informationType;
    private TextView weatherForecastTextView;

    private Socket socket;

    public ClientThread(int port, String informationType, TextView weatherForecastTextView) {
        this.port = port;
        this.informationType = informationType;
        this.weatherForecastTextView = weatherForecastTextView;
    }

    @Override
    public void run() {
        try {
            Log.e(Constants.TAG, "[CLIENT THREAD] run!");
            socket = new Socket(Constants.ADDRESS, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(informationType);
            printWriter.flush();
            String weatherInformation;
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;
                weatherForecastTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherForecastTextView.setText(finalizedWeateherInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());

                }
            }
        }
    }
}
