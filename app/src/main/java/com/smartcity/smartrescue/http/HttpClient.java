package com.smartcity.smartrescue.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class HttpClient {
    public static final int CONNECT_TIMEOUT = 15;
    public static final int SOCKET_TIMEOUT = 15;

    public static boolean answerEmergency(String serverEndpoint, Integer idEmergency, String token, String response) {
        if (token.isEmpty() || response.isEmpty()) {
            throw new IllegalArgumentException("Empty args");
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Timber.d("RETRY INTERCEPTOR");
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        try {
                            int tryCount = 0;
                            while (!response.isSuccessful() && tryCount < 50) {
                                Timber.d("HTTPRequest retry : " + tryCount);
                                ++tryCount;
                                response = chain.proceed(request);
                            }
                        } catch (Exception e) {
                            Timber.e(e);
                        }

                        return response;
                    }
                })
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("idEmergency", idEmergency.toString())
                .add("token", token)
                .add("response", response)
                .build();

        Request request = new Request.Builder()
                .url(serverEndpoint)
                .post(body)
                .build();
        try {
            Response res = client.newCall(request).execute();
            Timber.d("Answer emergency sent. %d", res.code());
            res.close();

            return true;
        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    private static String getServerEndpoint() {
        return "http://192.168.0.14:1234/android";
    }

//    private static class RetryInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Timber.d("RETRY INTERCEPTOR");
//            Request request = chain.request();
//            Response response = new Response.Builder().build();
//            try {
//                response = chain.proceed(request);
//
//                int tryCount = 0;
//                while (!response.isSuccessful() && tryCount < 50) {
//                    Timber.d("HTTPRequest retry : " + tryCount);
//                    ++tryCount;
//                    response = chain.proceed(request);
//                }
//            } catch (Exception e) {
//                Timber.e(e);
//            }
//
//            return response;
//        }
//    }
}
