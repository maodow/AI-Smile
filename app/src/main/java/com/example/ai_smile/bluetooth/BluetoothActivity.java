package com.example.ai_smile.bluetooth;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ai_smile.BuildConfig;
import com.example.ai_smile.MainActivity;
import com.example.ai_smile.R;
import com.example.ai_smile.activity.Camera3Activity;
import com.example.ai_smile.activity.DetectorActivity;
import com.example.ai_smile.adapter.DeviceListAdapter;
import com.example.ai_smile.base.BaseActivity;
import com.example.ai_smile.base.BaseCQActivity;
import com.example.ai_smile.base.BlankPresenter;
import com.example.ai_smile.utils.MyBus;
import com.example.ai_smile.utils.ToastUtils;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.ScanCallback;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.example.ai_smile.R;
import com.example.ai_smile.adapter.DeviceListAdapter;
import com.example.ai_smile.base.BaseActivity;
import com.example.ai_smile.base.BlankPresenter;
import com.example.ai_smile.utils.ToastUtils;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.ScanCallback;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BluetoothActivity extends BaseCQActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.mDeviceList)
    ListView mDeviceList;

    private ProgressDialog progressDialog;
    private BleController mBleController;

    private String iconPath = "";
    private static final int TAKEPHOTO_REQUEST = 196;

    //搜索结果列表
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestBlueToothPermission();
//        initBleScan();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.top_view)
                .statusBarDarkFont(true)//android 6.0以上设置状态栏字体为暗色
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                .fullScreen(true)
                .init();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bluetooth;
    }

    @OnClick({R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void requestBlueToothPermission() {
        XXPermissions.with(this)
                //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .constantRequest()
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            initBleScan();
                        } else {
//                            ToastUtils.showShort("获取权限成功，部分权限未正常授予");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            ToastUtils.showToast(BluetoothActivity.this, "被永久拒绝授权，请手动授予权限");
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(BluetoothActivity.this);
                        } else {
                            ToastUtils.showToast(BluetoothActivity.this, "获取权限失败");
                        }
                    }
                });
    }

    private void initBleScan() {
        // TODO  第一步：初始化
        mBleController = BleController.getInstance().init(this);
        // TODO  第二步：搜索设备，获取列表后进行展示
        scanDevices();
    }

    private void scanDevices() {
        showProgressDialog("请稍候", "正在搜索蓝牙设备...");
        mBleController.scanBle(0, new ScanCallback() {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                if (bluetoothDevices.size() > 0) {
                    mDeviceList.setAdapter(new DeviceListAdapter(BluetoothActivity.this, bluetoothDevices));
                    mDeviceList.setOnItemClickListener(BluetoothActivity.this);
                } else {
                    Toast.makeText(BluetoothActivity.this, "没有搜索到可供连接的蓝牙设备！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                //过滤是否含有 Ai-Thinker 名字的蓝牙设备
                if (device.getName() != null && device.getName().contains("Ai-Thinker"))
                    if (!bluetoothDevices.contains(device)) {
                        bluetoothDevices.add(device);
                    }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showProgressDialog("请稍候", "蓝牙正在连接...");
        // TODO 第三步：点击条目后,获取地址，根据地址连接设备
        String address = bluetoothDevices.get(i).getAddress();
        mBleController.connect(0, address, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                hideProgressDialog();
                Toast.makeText(BluetoothActivity.this, "蓝牙连接成功!", Toast.LENGTH_SHORT).show();
                takePhoto();
                //播放声音
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playAudio(R.raw.hint_take_photo);
                    }
                }, 2000);
            }

            @Override
            public void onConnFailed() {
                hideProgressDialog();
                Toast.makeText(BluetoothActivity.this, "蓝牙连接失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playAudio(int resId){
        AudioManager audioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), resId);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    /**
     * 调用相机拍照
     */
    private void takePhoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = getOutputMediaFileUri(this);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(cameraIntent, TAKEPHOTO_REQUEST);
    }

    private Uri getOutputMediaFileUri(Context context) {
        File mediaFile = null;
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Pictures/temp.jpg");//注意这里需要和filepaths.xml中配置的一样
            iconPath = mediaFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", mediaFile);
            return contentUri;
        } else {
            return Uri.fromFile(mediaFile);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            MyBus.getInstance().post(iconPath);
            finish();
        }
    }

    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        return BlankPresenter.INSTANCE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}
