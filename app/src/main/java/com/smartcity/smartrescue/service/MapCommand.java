package com.smartcity.smartrescue.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.gson.JsonObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MapCommand implements Command {
    public static final String TRIGGER = "map";
    private Context context;

    @Override
    public void run(JsonObject data) {
        String address = "Valbonne";
        Uri intentUri = Uri.parse("google.navigation:q="+address);
        Intent i = new Intent(Intent.ACTION_VIEW, intentUri);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.google.android.apps.maps");
        context.startActivity(i);
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
