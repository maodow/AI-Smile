package com.example.ai_smile.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import com.example.ai_smile.interfaces.ActivityHintView;
import com.example.ai_smile.interfaces.ActivityHintViewImpl;
import com.example.ai_smile.utils.ActivityStackManager;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import java.io.Serializable;
import butterknife.ButterKnife;

/**
 * Created by Robert yao on 2016/10/17.
 *
 * 父类自定义了APP loading框, 为了减少上层改动
 * 在此也加入继承 see line 132
 *
 */
public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> implements ActivityHintView/*, EasyPermissions.PermissionCallbacks*/ {

    private static final int WRITE_EXTERNAL_STORAGE = 100;
    private static final int RECORD_AUDIO = 101;
    private static final int CAMERA = 102;

    private ActivityHintView activityHintView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutResId());
        activityHintView = new ActivityHintViewImpl(this);
        ActivityStackManager.getActivityStackManager().addActivity(this);
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutResId();

    public void startActivity(Class classs) {
        startActivity(new Intent(this, classs));
    }

    public void startActivity(Class classs, Object object) {
        startActivity(new Intent(this, classs).putExtra("data", (Serializable) object));
    }

    @Override
    public void showToast(String msg) {
        activityHintView.showToast(msg);
    }

    @Override
    public void showToast(int resId) {
        activityHintView.showToast(resId);
    }

    @Override
    public void showProgressDialog(int msgRes) {
        activityHintView.showProgressDialog(msgRes);
    }

    @Override
    public void showProgressDialog(String msg) {
        activityHintView.showProgressDialog(msg);
    }

    @Override
    public void showProgressDialog(String msg, String title) {
        activityHintView.showProgressDialog(msg, title);
    }

    @Override
    public void showProgressDialog(String msg, String title, View view) {
        activityHintView.showProgressDialog(msg, title, view);
    }

    @Override
    public void hideProgressDialogIfShowing() {
        activityHintView.hideProgressDialogIfShowing();
    }


    /**
     * 自定义APP加载loading框 on 2019/5/17.
     */
    @Override
    public void showWaitingDialog() {
        activityHintView.showWaitingDialog();
    }

    @Override
    public boolean hideWaitingDialog() {
        return activityHintView.hideWaitingDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> list) {
//
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> list) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
//            new AppSettingsDialog.Builder(this)
//                    .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置屏幕以修改应用权限")
//                    .setTitle("必需权限")
//                    .setPositiveButton("去设置")
//                    .setNegativeButton("取消")
//                    .build()
//                    .show();
//        }
//    }

    /**
     * 检查权限
     */
//    @AfterPermissionGranted(RECORD_AUDIO)
//    public boolean checkAudioPerm() {
//        String[] params = {Manifest.permission.RECORD_AUDIO};
//        if (EasyPermissions.hasPermissions(this, params)) {
//            return true;
//        } else {
//            EasyPermissions.requestPermissions(this, "需要语音权限", RECORD_AUDIO, params);
//            return false;
//        }
//    }

    /**
     * 检查权限
     */
//    @AfterPermissionGranted(CAMERA)
//    public boolean checkCameraPerm() {
//        String[] params = {Manifest.permission.CAMERA};
//        if (EasyPermissions.hasPermissions(this, params)) {
//            return true;
//        } else {
//            EasyPermissions.requestPermissions(this, "请打开相机相关权限，否则app将不能正常运行", CAMERA, params);
//            return false;
//        }
//    }

    /**
     * 检查SD卡权限
     */
//    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
//    public boolean checkSDcard() {
//        String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(this, mPermissionList)) {
//            return true;
//        } else {
//            EasyPermissions.requestPermissions(this, "请打开存储空间的权限，否则app将不能正常运行", WRITE_EXTERNAL_STORAGE, mPermissionList);
//            return false;
//        }
//    }

    public void checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();

        if (!isOpened) {
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("没有通知权限，此应用程序某些功能可能无法正常工作。请打开设置以修改应用权限")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", BaseActivity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", BaseActivity.this.getPackageName());
                                intent.putExtra("app_uid", BaseActivity.this.getApplicationInfo().uid);
                                startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + BaseActivity.this.getPackageName()));
                            } else if (Build.VERSION.SDK_INT >= 15) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", BaseActivity.this.getPackageName(), null));
                            }
                            startActivity(intent);
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }

    private InputMethodManager imm;

    public boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom != 0;
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;
//        ButterKnife.unbind(this);
    }

}
