package com.smartcity.smartrescue.service;

import android.content.Context;

import com.google.gson.JsonObject;


public class SituationCommand implements Command {
    public static final String TRIGGER = "SituationCommand";
    private Context context;

    @Override
    public void run(JsonObject data) {

    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
