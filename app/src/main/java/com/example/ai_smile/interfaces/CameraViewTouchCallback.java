package com.example.ai_smile.interfaces;

import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;

public interface CameraViewTouchCallback {

    CameraCharacteristics getCameraCharacteristics() throws CameraAccessException;

    void setCropRegion(Rect region);

    void setRepeatingRequest() throws CameraAccessException;
}
