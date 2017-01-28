package com.smartcity.smartrescue.fcm;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
//        String token = FirebaseInstanceId.getInstance().getToken();
//        registerToken(token);
    }

//    private void registerToken(String token) {
//        Timber.d("Register token %s", token);
//
//        OkHttpClient client = new OkHttpClient();
//
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
//        client.interceptors().add(logging);
//
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        String vehiculeId = sp.getString(VEHICULE_ID_KEY, "");
//        RequestBody body = new FormBody.Builder()
//            .add("vehiculeId", vehiculeId)
//            .add("token", token)
//            .build();
//
//        Request request = new Request.Builder()
//            .url(SERVER_ENDPOINT)
//            .post(body)
//            .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            Timber.d("Request token sent. %d", response.code());
//            response.close();
//        } catch (IOException e) {
//            Timber.e(e);
//        }
//    }
}
