package fr.cefim.android.birthday_app.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UtilApi {

    public static final String URL_LOGIN = "http://192.168.1.61:8080/login";
    public static final String CREATE_BIRTHDAY = "http://192.168.1.61:8080/users/%s/birthdays";

    public static OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void get(String url, final ApiCallback callback) {
        Request request = new Request.Builder().url(url).build();
        requestCallback(request, callback);
    }

    public static void post(String url, Map<String, String> mapBody, String token, final ApiCallback callback) {
        Request.Builder builder = new Request.Builder();
        if (token != null) {
            builder.addHeader("Authorization", token);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : mapBody.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("LOG", jsonObject.toString());
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = builder
                .url(url)
                .post(body)
                .build();
        requestCallback(request, callback);
    }

    private static void requestCallback(Request request, ApiCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure("ON FAILURE");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful())
                    callback.onResponseSuccess(Objects.requireNonNull(response.body()).string());
                else {
                    callback.onResponseFail("ON RESPONSE FAIL");
                }
            }
        });
    }

}
