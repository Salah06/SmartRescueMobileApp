package com.smartcity.smartrescue.service;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.smartcity.smartrescue.ui.RequestActivity;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RequestCommand implements Command {
    public static final String TRIGGER = "request";
    public static final String EXTRA_ADDRESS = "ADDRESS";
    private Context context;

    @Override
    public void run(JsonObject data) {
        // TODO: if vehicule is available
        Timber.d("COMMAND");
        String address = data.get("address").getAsString();
        Intent i = new Intent(context, RequestActivity.class);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(EXTRA_ADDRESS, address);
        context.startActivity(i);
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}