package com.example.ai_smile.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by jingbin on 2020/5/21.
 *
 */

public class Constants {

    public static boolean IS_BLUETOOTH_CONNECTED;

    public static final String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator +"camera2demo";

    /**
     * 视频地址
     */
    public static final String SAVE_VIDEO_DIR = BASE_DIR + File.separator + "video" + File.separator;
    /**
     * 图片路径
     */
    public final static String SAVE_PIC_PATH = BASE_DIR + File.separator + "pic" + File.separator;

    /**
     * jpg 后缀
     */
    public static final String PICTURE_IMG_SUFFIX = ".jpg";

    /**
     * video 后缀
     */
    public static final String VIDEO_IMG_SUFFIX = ".mp4";

}
