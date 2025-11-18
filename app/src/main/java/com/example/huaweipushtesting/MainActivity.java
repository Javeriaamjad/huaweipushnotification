package com.example.huaweipushtesting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.huaweipush.HuaweiApiHelper;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String accessToken;
    private String devicePushToken;
    
    private TextView tvDeviceTokenStatus;
    private TextView tvDeviceToken;
    private TextView tvAccessTokenStatus;
    private TextView tvAccessToken;
    private TextView tvPushStatus;
    private TextView tvPushMessage;
    private Button btnSendNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        tvDeviceTokenStatus = findViewById(R.id.tvDeviceTokenStatus);
        tvDeviceToken = findViewById(R.id.tvDeviceToken);
        tvAccessTokenStatus = findViewById(R.id.tvAccessTokenStatus);
        tvAccessToken = findViewById(R.id.tvAccessToken);
        tvPushStatus = findViewById(R.id.tvPushStatus);
        tvPushMessage = findViewById(R.id.tvPushMessage);
        btnSendNotification = findViewById(R.id.btnSendNotification);

        // Set button click listener
        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devicePushToken != null && !devicePushToken.isEmpty() && 
                    accessToken != null && !accessToken.isEmpty()) {
                    sendPushNotification(devicePushToken);
                } else {
                    runOnUiThread(() -> {
                        tvPushStatus.setText("⚠ Tokens not ready yet. Please wait...");
                        tvPushStatus.setBackgroundResource(R.drawable.status_warning);
                        tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_warning, getTheme()));
                    });
                }
            }
        });

        // Get device push token first
        getDevicePushToken();
        
        // Trigger access token generation
        getAccessToken();
    }

    private void getDevicePushToken() {
        runOnUiThread(() -> {
            tvDeviceTokenStatus.setText("Loading...");
            tvDeviceTokenStatus.setBackgroundResource(R.drawable.status_info);
            tvDeviceTokenStatus.setTextColor(getResources().getColor(R.color.meezan_info, getTheme()));
            tvDeviceToken.setText("Requesting device push token...");
        });

        new Thread(() -> {
            try {
                String appId = BuildConfig.HUAWEI_APP_ID;
                String token = HmsInstanceId.getInstance(this).getToken(appId, "HCM");
                devicePushToken = token;
                
                runOnUiThread(() -> {
                    tvDeviceTokenStatus.setText("✓ Device Push Token Received Successfully!");
                    tvDeviceTokenStatus.setBackgroundResource(R.drawable.status_success);
                    tvDeviceTokenStatus.setTextColor(getResources().getColor(R.color.meezan_success, getTheme()));
                    tvDeviceToken.setText(token);
                    checkAndEnableButton();
                });
                
                Log.i(TAG, "DEVICE PUSH TOKEN RECEIVED: " + token);
            } catch (ApiException e) {
                String errorMsg = "Error: " + e.getMessage();
                runOnUiThread(() -> {
                    tvDeviceTokenStatus.setText("✗ Failed to get Device Push Token");
                    tvDeviceTokenStatus.setBackgroundResource(R.drawable.status_error);
                    tvDeviceTokenStatus.setTextColor(getResources().getColor(R.color.meezan_error, getTheme()));
                    tvDeviceToken.setText(errorMsg);
                });
                Log.e(TAG, "Failed to get device push token: " + e.getMessage());
            }
        }).start();
    }

    private void getAccessToken() {
        runOnUiThread(() -> {
            tvAccessTokenStatus.setText("Loading...");
            tvAccessTokenStatus.setBackgroundResource(R.drawable.status_info);
            tvAccessTokenStatus.setTextColor(getResources().getColor(R.color.meezan_info, getTheme()));
            tvAccessToken.setText("Requesting access token from Huawei API...");
        });

        Log.d(TAG, "Requesting access token...");
        HuaweiApiHelper.getAccessToken(new HuaweiApiHelper.TokenCallback() {
            @Override
            public void onSuccess(String token) {
                accessToken = token;
                
                runOnUiThread(() -> {
                    tvAccessTokenStatus.setText("✓ Access Token Received Successfully!");
                    tvAccessTokenStatus.setBackgroundResource(R.drawable.status_success);
                    tvAccessTokenStatus.setTextColor(getResources().getColor(R.color.meezan_success, getTheme()));
                    tvAccessToken.setText(token);
                    checkAndEnableButton();
                });
                
                Log.i(TAG, "ACCESS TOKEN RECEIVED: " + token);
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvAccessTokenStatus.setText("✗ Failed to get Access Token");
                    tvAccessTokenStatus.setBackgroundResource(R.drawable.status_error);
                    tvAccessTokenStatus.setTextColor(getResources().getColor(R.color.meezan_error, getTheme()));
                    tvAccessToken.setText("Error: " + error);
                });
                Log.e(TAG, "Failed to get access token: " + error);
            }
        });
    }

    private void sendPushNotification(String pushToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            runOnUiThread(() -> {
                tvPushStatus.setText("✗ Access token not available");
                tvPushStatus.setBackgroundResource(R.drawable.status_error);
                tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_error, getTheme()));
            });
            Log.e(TAG, "Access token is not available. Please get token first.");
            return;
        }

        runOnUiThread(() -> {
            tvPushStatus.setText("Sending push notification...");
            tvPushStatus.setBackgroundResource(R.drawable.status_info);
            tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_info, getTheme()));
            tvPushMessage.setVisibility(android.view.View.GONE);
        });

        Log.d(TAG, "Sending push notification to token: " + pushToken);
        HuaweiApiHelper.sendPushNotification(accessToken, pushToken, new HuaweiApiHelper.TokenCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    tvPushStatus.setText("✓ Push Notification Sent Successfully!");
                    tvPushStatus.setBackgroundResource(R.drawable.status_success);
                    tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_success, getTheme()));
                    tvPushMessage.setText("Notification sent to device. Check your notification tray!");
                    tvPushMessage.setVisibility(android.view.View.VISIBLE);
                });
                Log.i(TAG, "PUSH NOTIFICATION SENT SUCCESSFULLY: " + result);
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvPushStatus.setText("✗ Failed to send Push Notification");
                    tvPushStatus.setBackgroundResource(R.drawable.status_error);
                    tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_error, getTheme()));
                    tvPushMessage.setText("Error: " + error);
                    tvPushMessage.setVisibility(android.view.View.VISIBLE);
                });
                Log.e(TAG, "Failed to send push notification: " + error);
            }
        });
    }

    private void checkAndEnableButton() {
        if (devicePushToken != null && !devicePushToken.isEmpty() && 
            accessToken != null && !accessToken.isEmpty()) {
            runOnUiThread(() -> {
                btnSendNotification.setEnabled(true);
                tvPushStatus.setText("✓ Ready to send notification");
                tvPushStatus.setBackgroundResource(R.drawable.status_success);
                tvPushStatus.setTextColor(getResources().getColor(R.color.meezan_success, getTheme()));
            });
        }
    }
}