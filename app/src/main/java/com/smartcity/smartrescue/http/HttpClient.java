package com.smartcity.smartrescue.http;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static com.smartcity.smartrescue.Constants.SERVER_ENDPOINT;

public class HttpClient {
    public static boolean answerEmergency(Integer idEmergency, String token, String response) {
        if (token.isEmpty() || response.isEmpty()) {
            throw new IllegalArgumentException("Empty args");
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("idEmergency", idEmergency.toString())
                .add("token", token)
                .add("response", response)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_ENDPOINT)
                .post(body)
                .build();
        try {
            Response res = client.newCall(request).execute();
            Timber.d("Request token sent. %d", res.code());
            res.close();

            return true;
        } catch (IOException e) {
            Timber.e(e);
        }

        return false;
    }

    public static boolean pushToken(String vehiculeId, String token) {
        if (vehiculeId.isEmpty() || token.isEmpty()) {
            throw new IllegalArgumentException("Empty args");
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .build();

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
