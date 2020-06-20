package com.example.ai_smile.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.ai_smile.R;
import com.example.ai_smile.base.BaseActivity;
import com.example.ai_smile.base.BlankPresenter;
import com.example.ai_smile.interfaces.CameraOprCallback;
import com.example.ai_smile.interfaces.CameraResultCallback;
import com.example.ai_smile.utils.Camera2Utils;
import com.example.ai_smile.widget.AutoFitTextureView2;
import com.example.ai_smile.widget.CameraShutterBtn;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Camera2Activity extends BaseActivity implements CameraOprCallback, CameraResultCallback {

    @BindView(R.id.capture_iv)
    CameraShutterBtn captureIv;
    @BindView(R.id.main_texture)
    AutoFitTextureView2 mainTexture;

    Unbinder unbinder;

    private boolean supportCamera = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? true : false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_camera2;
    }

    public void initData() {
        captureIv.setCameraOprCallback(this);
        try {
            Camera2Utils.getInstance().init(this, mainTexture);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (supportCamera) {
            mainTexture.setVisibility(View.VISIBLE);
        } else {
            mainTexture.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.close_iv, R.id.flip_iv, R.id.iv_take_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close_iv:
                finish();
                break;
            case R.id.flip_iv:
                try {
                    Camera2Utils.getInstance().flip();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_take_photo:
                Camera2Utils.getInstance().takePicture();
                break;
        }
    }

    @Override
    public void capturePic() {
        Camera2Utils.getInstance().takePicture();
    }

    @Override
    public void recordVideo() {
        Camera2Utils.getInstance().startRecord();
    }

    @Override
    public void stopRecord(boolean save) {
        Camera2Utils.getInstance().stopRecord(save);
    }

    @Override
    public void getMediaData(int mediatype, String mediaPath) {
        Intent intent = new Intent(Camera2Activity.this, TakepicActivity.class);
        intent.putExtra("path",mediaPath);
        intent.putExtra("type",0);
        startActivity(intent);
    }

    @Override
    public void getNv21Data(byte[] nv21, String uuid, int width, int height) {

    }

    @Override
    public void getVideoData(String mediaPath) {
//        Intent intent = new Intent(Camera2Activity.this, TakepicActivity.class);
//        intent.putExtra("path",mediaPath);
//        intent.putExtra("type",1);
//        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Camera2Utils.getInstance().resume(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Camera2Utils.getInstance().stop();
    }

    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        return BlankPresenter.INSTANCE;
    }

}
