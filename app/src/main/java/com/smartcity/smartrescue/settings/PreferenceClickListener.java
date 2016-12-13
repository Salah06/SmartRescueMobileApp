package com.smartcity.smartrescue.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.smartcity.smartrescue.http.HttpClient;

import timber.log.Timber;

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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String vehiculeId = sp.getString(VEHICULE_ID_KEY, "");

            return HttpClient.pushToken(vehiculeId, token);
        }
    }
}
