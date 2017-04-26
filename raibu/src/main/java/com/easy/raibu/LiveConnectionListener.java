package com.easy.raibu;

/**
 * @author chenp
 * @version 2017-04-25 15:19
 */

public interface LiveConnectionListener {
    void onConnecting();
    void onConnected();
    void onDisConnected();
    void onPublishFail();
    void onNetGood();
    void onNetBad();
}
