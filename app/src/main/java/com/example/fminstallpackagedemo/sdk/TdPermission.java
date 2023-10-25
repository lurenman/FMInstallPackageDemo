package com.example.fminstallpackagedemo.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;


public class TdPermission {
    public static final String GET_INSTALLED_APPS = "com.android.permission.GET_INSTALLED_APPS";
    static final String TAG = TdPermission.class.getSimpleName();

    TdPermission.Lazy<PermissionsFragment> mPermissionsFragment;

    public TdPermission(Activity activity) {
        mPermissionsFragment = getLazySingleton(activity.getFragmentManager());
    }


    private TdPermission.Lazy<PermissionsFragment> getLazySingleton(final FragmentManager fragmentManager) {
        return new TdPermission.Lazy<PermissionsFragment>() {

            private PermissionsFragment permissionsFragment;

            @Override
            public synchronized PermissionsFragment get() {
                if (permissionsFragment == null) {
                    permissionsFragment = getPermissionsFragment(fragmentManager);
                }
                return permissionsFragment;
            }

        };
    }

    private PermissionsFragment getPermissionsFragment(FragmentManager fragmentManager) {
        PermissionsFragment permissionsFragment = findPermissionsFragment(fragmentManager);
        boolean isNewInstance = permissionsFragment == null;
        if (isNewInstance) {
            permissionsFragment = new PermissionsFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionsFragment;
    }

    private PermissionsFragment findPermissionsFragment(FragmentManager fragmentManager) {
        return (PermissionsFragment) fragmentManager.findFragmentByTag(TAG);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mPermissionsFragment.get().requestPermissions(permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions, OnRequestPermissionsResultCallback permissionsResultCallback) {
        mPermissionsFragment.get().requestPermissions(permissions, permissionsResultCallback);
    }

    /**
     * 是否能采集安装包列表。
     * 只有在系统要求必须申请"应用列表"权限，且目前没有权限时，就不能采集安装包列表
     *
     * @param context context
     * @return true：能采集安装包列表；false：不可以采集
     */
    public static boolean canGetInstalledApps(Context context) {
        if (supportGetInstallApps(context)) {
            return hasPms(context, GET_INSTALLED_APPS);
        }
        return true;
    }

    /**
     * 检测当前系统是否有特殊要求必须申请"应用列表"权限
     *
     * @param context context
     * @return true：有要求；false：没有要求
     */
    public static boolean supportGetInstallApps(Context context) {
        try {
            PermissionInfo info = context.getPackageManager().getPermissionInfo(GET_INSTALLED_APPS, 0);
            if (info != null && "android".equals(info.packageName) && (info.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS) {
                return true;
            }
        } catch (Exception exception) {
        }
        return false;
    }

    /**
     * 检查单个权限是否授权
     * checkCallingOrSelfPermission函数有漏洞，所以用checkPermission替代
     */
    public static boolean hasPms(Context context, String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取在请求权限之前判断是否用户已经拒绝了需要展示ui
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity,
                                                               String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    /**
     * 请求权限
     *
     * @param activity
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 判断当前系统是否已经对设备软件列表做管控
     *
     * @param context
     * @return
     */
    public static boolean isSupportGetInstalledAppsPermission(Context context) {
        try {
            int flag = Settings.Secure.getInt(context.getContentResolver(), "oem_installed_apps_runtime_permission_enable", 0);
            if (flag == 1) {
                return true;
            }
            PackageManager packageManager = context.getPackageManager();
            PermissionInfo permissionInfo = packageManager.getPermissionInfo("com.android.permission.GET_INSTALLED_APPS", 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return permissionInfo.getProtection() == PermissionInfo.PROTECTION_DANGEROUS;
            }
        } catch (Throwable e) {
            return false;
        }
        return false;
    }


    public interface Lazy<V> {
        V get();
    }
}
