package com.example.fminstallpackagedemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fminstallpackagedemo.sdk.FMAgent;
import com.example.fminstallpackagedemo.sdk.GetInstalledAppsCallback;
import com.example.fminstallpackagedemo.sdk.InstalledConstant;
import com.example.fminstallpackagedemo.sdk.TdPermission;

import java.util.List;

import cn.tongdun.android.shell.TDOption;
import cn.tongdun.mobrisk.TDRisk;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FM_MainActivity";
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Button btn_get_status = (Button) findViewById(R.id.btn_get_status);
        btn_get_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstalledAppsStatus();
            }
        });

        Button btn_request_permission = (Button) findViewById(R.id.btn_request_permission);
        btn_request_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInstallPackagePermission();
            }
        });

        Button btn_get_package_list = (Button) findViewById(R.id.btn_get_package_list);
        btn_get_package_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callInstallPackagesList();
            }
        });

        Button btn_sdkip = (Button) findViewById(R.id.btn_sdkip);
        btn_sdkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SecondActivity.class);
                startActivity(intent);
            }
        });
        //测试判断当前系统是否已经对设备软件列表做管控（oppo）
        boolean supportGetInstallApps = TdPermission.isSupportGetInstalledAppsPermission(this);
        showToast(this, "isSupportGetInstalledAppsPermission: " + supportGetInstallApps);
        TDRisk.Builder builder = new TDRisk.Builder();

    }

    /**
     * 获取应用获取安装包列表的状态
     */
    private void getInstalledAppsStatus() {
        FMAgent.requestGetInstalledApps(this, new GetInstalledAppsCallback() {
            @Override
            public void onResult(int status) {
                // 在这里处理回调返回的状态
                if (status == InstalledConstant.BySettings) {
                    // 弹出提示框，提示用户为风控诉求，建议用户去系统设置里给当前应用授权安装包列表权限。
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("温馨提示")
                            .setMessage("为了保障您的资金财产安全，进行网络金融风控，建议您到设置中授权应用安装包列表权限。")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 跳转到 系统设置 -> 应用权限 界面
                                    // String packageName = "com.icbc"; // 替换为你要跳转的应用程序的包名,如com.icbc
                                    String packageName = "com.example.fminstallpackagedemo";
                                    FMAgent.openAppDetailsSettings(MainActivity.this, packageName);
                                    Log.d(TAG, "onClick: 跳转到 系统设置 -> 应用权限 界面");
                                }
                            })
                            .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                    Log.d(TAG, "onResult: 弹出提示框，提示用户为风控诉求，建议用户去系统设置里给当前应用授权安装包列表权限。");
                    showToast(mContext, "onResult: 弹出提示框，提示用户为风控诉求，建议用户去系统设置里给当前应用授权安装包列表权限。");
                } else if (status == InstalledConstant.ByAuthorized) {
                    // 弹出提示框，提示用户为风控诉求，建议用户授权应用安装包列表权限。
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("温馨提示")
                            .setMessage("为了实现进行网络金融风控，请您允许中国工商银行获取安装包列表权限。")
                            .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 发起系统弹框，向用户申请安装包权限
                                    requestInstallPackagePermission();
                                    Log.d(TAG, "onClick: 发起系统弹框，向用户申请安装包权限");
                                }
                            })
                            .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                    Log.d(TAG, "onResult: 弹出提示框，提示用户为风控诉求，建议用户授权应用安装包列表权限。");
                    showToast(mContext, "onResult: 弹出提示框，提示用户为风控诉求，建议用户授权应用安装包列表权限。");
                } else {
                    // 用户已授权或设备系统未做管控，无需处理 对应InstalledConstant.packagesHasAuthorized InstalledConstant.getPackagesByUnSystemLimited
                    Log.d(TAG, "onResult: 用户已授权或设备系统未做管控，无需处理");
                    showToast(mContext, "onResult: 用户已授权或设备系统未做管控，无需处理");
                }
            }
        });
    }

    /**
     * 请求安装包列表权限
     */
    private void requestInstallPackagePermission() {
        FMAgent.requestPackagesAuthority(this, new GetInstalledAppsCallback() {
            @Override
            public void onResult(int status) {
                if (status == InstalledConstant.ByUserRefused) {
                    // 弹出提示框，提示用户因拒绝了读取软件列表权限，会导致应用处于风险状态。
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("温馨提示")
                            .setMessage("因拒绝授予应用安装包列表权限，将会导致应用存在风险状态")
                            .setPositiveButton("我已知晓", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 我已知晓
                                    Log.d(TAG, "requestPackagesAuthority : 我已知晓");
                                }
                            });
                    builder.show();
                    Log.d(TAG, "requestPackagesAuthority :用户拒绝权限");
                    showToast(mContext, "requestPackagesAuthority :用户拒绝权限");
                } else {
                    Log.d(TAG, "requestPackagesAuthority : 用户已授权，无需处理");
                    showToast(mContext, "requestPackagesAuthority : 用户已授权，无需处理");
                }
            }
        });
    }

    /**
     * 获取安装包列表测试
     */
    private void callInstallPackagesList() {
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if (installedPackages != null) {
            Log.d(TAG, "callInstallPackagesList size: " + installedPackages.size());
            showToast(mContext, "callInstallPackagesList size: " + installedPackages.size());
        }
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}