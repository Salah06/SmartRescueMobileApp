package com.smartcity.smartrescue.fcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static com.smartcity.smartrescue.Constants.SERVER_ENDPOINT;
import static com.smartcity.smartrescue.settings.SettingsActivity.VEHICULE_ID_KEY;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
//        String token = FirebaseInstanceId.getInstance().getToken();
//        registerToken(token);
    }

    private void registerToken(String token) {
        Timber.d("Register token %s", token);

        OkHttpClient client = new OkHttpClient();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(logging);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String vehiculeId = sp.getString(VEHICULE_ID_KEY, "");
        RequestBody body = new FormBody.Builder()
            .add("vehiculeId", vehiculeId)
            .add("token", token)
            .build();

        Request request = new Request.Builder()
            .url(SERVER_ENDPOINT)
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
