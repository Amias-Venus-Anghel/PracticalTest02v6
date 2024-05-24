package ro.pub.cs.systems.eim.practivaltest02v6.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practivaltest02v6.R;
import ro.pub.cs.systems.eim.practivaltest02v6.general.Constants;
import ro.pub.cs.systems.eim.practivaltest02v6.network.ClientThread;
import ro.pub.cs.systems.eim.practivaltest02v6.network.ServerThread;

public class PracticalTest02v6MainActivity extends AppCompatActivity {
    TextView serverResponseView;
    EditText server_port, client_port, client_option, client_city;
    Button connect_to_server, get_weather;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v6_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        serverResponseView = (TextView) findViewById(R.id.response);

        connect_to_server = (Button) findViewById(R.id.server_connect);
        connect_to_server.setOnClickListener(new ConnectButtonListener());

        get_weather = (Button) findViewById(R.id.get_info);
        get_weather.setOnClickListener(new GetWeatherListener());

        server_port = (EditText) findViewById(R.id.server_port);
        client_port = (EditText) findViewById(R.id.client_port);
        client_option = (EditText) findViewById(R.id.option);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

    private class ConnectButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = server_port.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private class GetWeatherListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String clientPort = client_port.getText().toString();
            if (clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String informationType = client_option.getText().toString();

            if (informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            serverResponseView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    Integer.parseInt(clientPort), informationType, serverResponseView
            );
            clientThread.start();
        }
    }
}