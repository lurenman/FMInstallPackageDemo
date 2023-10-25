package com.example.fminstallpackagedemo.sdk;

public class InstalledConstant {
    /**
     * status定义
     * HasAuthorized：无需处理，用户已授权安装包列表权限。
     * BySettings：弹出提示框，建议用户去系统设置里给当前应用授权安装包列表权限。
     * ByAuthorized：弹出提示框，建议用户授权应用安装包列表权限。
     * ByUserRefused：弹出提示框，提示用户因拒绝了读取软件列表权限，会导致应用处于风险状态。
     * ByUnSystemLimited：无需处理，获取安装包列表信息未被系统限制。
     * ByMisMatch：⽆需处理，BySettnigs、ByAuthorized情况下已超过最⼤次数或未超过调⽤授权间隔。
     */
    public static final int HasAuthorized = 100;
    public static final int BySettings = 101;
    public static final int ByAuthorized = 102;
    public static final int ByUserRefused = 103;
    public static final int ByUnSystemLimited = 104;
    public static final int ByMisMatch = 105;
}
