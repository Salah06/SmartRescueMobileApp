package com.smartcity.smartrescue;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

        String url = "https://morning-beyond-41458.herokuapp.com/android";
        OkHttpClient client = new OkHttpClient();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(logging);

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
    }
}
