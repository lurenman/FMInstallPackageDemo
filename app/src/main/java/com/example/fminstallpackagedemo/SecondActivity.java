package com.example.fminstallpackagedemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.fminstallpackagedemo.sdk.FMAgent;
import com.example.fminstallpackagedemo.sdk.GetInstalledAppsCallback;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "FM_SecondActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mContext = this;
        Button btn_request_permission = (Button) findViewById(R.id.btn_request_permission);
        btn_request_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FMAgent.requestPackagesAuthority((FragmentActivity) mContext, new GetInstalledAppsCallback() {
                    @Override
                    public void onResult(int status) {
                        Log.d(TAG, "onResult status: " + status);
                    }
                });
            }
        });
    }
}
