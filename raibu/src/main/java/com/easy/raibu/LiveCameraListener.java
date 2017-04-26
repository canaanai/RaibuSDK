package com.easy.raibu;

import com.laifeng.sopcastsdk.camera.CameraListener;

/**
 * @author chenp
 * @version 2017-04-25 15:16
 */

public interface LiveCameraListener {
    int ERROR_CAMERA_NOT_SUPPORT = CameraListener.CAMERA_NOT_SUPPORT;
    int NERROR_O_CAMERA = CameraListener.NO_CAMERA;
    int ERROR_CAMERA_DISABLED = CameraListener.CAMERA_DISABLED;
    int ERROR_CAMERA_OPEN_FAILED = CameraListener.CAMERA_OPEN_FAILED;

    void onCameraOpenSuccess();

    void onCameraOpenFail(int error);

    void onCameraChange();
}
