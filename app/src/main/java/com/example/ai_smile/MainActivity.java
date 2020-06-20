package com.example.ai_smile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.example.ai_smile.activity.Camera2Activity;
import com.example.ai_smile.activity.Camera3Activity;
import com.example.ai_smile.activity.DetectorActivity;
import com.example.ai_smile.base.BaseActivity;
import com.example.ai_smile.bluetooth.BluetoothActivity;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.http.Injection;
import com.example.ai_smile.receiver.BluetoothStateBroadcastReceive;
import com.example.ai_smile.test.FaceTestContract;
import com.example.ai_smile.test.FaceTestPresenter;
import com.example.ai_smile.test.TestInfoActivity;
import com.example.ai_smile.test.TestRecordListActivity;
import com.example.ai_smile.utils.ActivityStackManager;
import com.example.ai_smile.utils.AddressUtil;
import com.example.ai_smile.utils.Constants;
import com.example.ai_smile.utils.MyBus;
import com.example.ai_smile.utils.SpUtils;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity<FaceTestContract.View, FaceTestContract.Presenter> implements FaceTestContract.View {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.iv_face)
    CircleImageView mIvFace;

    private BluetoothStateBroadcastReceive mReceive;
    private BluetoothAdapter mBluetoothAdapter; //蓝牙适配器BluetoothAdapter

    private String iconPath = "";
    private static final int TAKEPHOTO_REQUEST = 196;


    @NonNull
    @Override
    public FaceTestPresenter createPresenter() {
        return new FaceTestPresenter(Injection.provideFaceTestUseCase());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyBus.getInstance().register(this);
    }

    @OnClick({R.id.tv_exit, R.id.iv_check_record, R.id.btn_take_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                Intent intent_exit = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_exit);
                SpUtils.getInstance().remove(SpUtils.ISLOGIN);
                ActivityStackManager.getActivityStackManager().finishAllActivity();
                break;
            case R.id.iv_check_record:
                Intent intent_record = new Intent(MainActivity.this, TestRecordListActivity.class);
                startActivity(intent_record);
                break;
            case R.id.btn_take_photo:
                if (isSupported()) {
                    boolean isOpened = mBluetoothAdapter.enable();
                    if (isOpened) {
                        if (Constants.IS_BLUETOOTH_CONNECTED) {
                            takePhoto();
                            //播放声音
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    playAudio(R.raw.hint_take_photo);
                                }
                            }, 2000);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, BluetoothActivity.class);
                            startActivity(intent);
                        }

                    } else {
//                        Toast.makeText(MainActivity.this, "蓝牙状态：关闭", Toast.LENGTH_SHORT).show();
                    }
                    registerBluetoothReceiver();
                }
                break;
            default:
                break;
        }
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
            return Uri.fromFile(mediaFile);//或者 Uri.isPaise("file://"+file.toString()

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            displayHeadPic(iconPath);
        }
    }

    private void displayHeadPic(String imgPath) {
        Bitmap photo_bitmap = BitmapFactory.decodeFile(imgPath);
        if (null != photo_bitmap) {
            mIvFace.setImageBitmap(photo_bitmap);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestFaceTest(imgPath);
                }
            }, 1000);
        }
    }

    @Subscribe
    public void refreshHeadPic(String imagePath) { // 刷新头像
        Bitmap photo_bitmap = BitmapFactory.decodeFile(imagePath);
        if (null != photo_bitmap) {
            mIvFace.setImageBitmap(photo_bitmap);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestFaceTest(imagePath);
                }
            }, 1000);
        }
    }

    private void requestFaceTest(String mFaceFilePath) {
        if (!mFaceFilePath.isEmpty()) {
            getPresenter().faceTest(AddressUtil.getMacAddr(this), mFaceFilePath);
        }
    }

    @Override
    public void onFaceTestSuccess(HttpRespose<List<TestResultBean>> testResults) {
        Intent intent = new Intent(MainActivity.this, TestInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("testResultList", (Serializable) testResults.getResult());
        intent.putExtra("testResultData", bundle);
        startActivity(intent);
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    private void registerBluetoothReceiver() {
        if (mReceive == null) {
            mReceive = new BluetoothStateBroadcastReceive();
        }
        IntentFilter intentFilter = new IntentFilter();
        //监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        registerReceiver(mReceive, intentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown : " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.d(TAG, "KeyEvent.KEYCODE_VOLUME_UP");
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.d(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN");
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断是否设备是否支持蓝牙
     *
     * @return 是否支持
     */
    private boolean isSupported() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "isSupported: 设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime < 2000) {
            super.onBackPressed();
            System.exit(0);
        } else {
            showToast(getString(R.string.str_hint_quit_app));
            firstTime = System.currentTimeMillis();
        }
    }

    private void unregisterBluetoothReceiver() {
        if (mReceive != null) {
            unregisterReceiver(mReceive);
            mReceive = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyBus.getInstance().unregister(this);
        unregisterBluetoothReceiver();
    }

}
