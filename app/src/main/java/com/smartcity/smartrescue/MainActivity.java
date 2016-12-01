package com.smartcity.smartrescue;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String VEHICULE_ID = "ambulance-1";

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference locRef = dbRef.child(VEHICULE_ID);

    TextView gpsCoordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }

        gpsCoordView = (TextView) findViewById(R.id.gps_coord);
        String token = FirebaseInstanceId.getInstance().getToken();
        gpsCoordView.setText(token);
        Timber.d("Token %s", token);
        LocationService.getLocationManager(this, VEHICULE_ID);

        Button btn = (Button) findViewById(R.id.test_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestServer().execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                String text = "Latitude: "+ String.valueOf(latitude)+ ", Longitude: "+ String.valueOf(longitude);
                gpsCoordView.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class TestServer extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Timber.d("Register token %s", token);

            String url = "https://morning-beyond-41458.herokuapp.com/android";
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .addInterceptor(logging)
                    .build();

            RequestBody body = new FormBody.Builder()
                    .add("vehiculeId", MainActivity.VEHICULE_ID)
                    .add("token", token)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Timber.d("Request token sent. %d", response.code());
                response.close();
            } catch (IOException e) {
                Timber.e(e);
            }

            return true;
        }
    }
}
