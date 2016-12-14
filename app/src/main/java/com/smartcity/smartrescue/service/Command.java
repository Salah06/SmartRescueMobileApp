package com.smartcity.smartrescue.service;

import android.content.Context;

import com.google.gson.JsonObject;


public interface Command {
    void run(JsonObject data);

    void setContext(Context context);
}
