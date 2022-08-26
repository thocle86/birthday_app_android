package fr.cefim.android.birthday_app.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UtilApi {

    public static final String URL_LOGIN = "http://192.168.1.61:8080/login";
    public static final String CREATE_BIRTHDAY = "http://192.168.1.61:8080/%s/birthdays";

    public static OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void get(String url, final ApiCallback callback) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.fail("error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful())
                    callback.success(response.body().string());
                else {
                    callback.fail("error");
                }
            }
        });
    }

    public static void post(String url, Map<String, String> map, final ApiCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.fail("error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful())
                    callback.success(response.body().string());
                else {
                    callback.fail("error");
                }
            }
        });
    }

}
