package com.smartcity.smartrescue.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.smartcity.smartrescue.R;
import com.smartcity.smartrescue.http.HttpClient;
import com.smartcity.smartrescue.service.RequestCommand;
import com.smartcity.smartrescue.vehicule.Vehicule;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.smartcity.smartrescue.settings.SettingsActivity.SERVER_IP;

public class RequestActivity extends AppCompatActivity {
    private Thread thread;
    private String serverEndpoint;
    private String address;

    @BindView(R.id.addressTv)
    TextView addressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_request);
        ButterKnife.bind(this);

        String address = getIntent().getStringExtra(RequestCommand.EXTRA_ADDRESS);
        this.address = address;
        address += Vehicule.getInstance().getVehiculeStatus();
        Timber.d(address);
        addressTv.setText(address);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String serverIP = sp.getString(SERVER_IP, "");
        serverEndpoint = "http://"+ serverIP + "/android";
    }

    @OnClick(R.id.acceptBtn)
    public void acceptClick() {
        Toast.makeText(this, "Accept click", Toast.LENGTH_SHORT).show();

        while (!checkConnection()) {
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(2000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        if (checkConnection()) {
            new OKAnswer(serverEndpoint).execute();
        }
    }

    @OnClick(R.id.denyBtn)
    public void denyClick() {
        Toast.makeText(this, "Deny click", Toast.LENGTH_SHORT).show();

        while (!checkConnection()) {
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(2000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        if (checkConnection()) {
            new KOAnswer(serverEndpoint).execute();
            finish();
        }
    }

    private class OKAnswer extends AsyncTask<Boolean, Boolean, Boolean> {
        private String serverEndpoint;

        OKAnswer(String serverEndpoint) {
            this.serverEndpoint = serverEndpoint;
        }


        @Override
        protected Boolean doInBackground(Boolean... params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Integer idEmergency = Vehicule.getInstance().getIdEmergency();
            Timber.d(String.valueOf(idEmergency));

            boolean response = HttpClient.answerEmergency(serverEndpoint, idEmergency, token, "OK");
            if (response) {
                Vehicule.getInstance().changeVehiculeStatus(com.smartcity.smartrescue.vehicule.Status.ENROUTE);

                Uri intentUri = Uri.parse("google.navigation:q="+address);
                Intent i = new Intent(Intent.ACTION_VIEW, intentUri);
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                finish();
            }
        }
    }

    private class KOAnswer extends AsyncTask {
        private String serverEndpoint;

        KOAnswer(String serverEndpoint) {
            this.serverEndpoint = serverEndpoint;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Integer idEmergency = Vehicule.getInstance().getIdEmergency();

            return HttpClient.answerEmergency(serverEndpoint, idEmergency, token, "KO");
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
