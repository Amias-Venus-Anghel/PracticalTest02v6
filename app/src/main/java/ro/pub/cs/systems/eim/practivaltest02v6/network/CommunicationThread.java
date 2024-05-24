package ro.pub.cs.systems.eim.practivaltest02v6.network;


import android.util.Log;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practivaltest02v6.general.Constants;
import ro.pub.cs.systems.eim.practivaltest02v6.general.Utilities;
import ro.pub.cs.systems.eim.practivaltest02v6.model.ValutaModel;

public class CommunicationThread extends Thread{
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client valuta type!");
            String informationType = bufferedReader.readLine();
            if ( informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client valuta!");
                return;
            }
            ValutaModel data = serverThread.getData();
            ValutaModel valutaInfo = null;
            if (data.CanUseCache()) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                valutaInfo = data;
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else
                    Log.i(Constants.TAG, pageSourceCode );

                JSONObject content = new JSONObject(pageSourceCode);

                JSONObject bpiVal = content.getJSONObject("bpi");
                String usdRate = bpiVal.getJSONObject("USD").getString("rate");

                String eur = bpiVal.getJSONObject("EUR").getString("rate");

                String updatedAt = content.getJSONObject("time").getString("updated");

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] updated at: " + updatedAt);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] usd: " + usdRate);
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] eur " + eur);

                valutaInfo = new ValutaModel(updatedAt, eur, usdRate);

                serverThread.setData(valutaInfo);
            }

            if (valutaInfo == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = null;
            switch(informationType) {
                case Constants.EUR:
                    result = valutaInfo.GetEur();
                    break;
                case Constants.USD:
                    result = valutaInfo.GetUsd();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());

        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An jsonException exception has occurred: " + jsonException.getMessage());

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());

                }
            }
        }
    }
}
