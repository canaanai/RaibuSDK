package com.easy.raibu;

/**
 * @author chenp
 * @version 2017-02-23 11:17
 */

public interface LivingListener {
    void onCameraOpenSuccess();
    void onCameraOpenFail();
    void onCameraChanged();
    void onServerConnected();
    void onServerDisconnected();
    void onLivePublishFailed();
    void onNetGood();
    void onNetBad();
}
