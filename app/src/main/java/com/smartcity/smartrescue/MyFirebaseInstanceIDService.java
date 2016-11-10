package com.smartcity.smartrescue;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import timber.log.Timber;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
    }

    private void registerToken(String token) {
        Timber.d("Register token %s", token);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
            .add("token", token)
            .build();

        String url = "https://morning-beyond-41458.herokuapp.com";
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try {
            client.newCall(request).execute();
            Timber.d("Request sent.");
        } catch (IOException e) {
            Timber.e(e);
        }
    }
}
