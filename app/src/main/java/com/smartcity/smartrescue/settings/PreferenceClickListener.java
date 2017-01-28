package com.smartcity.smartrescue.settings;

import android.content.Context;
import android.preference.Preference;
import android.widget.Toast;

public class PreferenceClickListener implements Preference.OnPreferenceClickListener {

    private Context context;

    public PreferenceClickListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Toast.makeText(context, "Preference click", Toast.LENGTH_SHORT).show();
        return true;
    }
}
