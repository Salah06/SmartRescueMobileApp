package com.smartcity.smartrescue.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.smartcity.smartrescue.BuildConfig;
import com.smartcity.smartrescue.LocationService;
import com.smartcity.smartrescue.R;
import com.smartcity.smartrescue.settings.SettingsActivity;
import com.smartcity.smartrescue.vehicule.Status;
import com.smartcity.smartrescue.vehicule.Vehicule;

import java.util.Set;

import timber.log.Timber;

import static com.smartcity.smartrescue.settings.SettingsActivity.MIBAND_MAC_KEY;
import static com.smartcity.smartrescue.settings.SettingsActivity.VEHICULE_ID_KEY;

public class MainActivity extends AppCompatActivity {
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference locRef;

    TextView gpsCoordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.ac_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        initVehicule(sp);
        if (sp.getString(VEHICULE_ID_KEY, "").isEmpty()) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        checkMibandAddress(sp);

        String vehiculeId = sp.getString(VEHICULE_ID_KEY, "");
        if (vehiculeId.isEmpty()) {
            throw new IllegalArgumentException("Vehicule ID is empty");
        }
        locRef = dbRef.child(vehiculeId);

        checkLocationPermission();

        gpsCoordView = (TextView) findViewById(R.id.gps_coord);
        String token = FirebaseInstanceId.getInstance().getToken();
        gpsCoordView.setText(token);
        LocationService.getLocationManager(this, vehiculeId);

        Button btn = (Button) findViewById(R.id.test_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new TestServer().execute();
            }
        });
    }

    private void initVehicule(SharedPreferences sp) {
        Vehicule.getInstance().setSharedPreferences(sp);
        Vehicule.getInstance().changeVehiculeStatus(Status.PENDING);
    }

    private void checkMibandAddress(SharedPreferences sp) {
        Timber.d(sp.getString(MIBAND_MAC_KEY, ""));
        if (sp.getString(MIBAND_MAC_KEY, "").isEmpty()) {
            String pairedMiband = getPairedMiBandMAC();
            Timber.d(pairedMiband);
            if (!pairedMiband.isEmpty()) {
                sp.edit().putString(MIBAND_MAC_KEY, pairedMiband).commit();
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getPairedMiBandMAC() {
        String result = "";

        if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice pairedDevice : pairedDevices) {
                if (pairedDevice != null && pairedDevice.getAddress() != null && pairedDevice.getName() != null && pairedDevice.getName().toLowerCase().contains("mi")) {
                    result = pairedDevice.getAddress();
                }
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();

        locRef.child("token").setValue(FirebaseInstanceId.getInstance().getToken());

        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                String text = "Latitude: "+ String.valueOf(latitude)+ ", Longitude: "+ String.valueOf(longitude);
                text += Vehicule.getInstance().getVehiculeStatus();
                gpsCoordView.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
