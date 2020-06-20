package com.example.ai_smile.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.ai_smile.R;
import com.example.ai_smile.interfaces.CameraCallbacks;
import com.example.ai_smile.utils.Constants;
import com.example.ai_smile.utils.MyBus;
import com.example.ai_smile.widget.OverlayView;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Size;
import org.tensorflow.demo.tracking.MultiBoxTracker;
import org.tensorflow.demo.tracking.MultiBoxTracker.TrackResultListener;
import org.tensorflow.demo.tracking.MultiBoxTracker.TrackedRecognition;
import org.tensorflow.lite.Classifier;
import org.tensorflow.lite.SDK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import butterknife.OnClick;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements CameraCallbacks {

    private static final String TAG = DetectorActivity.class.getName();

    private static final int TF_OD_API_INPUT_SIZE = 300;

    private static final boolean MAINTAIN_ASPECT = false;

    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation;

    private Classifier detector;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;


    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private Matrix frameToPreviewMatrix;
    private MultiBoxTracker tracker;
    private byte[] luminanceCopy;
    private Long lastProcessingTimeMs = 0l;

    private String mImagePath;//拍照图片的存储路径

//    private Handler syncHandler;
//    private HandlerThread syncHandlerThread;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
        setCameraCallbacks(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAudio(R.raw.hint_take_photo);
            }
        }, 2000);
    }

    @OnClick({R.id.iv_take_photo, R.id.iv_reset, R.id.iv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_take_photo:
                takePhoto();
                break;
            case R.id.iv_reset:
                mLayCamera.setVisibility(View.VISIBLE);
                mLayPhoto.setVisibility(View.GONE);
                camera.startPreview();
                break;
            case R.id.iv_ok:
                MyBus.getInstance().post(mImagePath);
                finish();
                break;
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        mLayCamera.setVisibility(View.GONE);
        mLayPhoto.setVisibility(View.VISIBLE);
        mImagePath = imageFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        mIvPhoto.setImageBitmap(bitmap);
    }

    @Override
    public void onCameraError(int errorCode) {

    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public void onResume() {
        super.onResume();
//        syncHandlerThread = new HandlerThread("syncHandlerThread");
//        syncHandlerThread.start();
//        syncHandler = new Handler(syncHandlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
//        syncHandlerThread.quitSafely();
//        try {
//            syncHandlerThread.join();
//            syncHandlerThread = null;
//            syncHandler = null;
//        } catch (final InterruptedException e) {
//            Log.e(TAG, "Exception!", e);
//        }
        super.onPause();
    }


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {   //初始化执行，一次
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;
        detector = SDK.getInstance().getDetector(this, cropSize);

        tracker.setViewSize(frameSize);
        tracker.setCameraId(camera_id);


        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();

        Log.i(TAG, String.format(" debug xxx: %d  %d", rotation, getScreenOrientation()));
        Log.i(TAG, String.format(" debug xxx sensorOrientation " + sensorOrientation));    //wjy
        Log.i(TAG, String.format("Camera orientation relative to screen canvas: %d", sensorOrientation));

        Log.i(TAG, String.format("Initializing at size %dx%d", previewWidth, previewHeight));
        if (rgbFrameBitmap == null) {
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
            croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);
        }


        boolean rotated = sensorOrientation % 180 == 90;

        frameToPreviewMatrix =
                ImageUtils.getTransformationMatrix(
                        previewWidth,
                        previewHeight,
                        (int) ((rotated ? previewHeight : previewWidth)),
                        (int) ((rotated ? previewWidth : previewHeight)),
                        sensorOrientation,
                        MAINTAIN_ASPECT);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        if (trackingView != null) {
            trackingView.addCallback(
                    new OverlayView.DrawCallback() {
                        @Override
                        public void drawCallback(final Canvas canvas) {
                            tracker.draw(canvas);
                        }
                    });
        }

        tracker.registTrackResultListener(new TrackResultListener() {
            @Override
            public void onTrackedObject(TrackedRecognition trackedRecognition) {
                RectF trackedPos = trackedRecognition.location;
                //框脸坐标
                Log.e(TAG, "框脸坐标： "+trackedPos.toString());
            }

            @Override
            public RectF onRectTracked(RectF box) {
                return box;
            }

        });

    }

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        final byte[] originalLuminance = getLuminance();
        tracker.onFrame(previewWidth, previewHeight, getLuminanceStride(), sensorOrientation, originalLuminance, timestamp);
        if (trackingView != null) {
            trackingView.postInvalidate();
        }

        //界面刷新,postInvalidate 在非UI线程中使用

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {

                        long startTime = SystemClock.uptimeMillis();
                        computingDetection = true;
                        Log.i(TAG, "Preparing image " + currTimestamp + " for detection in bg thread.");

                        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
//                        ImageUtils.saveBitmap(rgbFrameBitmap,"test.jpg");

                        if (luminanceCopy == null) {
                            luminanceCopy = new byte[originalLuminance.length];
                        }
                        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
                        readyForNextImage();

                        final Canvas canvas = new Canvas(croppedBitmap);  //croppedBitmap创建以后，通过canvas.drawBitmap获取变换后的bitmap
                        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

                        if (croppedBitmap == null) {
                            return;
                        }
                        // For examining the actual TF input.

                        Log.i(TAG, "iamge 耗时：" + (SystemClock.uptimeMillis() - startTime));

                        Log.i(TAG, "Running detection on image " + currTimestamp);
                        startTime = SystemClock.uptimeMillis();

//                        ImageUtils.saveBitmap(croppedBitmap,"detect.jpg");

                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        Log.i(TAG, "detect 耗时：" + lastProcessingTimeMs);


                        final List<Classifier.Recognition> mappedRecognitions = new LinkedList<Classifier.Recognition>();

                        Classifier.Recognition result = getFaceResult(results);
                        if (result != null) {
                            RectF location = result.getLocation();
                            cropToFrameTransform.mapRect(location);
                            result.setLocation(location);
                            result.setId("1");
                            //result.setConfidence(0.99f);
                            mappedRecognitions.add(result);
                        }

//                        Log.e(TAG, "mappedRecognitions的size :"+mappedRecognitions.size());
                        tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                        if (trackingView != null) {
                            trackingView.postInvalidate();   //界面刷新,postInvalidate 在非UI线程中使用
                        }

                        computingDetection = false;

                    }
                });
    }

    private Classifier.Recognition getFaceResult(List<Classifier.Recognition> results) {
        float area = 0;
        Classifier.Recognition maxRec = null;
        for (Classifier.Recognition rec : results) {
            if (rec.getConfidence() < 0.5f) { //可信度<50%
                continue;
            }
            float tmp = (results.get(0).getLocation().right - results.get(0).getLocation().left) * (results.get(0).getLocation().bottom - results.get(0).getLocation().top);
            if (tmp > area) {
                area = tmp;
                maxRec = rec;
            }
        }
        if (maxRec == null) {
            return null;
        }
        return maxRec;
    }

    private void playAudio(int resId){
        AudioManager audioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), resId);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    private Bitmap rotateBitmap(Bitmap originBitmap, float degree) {
        if (originBitmap == null) {
            return null;
        }
        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);

        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(originBitmap, 0, 0, width, height, frameToPreviewMatrix, false);
        if (newBM.equals(originBitmap)) {
            return newBM;
        }
//        originBitmap.recycle();
        return newBM;
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        if (detector != null) {
            detector.close();
        }
    }

}
