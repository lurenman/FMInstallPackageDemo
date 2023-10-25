package com.example.fminstallpackagedemo.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

public class FMAgent {

    /**
     * 获取应用获取安装包列表的状态
     *
     * @param activity
     * @param callback
     */
    public static void requestGetInstalledApps(Activity activity, GetInstalledAppsCallback callback) {
        if (activity == null || callback == null)
            return;
        boolean supportGetInstalledAppsPermission = TdPermission.isSupportGetInstalledAppsPermission(activity);
        if (!supportGetInstalledAppsPermission) {
            //设备系统未做管控，无需向用户申请权限
            callback.onResult(InstalledConstant.ByUnSystemLimited);
            return;
        }
        if (misMatch()) {
            //无需处理，BySettnigs、ByAuthorized情况下已超过最⼤次数或未超过调⽤授权间隔
            callback.onResult(InstalledConstant.ByMisMatch);
            return;
        }
        if (TdPermission.hasPms(activity, TdPermission.GET_INSTALLED_APPS)) {
            // 已经获取读取软件列表权限, 应用可以做需要的操作。
            callback.onResult(InstalledConstant.HasAuthorized);
        } else if (TdPermission.shouldShowRequestPermissionRationale(activity, TdPermission.GET_INSTALLED_APPS)) {
            // 用户已经不能通过系统requestPermissions接口请求权限弹框，需要去设置中授权
            callback.onResult(InstalledConstant.BySettings);
        } else {
            // 已经做管控，需要向用户申请权限
            // 弹出一个UI自定义框，提示用户应用将要申请安装包权限用于风控
            callback.onResult(InstalledConstant.ByAuthorized);
        }
    }

    /**
     * 发起弹框向用户申请安装包权限
     *
     * @param activity
     */
    public static void requestPackagesAuthority(Activity activity, GetInstalledAppsCallback callback) {
        if (activity == null || callback == null)
            return;
        boolean supportGetInstalledAppsPermission = TdPermission.isSupportGetInstalledAppsPermission(activity);
        if (!supportGetInstalledAppsPermission) {
            // 已经获取读取软件列表权限, 应用可以做需要的操作。
            callback.onResult(InstalledConstant.HasAuthorized);
            return;
        }
        TdPermission tdPermission = new TdPermission(activity);
        tdPermission.requestPermissionsFromFragment(new String[]{TdPermission.GET_INSTALLED_APPS}, new OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 已经获取读取软件列表权限, 应用可以做需要的操作。
                    callback.onResult(InstalledConstant.HasAuthorized);
                } else {
                    // 弹出提示框，提示用户因拒绝了读取软件列表权限，会导致应用处于风险状态。
                    callback.onResult(InstalledConstant.ByUserRefused);
                }
            }
        });
    }

    /**
     * 跳转到 系统设置 -> 应用权限 界面
     *
     * @param context
     * @param packageName
     */
    public static void openAppDetailsSettings(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName))
            return;
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Throwable e) {
        }
    }

    /**
     * 是否无需处理针对下发配置,demo测试
     *
     * @return
     */
    private static boolean misMatch() {
        return false;
    }
}
