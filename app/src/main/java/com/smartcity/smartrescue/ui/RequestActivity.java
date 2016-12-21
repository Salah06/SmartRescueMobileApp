package com.smartcity.smartrescue.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.smartcity.smartrescue.R;
import com.smartcity.smartrescue.service.RequestCommand;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RequestActivity extends AppCompatActivity {

    private String address;

    @BindView(R.id.addressTv)
    TextView addressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_request);
        Timber.d("ACTIVITY");
        ButterKnife.bind(this);

        String address = getIntent().getStringExtra(RequestCommand.EXTRA_ADDRESS);
        this.address = address;
        Timber.d(address);
        addressTv.setText(address);
    }

    @OnClick(R.id.acceptBtn)
    public void acceptClick() {
        Uri intentUri = Uri.parse("google.navigation:q="+address);
        Intent i = new Intent(Intent.ACTION_VIEW, intentUri);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.google.android.apps.maps");
        startActivity(i);
    }

    @OnClick(R.id.denyBtn)
    public void denyClick() {
        finish();
    }
}
