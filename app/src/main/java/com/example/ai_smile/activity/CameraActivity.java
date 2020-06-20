package com.example.ai_smile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.ai_smile.R;
import com.example.ai_smile.interfaces.CameraCallbacks;
import com.example.ai_smile.utils.CameraUtils;
import com.example.ai_smile.utils.PathUtil;
import com.example.ai_smile.widget.AutoFitTextureView;
import com.example.ai_smile.widget.OverlayView;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Size;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class CameraActivity extends Activity implements Camera.PreviewCallback {

    @BindView(R.id.texture)
    AutoFitTextureView textureView;
    @BindView(R.id.tracking_overlay)
    OverlayView trackingView;
    @BindView(R.id.lay_photo)
    LinearLayout mLayPhoto;
    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.ll_camera)
    LinearLayout mLayCamera;

    private static final String TAG = CameraActivity.class.getName();

    private Handler handler;
    private HandlerThread handlerThread;
    private boolean isProcessingFrame = false;

    private byte[][] yuvBytes = new byte[3][]; //YUV  颜色编码方法。常使用在各个视频处理组件中。 YUV在对照片或视频编码
    private int[] rgbBytes = null;
    private int yRowStride;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    protected Camera camera;
    protected static int camera_id = CameraInfo.CAMERA_FACING_FRONT;
    protected Size screenSize;
    protected Size previewSize;
    protected Size frameSize;
    protected int cameraRotation = 0;
    protected float previewScale = 0;

    protected CameraConfig mCameraConfig;
    protected CameraCallbacks mCameraCallbacks;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        initCamera();
        ButterKnife.bind(this);
    }

    private void initCamera(){
        mCameraConfig = new CameraConfig()
                .getBuilder(CameraActivity.this)
//                .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)//注意： 设置FRONT_FACING_CAMERA时，售货机报ERROR_CAMERA_OPEN_FAILED
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setCameraFocus(CameraFocus.AUTO)
                .setImageRotation(cameraRotation)
                .setPhotoSavePath(PathUtil.getPhotoPathByDate())
                //.setImageFile(getPhotoPath(CameraImageFormat.FORMAT_JPEG, this))
                .build();
    }

    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }

    protected byte[] getLuminance() {
        return yuvBytes[0];
    }

    /**
     * Callback for android.hardware.Camera API
     */
    @SuppressLint("NewApi")
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (isProcessingFrame) {
            //Log.w(TAG,"Dropping frame!");
            return;
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];

                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), cameraRotation);
            }
        } catch (final Exception e) {
            Log.e(TAG, "Exception!", e);
            return;
        }

        isProcessingFrame = true;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                new Runnable() {
                    @Override
                    public void run() {
                        ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                    }
                };

        postInferenceCallback =
                new Runnable() {
                    @Override
                    public void run() {
                        camera.addCallbackBuffer(bytes);
                        isProcessingFrame = false;
                    }
                };
        processImage();
        Log.i(TAG, "debug onPreviewFrame");
    }

    /**
     *  拍照
     */
    protected void takePhoto(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] bytes, Camera camera) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Convert byte array to bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        //Rotate the bitmap
                        Bitmap rotatedBitmap;
                        if (mCameraConfig.getImageRotation() != cameraRotation) {
                            rotatedBitmap = CameraUtils.rotateBitmap(bitmap, cameraRotation);
                            //noinspection UnusedAssignment
                            bitmap = null;
                        } else {
                            rotatedBitmap = bitmap;
                        }

                        mCameraConfig.setImageFile(mCameraConfig.getmPhotoSavePath()
                                + File.separator
                                + mCameraConfig.getPhotoName());
                        //Save image to the file.
                        if (CameraUtils.saveImageFromFile(rotatedBitmap,
                                mCameraConfig.getImageFile(),
                                mCameraConfig.getImageFormat())) {

                            //Post image file to the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mCameraCallbacks.onImageCapture(mCameraConfig.getImageFile());
                                }
                            });

                        } else {
                            //Post error to the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mCameraCallbacks.onCameraError(CameraError.ERROR_IMAGE_WRITE_FAILED);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    protected void setCameraCallbacks(CameraCallbacks cameraCallbacks){
        this.mCameraCallbacks = cameraCallbacks;
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { //硬件加速

        @Override
        public void onSurfaceTextureAvailable(
            final SurfaceTexture texture, final int width, final int height) {
            WindowManager wm = (WindowManager) CameraActivity.this.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenSize = new Size(dm.widthPixels, dm.heightPixels);
            camera_id = getCameraId();
            try {
                camera = Camera.open(camera_id);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                if (camera == null) {
                    camera_id = (camera_id == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
                    try {
                        camera = Camera.open(camera_id);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (camera == null) {
                Toast.makeText(CameraActivity.this, "找不到摄像头", Toast.LENGTH_LONG).show();
                CameraActivity.this.finish();
                return;
            }
            if (camera_id == CameraInfo.CAMERA_FACING_FRONT) {
                cameraRotation = 270;
            } else if (camera_id == CameraInfo.CAMERA_FACING_BACK) {
                cameraRotation = 90;
            }
            if (cameraRotation - getScreenOrientation() < 0) {
                cameraRotation = Math.abs(cameraRotation - getScreenOrientation());
            }

            Log.i(TAG, "camera Orientation:" + getScreenOrientation() + " camera:" + cameraRotation);

            try {
                if (previewSize == null) {
                    Camera.Parameters parameters = camera.getParameters();

                    Log.i(TAG, "picture==================");
                    List<Size> pictureSizes = getSupportedSizes(parameters.getSupportedPictureSizes(), 0.75f);
                    Log.i(TAG, "preview==================");
                    List<Size> supportSizes = getSupportedSizes(parameters.getSupportedPreviewSizes(), 0.75f);
                    previewSize = getMaxSize(supportSizes, 600, 1900);
                    Size maxSize = getMaxSize(pictureSizes, 1900, 2600);

                    parameters.setPictureSize(maxSize.getWidth(), maxSize.getHeight());
                    parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());

                    int displayOrientation = 90;

                    Log.i(TAG, "camera   preview:" + previewSize.getWidth() + ":" + previewSize.getHeight() + "  capture:" + maxSize.getWidth() + ":" + maxSize.getHeight());
                    try {
                        camera.setParameters(parameters);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        previewSize = getMaxSize(supportSizes, 600, 1900);
                        maxSize = getMaxSize(pictureSizes, 1900, 2500);
                        parameters.setPictureSize(maxSize.getWidth(), maxSize.getHeight());
                        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
                        camera.setParameters(parameters);
                    }

                    camera.setDisplayOrientation(displayOrientation);

                    previewScale = calculatePreviewScale();
                    Log.i(TAG, "camera  screen:" + screenSize.getWidth() + ":" + screenSize.getHeight() + "  scale:" + previewScale + "  preview:" + previewSize.getWidth() + ":" + previewSize.getHeight() + "  capture:" + maxSize.getWidth() + ":" + maxSize.getHeight());


                    ViewGroup.LayoutParams lp = textureView.getLayoutParams();
                    lp.width = (int) (previewSize.getHeight() * previewScale);
                    lp.height = (int) (previewScale * previewSize.getWidth());
                    frameSize = new Size(lp.width, lp.height);
                    textureView.setLayoutParams(lp);

                    if (trackingView != null) {
                        lp = trackingView.getLayoutParams();
                        lp.width = (int) (previewSize.getHeight() * previewScale);
                        lp.height = (int) (previewScale * previewSize.getWidth());
                        trackingView.setLayoutParams(lp);
                    }

                    camera.setPreviewTexture(texture);
                }
            } catch (IOException exception) {
                Log.e(TAG, exception.getMessage(), exception);
                camera.release();
            }

            camera.setPreviewCallbackWithBuffer(CameraActivity.this);
            Camera.Size s = camera.getParameters().getPreviewSize();
            camera.addCallbackBuffer(new byte[ImageUtils.getYUVByteSize(s.height, s.width)]);

            textureView.setAspectRatio(0, 0);

            camera.startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(
                final SurfaceTexture texture, final int width, final int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(final SurfaceTexture texture) {
        }
    };

    protected int getCameraTakeOrientation() {
        int orientation = cameraRotation - getScreenOrientation();

        return orientation;
    }

    private float calculatePreviewScale() {
        float scale = 0;
        scale = screenSize.getWidth() / (float) previewSize.getHeight();
        if (screenSize.getHeight() / (float) previewSize.getWidth() < scale) {
            return screenSize.getHeight() / (float) previewSize.getWidth();
        }
        return scale;
    }

    protected int getCameraId() {
        CameraInfo ci = new CameraInfo();
        int num = Camera.getNumberOfCameras();
        for (int i = 0; i < num; i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing != CameraInfo.CAMERA_FACING_FRONT)  //选择摄像头
                return i;
        }
        return CameraInfo.CAMERA_FACING_BACK; // No camera found
    }

    private List<Size> getSupportedSizes(List<Camera.Size> supportedSizes, float rate) {
        List<Size> list = new ArrayList<Size>();
        for (Camera.Size size : supportedSizes) {
            int width = size.width;
            int height = size.height;
            if (Math.abs((height / (float) width) - rate) > 0.1) {
                Log.i(TAG, "size:================skip:" + width + ":" + height + "   " + (height / (float) width));
                continue;
            }
            list.add(new Size(width, height));
            Log.i(TAG, "size:================:::" + width + ":" + height + "   " + (height / (float) width));
        }
        Collections.sort(list, new Comparator<Size>() {
            @Override
            public int compare(Size s1, Size s2) {
                if (s1.getHeight() < s2.getHeight()) {
                    return 1;
                } else if (s1.getHeight() > s2.getHeight()) {
                    return -1;
                } else {
                    if ((s1.getHeight() / (float) s1.getWidth() < s2.getHeight() / (float) s2.getWidth())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });
        return list;
    }

    private Size getMaxSize(List<Size> list, int minWidth, int maxWidth) {
        Size size = null;
        List<Size> sizes = new ArrayList<Size>();
        for (Size s : list) {
            Log.i(TAG, "cameraSize  " + s.getWidth() + ":" + s.getHeight() + "  rate:" + (s.getHeight() / (float) s.getWidth()));
            int height = s.getHeight();
            if (height < minWidth || height > maxWidth) {
                continue;
            }
            sizes.add(s);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        if (sizes.size() == 0) {
            size = list.get(0);
        } else {
            size = sizes.get(0);
        }
        Log.i(TAG, "getMaxSize finally  camera=" + size.getHeight() + ":" + size.getWidth() + "  rate=" + size.getHeight() / (float) size.getWidth());
        return size;
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(final Size lhs, final Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (textureView.isAvailable()) {
            camera.startPreview();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
        Log.d(TAG, "onPause " + this);

        if (!isFinishing()) {
            Log.d(TAG, "Requesting finish");
            finish();
        }

        stopCamera();

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Log.e(TAG, "Exception!", e);
        }

        super.onPause();
    }

    @Override
    public synchronized void onDestroy() {
        Log.d(TAG, "onDestroy " + this);
        super.onDestroy();
        if (rgbBytes != null) {
            rgbBytes = null;
        }
        if (yuvBytes != null) {
            yuvBytes = null;
        }
    }

    protected void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    protected void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

}
