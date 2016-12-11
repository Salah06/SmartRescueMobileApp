package com.smartcity.smartrescue.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;

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

public class PreferenceClickListener implements Preference.OnPreferenceClickListener {

    private Context context;

    public PreferenceClickListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        new TestServer().execute();
        return false;
    }

    private class TestServer extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Timber.d("Register token %s", token);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .addInterceptor(logging)
                    .build();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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

                return true;
            } catch (IOException e) {
                Timber.e(e);
            }

            return false;
        }
    }
}
