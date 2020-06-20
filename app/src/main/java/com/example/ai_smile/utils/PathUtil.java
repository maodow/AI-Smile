package com.example.ai_smile.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PathUtil {

    private static String getPath(String father, String child1, String child2, String child3) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (father != null) {
            path = path
                    + File.separator
                    + father;
            if (child1 != null) {
                path = path
                        + File.separator
                        + child1;
                if (child2 != null) {
                    path = path
                            + File.separator
                            + child2;
                    if (child3 != null) {
                        path = path
                                + File.separator
                                + child2;
                    }
                }
            }
        }

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
//                + File.separator
//                + father
//                + File.separator
//                + child1
//                + File.separator;
        File file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }
        return path;
    }

    public static String getAppPath(String child) {
        return getPath("ccpbox", child, null, null);
    }

    public static String getPhotoPathByDate() {
        Date date = new Date();
        String path = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return getPath("aismile", "photo", path, null);
    }

    /**
     * 获取某个文件目录下的所有文件(按时间倒序)
     * 并取最近两张
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> fileList = getFiles(path, new ArrayList<File>());
        if (fileList.size() > 0) {
            Collections.sort(fileList, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }

        return fileList.size() > 2 ? fileList.subList(0, 2) : fileList;
    }

    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param fileList
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> fileList) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), fileList);
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File createFile(File file) throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent.exists() || parent.mkdirs())
                if (file.createNewFile())
                    return file;
            return null;
        }
        return file;
    }

}
