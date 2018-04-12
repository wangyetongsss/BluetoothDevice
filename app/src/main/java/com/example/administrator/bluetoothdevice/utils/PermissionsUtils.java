package com.example.administrator.bluetoothdevice.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.administrator.bluetoothdevice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2018/4/12 10:58
 * 创建：Administrator on
 * 描述:权限管理类
 */

public class PermissionsUtils {
    private static final String TAG = "PermissionsUtils";
    private final static int REQUEST_RUNTIME_PERMISSION_ID = 0x2105;

    private final Activity mBaseActivity;
    private IResultCallBack ISuccessCallBack = null;

    public PermissionsUtils(Activity mBaseActivity) {
        this.mBaseActivity = mBaseActivity;
    }

    protected Activity getActivity() {
        return mBaseActivity;
    }

    //TODO 检查当前界面需要的运行时权限
    public void checkRuntimePermissions(String[] permission) {
        Logger.i(TAG, "checkRuntimePermissions()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && null != permission) {
            List<String> deniedPermissions = findDeniedPermissions(permission);
            Logger.i(TAG, "checkRuntimePermissions() " + deniedPermissions.size());
            if (deniedPermissions.size() > 0) {
                //有需要申请的权限，提交申请
                requestRuntimePermissions(deniedPermissions
                        .toArray(new String[deniedPermissions.size()]));
            } else {
                if (null != ISuccessCallBack) {
                    ISuccessCallBack.onRequestPermissionsSuccess();
                }
            }
        }
    }

    //TODO 检查当前界面需要的运行时权限,任何版本都启用
    public void checkRuntimePermissions(String[] permission, int version) {
        Logger.i(TAG, "checkRuntimePermissions()");
        if (null != permission) {
            List<String> deniedPermissions = findDeniedPermissions(permission);
            Logger.i(TAG, "checkRuntimePermissions() " + deniedPermissions.size());
            if (deniedPermissions.size() > 0) {
                //有需要申请的权限，提交申请
                requestRuntimePermissions(deniedPermissions
                        .toArray(new String[deniedPermissions.size()]));
            } else {
                if (null != ISuccessCallBack) {
                    ISuccessCallBack.onRequestPermissionsSuccess();
                }
            }
        }
    }

    //TODO 获取授权状态
    public boolean authorizeRuntimePermissions(String[] permission) {
        boolean bRet = true;
        List<String> deniedPermissions = findDeniedPermissions(permission);
        if (deniedPermissions.size() > 0) {
            bRet = false;
            checkRuntimePermissions(permission);
        }
        return bRet;
    }

    //TODO 查找运行时权限
    private List<String> findDeniedPermissions(String[] permission) {
        List<String> denyPermissions = new ArrayList<>();
        if (null != permission) {
            for (String value : permission) {
                if (ContextCompat.checkSelfPermission(getActivity(), value)
                        != PackageManager.PERMISSION_GRANTED) {
                    //没有权限 就添加
                    Logger.i(TAG, "findDeniedPermissions() " + value);
                    denyPermissions.add(value);
                }
            }
        }
        return denyPermissions;
    }

    //TODO 检测这些权限中是否有 没有授权需要提示的
    //默认是false,但是只要请求过一次权限就会为true,
    //除非点了不再询问才会重新变为false
    private boolean shouldShowPermissions(String[] permission) {
        boolean bRet = false;
        for (String value : permission) {
            bRet = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), value);
            Logger.i(TAG, "shouldShowPermissions() " + value + " bRet=" + bRet);
            if (bRet) {
                break;
            }
        }
        Logger.i(TAG, "shouldShowPermissions(E) bRet" + bRet);
        return bRet;
    }

    //TODO 请求运行时权限
    private void requestRuntimePermissions(String[] permission) {
        if (shouldShowPermissions(permission)) {
            //提示用户需要授权
            showApplySnackbar(permission);
        } else {
            //直接显示申请界面
            showApplyDialog(permission);
        }
    }

    //TODO 显示权限申请界面
    private void showApplyDialog(String[] permission) {
        Logger.i(TAG, "showApplyDialog() ");
        ActivityCompat.requestPermissions(getActivity(), permission,
                REQUEST_RUNTIME_PERMISSION_ID);
    }

    //TODO 判断请求权限是否成功
    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //TODO 显示提示信息
    private void showApplySnackbar(final String[] permission) {
        View view = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Logger.i(TAG, "showApplySnackbar() " + view);
        Snackbar snackbar = Snackbar.make(view, R.string.str_no_permissions_tip,
                Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getActivity().getResources().getColor(R.color.white))
                .setAction(R.string.str_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showApplyDialog(permission);
                    }
                });
        View bar = snackbar.getView();
        bar.setBackgroundColor(getActivity().getResources().getColor(R.color.white_trans_50));
        snackbar.show();
    }

    //TODO 显示去设置中设置权限界面
    private void showGoSettingSnackbar() {
        View view = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Logger.i(TAG, "showGoSettingSnackbar() " + view);
        Snackbar snackbar = Snackbar.make(view, R.string.str_set_permissions_tip,
                Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getActivity().getResources().getColor(R.color.white))
                .setAction(R.string.str_to_setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去设置界面设置
                        settingPermission();
                    }
                });
        View bar = snackbar.getView();
        bar.setBackgroundColor(getActivity().getResources().getColor(R.color.white_trans_50));
        snackbar.show();
    }

    //TODO 提示授权成功
    private void showSuccessSnackbar() {
        View view = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Logger.i(TAG, "showSuccessSnackbar() " + view);
        Snackbar snackbar = Snackbar.make(view, R.string.str_runtime_permission_succ,
                Snackbar.LENGTH_SHORT);
        View bar = snackbar.getView();
        bar.setBackgroundColor(getActivity().getResources().getColor(R.color.white_trans_50));
        snackbar.show();
    }

    //TODO 用户操作结果处理
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Logger.i(TAG, "onRequestPermissionsResult() requestCode=" + requestCode);
        if (requestCode == REQUEST_RUNTIME_PERMISSION_ID) {
            if (verifyPermissions(grantResults)) {
                if (null != ISuccessCallBack) {
                    ISuccessCallBack.onRequestPermissionsSuccess();
                    showSuccessSnackbar();
                }
            } else {
                //用户没有通过申请
                if (!shouldShowPermissions(permissions)) {
                    showGoSettingSnackbar();
                } else {
                    showApplySnackbar(permissions);
                }
            }
        }
    }

    //TODO 设置成功的回调函数
    public PermissionsUtils setResultCallBack(IResultCallBack callBack) {
        ISuccessCallBack = callBack;
        return this;
    }


    //////////////////////////////////////////////////////////
    //设置程序的权限设置

    private void settingPermission() {
        if (Build.MANUFACTURER.equals("Huawei")) {
            settingPermissionHuawei();
        } else if (Build.MANUFACTURER.equals("Meizu")) {
            settingPermissionMeizu();
        } else if (Build.MANUFACTURER.equals("Xiaomi")) {
            settingPermissionXiaomi();
        } else if (Build.MANUFACTURER.equals("Sony")) {
            settingPermissionSony();
        } else if (Build.MANUFACTURER.equals("OPPO")) {
            settingPermissionOppo();
        } else if (Build.MANUFACTURER.equals("LG")) {
            settingPermissionLG();
        } else if (Build.MANUFACTURER.equals("vivo")) {
            settingPermissionVivo();
        } else if (Build.MANUFACTURER.equals("samsung")) {
            settingPermissionSamsung();
        } else if (Build.MANUFACTURER.equals("Letv")) {
            settingPermissionLetv();
        } else if (Build.MANUFACTURER.equals("ZTE")) {
            settingPermissionZTE();
        } else if (Build.MANUFACTURER.equals("YuLong")) {
            settingPermissionYuLong();
        } else if (Build.MANUFACTURER.equals("LENOVO")) {
            settingPermissionLENOVO();
        } else {
            settingPermissionAndroid();
        }
    }

    //原生系统的权限界面
    private void settingPermissionAndroid() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",
                    getActivity().getPackageName());
        }
        getActivity().startActivity(localIntent);
    }

    //华为系统的权限界面
    private void settingPermissionHuawei() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", getActivity().getPackageName());
        ComponentName comp = new ComponentName("com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        getActivity().startActivity(intent);
    }

    //魅族系统的权限界面
    private void settingPermissionMeizu() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", getActivity().getPackageName());
        getActivity().startActivity(intent);
    }

    //小米系统的权限界面
    private void settingPermissionXiaomi() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.setComponent(componentName);
        intent.putExtra("extra_pkgname", getActivity().getPackageName());
        getActivity().startActivity(intent);
    }

    //索尼系统的权限界面
    private void settingPermissionSony() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", getActivity().getPackageName());
        ComponentName comp = new ComponentName("com.sonymobile.cta",
                "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        getActivity().startActivity(intent);
    }

    //oppo系统的权限界面
    private void settingPermissionOppo() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", getActivity().getPackageName());
        ComponentName comp = new ComponentName("com.color.safecenter",
                "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        getActivity().startActivity(intent);
    }

    //LG系统的权限界面
    private void settingPermissionLG() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", getActivity().getPackageName());
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        getActivity().startActivity(intent);
    }

    //vivo系统的权限界面
    private void settingPermissionVivo() {
        settingPermissionAndroid();
    }

    //三星系统的权限界面
    private void settingPermissionSamsung() {
        settingPermissionAndroid();
    }

    //乐视系统的权限界面
    private void settingPermissionLetv() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", getActivity().getPackageName());
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        getActivity().startActivity(intent);
    }

    //中兴系统的权限界面
    private void settingPermissionZTE() {
        settingPermissionAndroid();
    }

    //酷派系统的权限界面
    private void settingPermissionYuLong() {
        settingPermissionAndroid();
    }

    //联想系统的权限界面
    private void settingPermissionLENOVO() {
        settingPermissionAndroid();
    }


    ///////////////////////////////////////////////////
    //TODO 权限结果回调函数
    public interface IResultCallBack {
        public void onRequestPermissionsSuccess();
    }
}
