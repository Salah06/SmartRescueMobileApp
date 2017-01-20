package com.smartcity.smartrescue.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.smartcity.smartrescue.vehicule.Status;
import com.smartcity.smartrescue.vehicule.Vehicule;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.smartcity.smartrescue.settings.SettingsActivity.VEHICULE_ID_KEY;

public class RequestActivity extends AppCompatActivity {

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
        Timber.d(address);
        addressTv.setText(address);
    }

    @OnClick(R.id.acceptBtn)
    public void acceptClick() {
        new OKAnswer().execute();
        Vehicule.getInstance().changeVehiculeStatus(Status.ENROUTE);

        Uri intentUri = Uri.parse("google.navigation:q="+address);
        Intent i = new Intent(Intent.ACTION_VIEW, intentUri);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.google.android.apps.maps");
        startActivity(i);
    }

    @OnClick(R.id.denyBtn)
    public void denyClick() {
        new KOAnswer().execute();
        finish();
    }

    private class OKAnswer extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Integer idEmergency = Vehicule.getInstance().getIdEmergency();

            return HttpClient.answerEmergency(idEmergency, token, "OK");
        }
    }

    private class KOAnswer extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Integer idEmergency = Vehicule.getInstance().getIdEmergency();

            return HttpClient.answerEmergency(idEmergency, token, "KO");
        }
    }
}
