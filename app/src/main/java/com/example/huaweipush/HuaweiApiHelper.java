package com.example.huaweipush;

import android.util.Log;
import com.example.huaweipushtesting.BuildConfig;
import org.json.JSONObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class HuaweiApiHelper {
    private static final String TAG = "HuaweiApiHelper";
    
    // Shared OkHttpClient instance to avoid redundant creation
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public interface TokenCallback {
        void onSuccess(String accessToken);
        void onError(String error);
    }

    public static void getAccessToken(final TokenCallback callback) {
        new Thread(() -> {
            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("grant_type", "client_credentials")
                        .add("client_id", BuildConfig.HUAWEI_CLIENT_ID)
                        .add("client_secret", BuildConfig.HUAWEI_CLIENT_SECRET)
                        .build();

                Request request = new Request.Builder()
                        .url(BuildConfig.HUAWEI_TOKEN_URL)
                        .post(formBody)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                Response response = httpClient.newCall(request).execute();
                String responseBody = response.body().string();
                
                Log.d(TAG, "Token Response: " + responseBody);

                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String accessToken = jsonObject.getString("access_token");
                    callback.onSuccess(accessToken);
                } else {
                    callback.onError("Error: " + response.code() + " - " + responseBody);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error getting access token", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public static void sendPushNotification(String accessToken, String pushToken, 
                                           final TokenCallback callback) {
        new Thread(() -> {
            try {
                // Construct push message
                JSONObject message = new JSONObject();
                
                // Notification
                JSONObject notification = new JSONObject();
                notification.put("title", "Test Notification");
                notification.put("body", "This is a test push notification from Huawei Push Kit");
                
                // Android config
                JSONObject android = new JSONObject();
                JSONObject androidNotification = new JSONObject();
                androidNotification.put("click_action", new JSONObject()
                        .put("type", 3));
                android.put("notification", androidNotification);
                
                // Message structure
                message.put("notification", notification);
                message.put("android", android);
                message.put("token", new org.json.JSONArray().put(pushToken));

                JSONObject payload = new JSONObject();
                payload.put("message", message);

                String pushUrl = BuildConfig.HUAWEI_PUSH_URL_BASE + "/" + BuildConfig.HUAWEI_APP_ID + "/messages:send";
                
                RequestBody body = RequestBody.create(
                        payload.toString(),
                        okhttp3.MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(pushUrl)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = httpClient.newCall(request).execute();
                String responseBody = response.body().string();
                
                Log.d(TAG, "Push Response: " + responseBody);

                if (response.isSuccessful()) {
                    callback.onSuccess("Push notification sent successfully!");
                } else {
                    callback.onError("Error: " + response.code() + " - " + responseBody);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error sending push", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
}

