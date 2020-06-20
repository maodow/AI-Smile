package com.example.ai_smile.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ai_smile.R;
import com.example.ai_smile.utils.MyBus;

import java.io.File;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 拍照获取的图片
 */
public class TakepicActivity extends Activity {
    
    private final int PIC_TYPE = 0;
    private final int VIDEO_TYPE = 1;
    private final int RECAPTURE_TYPE = 2;
    @BindView(R.id.takepic_iv)
    ImageView takepicIv;
    @BindView(R.id.takepic_vv)
    VideoView takepicVv;
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.submit_iv)
    ImageView submitIv;

    private String mImagePath;

    private int mediaType;

    private MediaPlayer mediaPlayer;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepic2);
        ButterKnife.bind(this);
        initData();
    }

    public void initData() {
        mImagePath = getIntent().getStringExtra("path");
        //0 拍照  1视频
        mediaType = getIntent().getIntExtra("type", 0);
        if (TextUtils.isEmpty(mImagePath)) {
            finish();
        }
    }


    @OnClick({R.id.back_iv, R.id.submit_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                backEvent();
                break;
            case R.id.submit_iv:
                submitEvent();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadView();
    }

    private void loadView() {
        if (mediaType == PIC_TYPE || mediaType == RECAPTURE_TYPE) {
            takepicIv.setVisibility(View.VISIBLE);
            takepicVv.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions();
            options.centerInside();
            options.placeholder(R.drawable.anim_loading).centerInside();
            Glide.with(this)
                    .load(mImagePath)
                    .apply(options)
                    .into(takepicIv);
        } else if (mediaType == VIDEO_TYPE) {
            takepicIv.setVisibility(View.GONE);
            takepicVv.setVisibility(View.VISIBLE);
            playVideo(mImagePath);
        }
    }

    private void submitEvent() {
        MyBus.getInstance().post(mImagePath);
        finish();
    }

    /**
     * 返回
     */
    private void backEvent() {
        if (mediaType == VIDEO_TYPE) {
            deleteVideoFile();
        }
        finish();
    }


    public void playVideo(final String url) {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                try {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    } else {
                        mediaPlayer.reset();
                    }
                    mediaPlayer.setDataSource(url);

                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                            .OnVideoSizeChangedListener() {
                        @Override
                        public void
                        onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            updateVideoViewSize(mediaPlayer.getVideoWidth(), mediaPlayer
                                    .getVideoHeight());
                        }
                    });
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.setSurface(takepicVv.getHolder().getSurface());
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void stopVideo() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pageOnPause();
    }

    private void pageOnPause() {
        if (mediaType == VIDEO_TYPE) {
            stopVideo();
        }
    }

    private void updateVideoViewSize(float videoWidth, float videoHeight) {


        int viewWidth = takepicVv.getWidth();
        int viewHeight = takepicVv.getHeight();

        double viewRatio = viewWidth * 1.00d / viewHeight;
        double sizeRatio = videoWidth * 1.00d / videoHeight;


        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) takepicVv.getLayoutParams();
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;

        if (viewRatio < sizeRatio) {

            layoutParams.height = viewHeight;
            int width = (int) (viewHeight * 1.00d * videoWidth / videoHeight);
            layoutParams.width = width;

        } else {
            layoutParams.width = viewWidth;
            int height = (int) (viewWidth * 1.00d * videoHeight / videoWidth);
            layoutParams.height = height;
        }
        takepicVv.setLayoutParams(layoutParams);
    }

    /**
     * 删除视频文件相关
     */
    private void deleteVideoFile() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(mImagePath);
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
