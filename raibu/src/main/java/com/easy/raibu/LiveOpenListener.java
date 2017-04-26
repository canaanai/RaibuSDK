package com.easy.raibu;

import com.laifeng.sopcastsdk.ui.CameraLivingView;

/**
 * @author chenp
 * @version 2017-04-25 15:18
 */

public interface LiveOpenListener {
    int NO_ERROR = CameraLivingView.NO_ERROR;
    int VIDEO_TYPE_ERROR = CameraLivingView.VIDEO_TYPE_ERROR;
    int AUDIO_TYPE_ERROR = CameraLivingView.AUDIO_TYPE_ERROR;
    int VIDEO_CONFIGURATION_ERROR = CameraLivingView.VIDEO_CONFIGURATION_ERROR;
    int AUDIO_CONFIGURATION_ERROR = CameraLivingView.AUDIO_CONFIGURATION_ERROR;
    int CAMERA_ERROR = CameraLivingView.CAMERA_ERROR;
    int AUDIO_ERROR = CameraLivingView.AUDIO_ERROR;
    int AUDIO_AEC_ERROR = CameraLivingView.AUDIO_AEC_ERROR;
    int SDK_VERSION_ERROR = CameraLivingView.SDK_VERSION_ERROR;

    void liveOpenError(int error);
    void liveOpenSuccess();
}
