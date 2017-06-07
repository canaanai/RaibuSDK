package com.easy.raibu;

import android.media.AudioFormat;
import android.util.Log;

import com.easy.raibu.ui.KLiveView;
import com.laifeng.sopcastsdk.camera.CameraHolder;
import com.laifeng.sopcastsdk.camera.CameraListener;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.stream.packer.Packer;
import com.laifeng.sopcastsdk.stream.packer.rtmp.RtmpPacker;
import com.laifeng.sopcastsdk.stream.sender.rtmp.RtmpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;
import com.laifeng.sopcastsdk.utils.SopCastLog;
import com.laifeng.sopcastsdk.video.effect.Effect;
import com.laifeng.sopcastsdk.video.effect.NullEffect;

/**
 * @author chenp
 * @version 2017-02-22 15:56
 */

public class LiveEngine {

    public static final String TAG = LiveEngine.class.getSimpleName();

    private RtmpSender mRtmpSender;
    private CameraLivingView livingView;
    //private LivingListener livingListener;
    private LiveOpenListener openListener;
    private LiveConnectionListener connectionListener;
    private LiveCameraListener cameraListener;
    private boolean startAfterCameraOpened = false;
    private CameraConfiguration.Builder cameraBuilder;

    private LiveEngine(CameraLivingView livingView){
        this.livingView = livingView;
    }

    /*public void openLive(){
        livingView.start();
        mRtmpSender.connect();
    }*/

    /**
     * 重连服务器
     */
    public void reconnect(){
        mRtmpSender.connect();
    }

    /**
     * 需在Camera初始化完成后可调用
     */
    public void start(){

        if (CameraHolder.instance().getState() == CameraHolder.State.PREVIEW){
            mRtmpSender.connect();
        }else {
            startAfterCameraOpened = true;
        }
    }

    /**
     * 恢复
     */
    public void resume(){
        livingView.resume();
    }

    /**
     *  暂停
     */
    public void pause(){
        livingView.pause();
    }

    /**
     * 停止
     */
    public void stop(){
        livingView.stop();
    }

    /**
     * 销毁资源
     */
    public void destroy(){
        livingView.release();
    }

    /**
     * 切换摄像头
     */
    public void swithCamera(){
        livingView.switchCamera();
    }

    /**
     * 设置特效
     * @param effect
     */
    public void setEffect(Effect effect){
        if (effect == null)
            livingView.setEffect(new NullEffect(livingView.getContext()));
        else
            livingView.setEffect(effect);
    }

    /*public void setLivingListener(LivingListener livingListener){
        this.livingListener = livingListener;
    }*/
    
    public void setLiveCameraListener(LiveCameraListener liveCameraListener){
        this.cameraListener = liveCameraListener;
    }
    
    public void setLiveConnectionListener(LiveConnectionListener liveConnectionListener){
        this.connectionListener = liveConnectionListener;
    }
    
    public void setLiveOpenListener(LiveOpenListener liveOpenListener){
        this.openListener = liveOpenListener;
    }
    
    public void initLiveView(boolean isLogOpen) {
        SopCastLog.isOpen(isLogOpen);
        livingView.init();
        livingView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                Log.e("LiveEngine", "errorCode:" + error);
                
                if (openListener != null)
                    openListener.liveOpenError(error);
            }

            @Override
            public void startSuccess() {
                Log.d("LiveEngine", "直播开启成功");
                
                if (openListener != null)
                    openListener.liveOpenSuccess();
            }
        });
        //设置预览监听
        livingView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                if (startAfterCameraOpened){
                    start();

                    startAfterCameraOpened = false;
                }

                if (cameraListener != null)
                    cameraListener.onCameraOpenSuccess();
            }

            @Override
            public void onOpenFail(int error) {
                if (cameraListener != null)
                    cameraListener.onCameraOpenFail(error);
            }

            @Override
            public void onCameraChange() {
                if (cameraListener != null)
                    cameraListener.onCameraChange();
            }
        });
    }

    private void initCamera(CameraConfiguration.Builder builder){
        /*CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.PORTRAIT)
                .setFacing(CameraConfiguration.Facing.BACK)
                .setFocusMode(CameraConfiguration.FocusMode.TOUCH);
        cameraConfiguration = cameraBuilder.build();*/
        cameraBuilder = builder;
        livingView.setCameraConfiguration(builder.build());
    }

    private void initVideo(VideoConfiguration configuration){
        /*VideoConfiguration.Builder videoBuilder = new VideoConfiguration.Builder();
        videoBuilder.setSize(outputVideoWidth, outputVideoWidth * cameraConfiguration.height / cameraConfiguration.width).setMime(VideoConfiguration.DEFAULT_MIME)
                .setFps(15).setBps(100, 400).setIfi(2);
        mVideoConfiguration = videoBuilder.build();
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);*/
        livingView.setVideoConfiguration(configuration);
    }

    private void initAudio(AudioConfiguration configuration){
        /*AudioConfiguration.Builder audioBuilder = new AudioConfiguration.Builder();
        audioBuilder.setAec(true).setBps(32, 64).setFrequency(16000).setMime(AudioConfiguration.DEFAULT_MIME).
                setAacProfile(DEFAULT_AAC_PROFILE).setAdts(DEFAULT_ADTS).
                setChannelCount(1).setEncoding(DEFAULT_AUDIO_ENCODING);
        AudioConfiguration audioConfiguration = audioBuilder.build();
        mLFLiveView.setAudioConfiguration(audioConfiguration);*/

        livingView.setAudioConfiguration(configuration);
    }

    private void initPacker(Packer packer){
        livingView.setPacker(packer);
    }

    private void initPacker(AudioConfiguration configuration){
        RtmpPacker packer = new RtmpPacker();
        packer.initAudioParams(configuration.frequency,
                configuration.encoding == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8,
                false);
        livingView.setPacker(packer);
    }

    private void initSender(String url, int videoWidth, int videoHeight, AudioConfiguration configuration){

        mRtmpSender = new RtmpSender();

        mRtmpSender.setAddress(url);
        mRtmpSender.setVideoParams(videoWidth, videoHeight);
        mRtmpSender.setAudioParams(configuration.frequency,
                configuration.encoding == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8,
                false);
        mRtmpSender.setSenderListener(new SenderListener());
        livingView.setSender(mRtmpSender);
    }

    /*public void setOrientation(Orientation orientation){
        CameraConfiguration.Orientation co = (orientation == Orientation.PORTRAIT)
                ? CameraConfiguration.Orientation.PORTRAIT
                : CameraConfiguration.Orientation.LANDSCAPE;

        cameraBuilder = cameraBuilder.setOrientation(co);

        livingView.setCameraConfiguration(cameraBuilder.build());
    }*/

    /*public static class Builder{
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        VideoConfiguration.Builder videoBuilder = new VideoConfiguration.Builder();
        AudioConfiguration.Builder audioBuilder = new AudioConfiguration.Builder();

        String serverUrl;
        int outputWidth = 360;
        //int outputHeight = -1;
        CameraLivingView livingView;

        *//**
         *
         * @param livingView CameraLivingView控件
         * @param serverUrl  服务器地址
         *//*
        public Builder(CameraLivingView livingView, String serverUrl){
            this.livingView = livingView;
            this.serverUrl = serverUrl;
        }

        *//**
         * 设置特效
         * @param effect
         *//*
        public Builder setEffect(Effect effect){

            if (effect != null)
                livingView.setEffect(effect);
            else
                livingView.setEffect(new NullEffect(livingView.getContext()));

            return this;
        }

        *//**
         * 设置横竖屏
         * @param orientation
         * @return
         *//*
        public Builder orientation(CameraConfiguration.Orientation orientation){
            cameraBuilder.setOrientation(orientation);

            return this;
        }

        *//**
         * 设置摄像头类型
         * @param facing
         * @return
         *//*
        public Builder cameraType(CameraConfiguration.Facing facing){
            cameraBuilder.setFacing(facing);

            return this;
        }

        *//**
         * 设置帧率
         * @param fps
         * @return
         *//*
        public Builder fps(int fps){
            cameraBuilder.setFps(fps);
            videoBuilder.setFps(fps);

            return this;
        }

        public Builder ifi(int ifi){
            videoBuilder.setIfi(ifi);

            return this;
        }

        *//**
         * 设置视频输出大小
         * @param width
         * @param height
         * @return
         *//*
        public Builder outputVidioSize(int width, int height){
            *//*this.outputWidth = width;
            this.outputHeight = height;*//*
            videoBuilder.setSize(width, height);

            return this;
        }

        *//**
         * 设置视频输出宽度，高度随摄像头宽高比缩放
         * @param width
         * @return
         *//*
        public Builder outputVideoWidth(int width){
            this.outputWidth = width;

            return this;
        }

        *//**
         * 设置码率
         * @param minBps
         * @param maxBps
         * @return
         *//*
        public Builder bps(int minBps, int maxBps){
            videoBuilder.setBps(minBps, maxBps);

            return this;
        }

        *//**
         * 设置设置自定义摄像头参数
         * @param builder
         * @return
         *//*
        public Builder cameraConfigurationBuilder(CameraConfiguration.Builder builder){
            this.cameraBuilder = builder;

            return this;
        }

        *//**
         * 设置自定义视频参数
         * @param builder
         * @return
         *//*
        public Builder videoConfigrationBuilder(VideoConfiguration.Builder builder){
            this.videoBuilder = builder;
            outputWidth = -1;

            return this;
        }

        *//**
         * 设置自定义音频参数
         *//*
        public Builder audioConfigurationBuilder(AudioConfiguration.Builder builder){
            this.audioBuilder = builder;

            return this;
        }

        public LiveEngine builder(){
            LiveEngine component = new LiveEngine(livingView);
            CameraConfiguration cameraConfiguration = cameraBuilder.build();
            VideoConfiguration videoConfiguration;
            AudioConfiguration audioConfiguration = audioBuilder.build();

            if (outputWidth > 0)
                videoBuilder.setSize(outputWidth, outputWidth * cameraConfiguration.height / cameraConfiguration.width);

            videoConfiguration = videoBuilder.build();

            component.initLiveView(true);
            component.initCamera(cameraConfiguration);
            component.initVideo(videoConfiguration);
            component.initAudio(audioConfiguration);
            component.initPacker(audioConfiguration);
            component.initSender(serverUrl, videoConfiguration.width, videoConfiguration.height, audioConfiguration);

            return component;
        }
    }*/

    public static class Builder{

        String serverUrl;
        int outputWidth = VideoConfiguration.DEFAULT_WIDTH;
        int outputHeight = VideoConfiguration.DEFAULT_HEIGHT;
        int outFps = VideoConfiguration.DEFAULT_FPS;
        int previewWidth = CameraConfiguration.DEFAULT_WIDTH;
        int previewHeight = CameraConfiguration.DEFAULT_HEIGHT;
        int previewFps = CameraConfiguration.DEFAULT_FPS;
        int iFrameInterval = 1;
        int minVideoBps = VideoConfiguration.DEFAULT_MIN_BPS;
        int maxVideoBps = VideoConfiguration.DEFAULT_MAX_BPS;
        boolean ace = true;
        AudioFrequency audioFrequency = AudioFrequency.FREQUENCY_16000HZ;
        AudioEncoding audioEncoding = AudioEncoding.ENCODING_PCM_16BIT;
        Orientation orientation = Orientation.PORTRAIT;
        CameraType cameraType = CameraType.BACK;
        Effect effect;
        KLiveView livingView;
        LiveOpenListener openListener;
        LiveConnectionListener connectionListener;
        LiveCameraListener cameraListener;
        boolean logEnable = false;
        boolean isOutVideoSizeLocked = false;

        /**
         *
         * @param livingView CameraLivingView控件
         * @param serverUrl  服务器地址
         */
        public Builder(KLiveView livingView, String serverUrl){
            this.livingView = livingView;
            this.serverUrl = serverUrl;
        }

        public Builder(KLiveView livingView, String serverUrl, SimpleLiveListener liveListener){
            this.livingView = livingView;
            this.serverUrl = serverUrl;
            this.openListener = liveListener;
            this.connectionListener = liveListener;
            this.cameraListener = liveListener;
        }

        /**
         * 设置特效
         * @param effect
         */
        public Builder setEffect(Effect effect){
            this.effect = effect;

            return this;
        }

        /**
         * 设置横竖屏
         * @param orientation
         * @return
         */
        public Builder orientation(Orientation orientation){
            this.orientation = orientation;

            return this;
        }

        /**
         * 设置摄像头类型
         * @param cameraType
         * @return
         */
        public Builder cameraType(CameraType cameraType){
            this.cameraType = cameraType;

            return this;
        }

        /**
         * 设置帧率
         * @param fps
         * @return
         */
        public Builder previewFps(int fps){
            this.previewFps = fps;

            return this;
        }

        /**
         * 输出帧率
         * @param fps
         * @return
         */
        public Builder outFps(int fps){
            this.outFps = fps;

            return this;
        }

        /**
         * I帧间隔
         * @param ifi
         * @return
         */
        public Builder iFrameInterval(int ifi){
            this.iFrameInterval = ifi;

            return this;
        }

        /**
         * 预览视频大小
         * @param width
         * @param height
         * @return
         */
        public Builder previewVideoSize(int width, int height){
            this.previewWidth = width;
            this.previewHeight = height;

            return this;
        }

        /**
         * 设置视频输出大小
         * @param width
         * @param height
         * @return
         */
        public Builder outputVidioSize(int width, int height){
            this.outputWidth = width;
            this.outputHeight = height;

            return this;
        }

        /**
         * 设置码率
         * @param minBps
         * @param maxBps
         * @return
         */
        public Builder bps(int minBps, int maxBps){
            this.minVideoBps = minBps;
            this.maxVideoBps = maxBps;

            return this;
        }

        /**
         * 是否回声消除
         * @param ace
         * @return
         */
        public Builder isAce(boolean ace){
            this.ace = ace;
            
            return this;
        }

        /**
         * 音频采样率
         * @param frequency
         * @return
         */
        public Builder audioFrequency(AudioFrequency frequency){
            this.audioFrequency = frequency;
            
            return this;
        }

        /**
         * @param encoding 音频编码类型
         * @return
         */
        public Builder audioEncoding(AudioEncoding encoding){
            this.audioEncoding = encoding;
            
            return this;
        }

        /**
         *
         * @param liveCameraListener 相机事件监听
         * @return
         */
        public Builder cameraListener(LiveCameraListener liveCameraListener){
            this.cameraListener = liveCameraListener;
            
            return this;
        }

        /**
         *
         * @param openListener 直播开启事件监听
         * @return
         */
        public Builder openListener(LiveOpenListener openListener){
            this.openListener = openListener;
            
            return this;
        }

        /**
         *
         * @param connectionListener 网络连接事件监听
         * @return
         */
        public Builder connectionListener(LiveConnectionListener connectionListener){
            this.connectionListener = connectionListener;
            
            return this;
        }

        /**
         *
         * @param logEnable 日志输出与否
         * @return
         */
        public Builder isLog(boolean logEnable){
            this.logEnable = logEnable;
            
            return this;
        }

        public Builder isVideoSizeLocked(boolean isLocked){
            this.isOutVideoSizeLocked = isLocked;

            return this;
        }
        
        public LiveEngine builder(){
            CameraConfiguration.Orientation co = (orientation == Orientation.PORTRAIT)
                    ? CameraConfiguration.Orientation.PORTRAIT
                    : CameraConfiguration.Orientation.LANDSCAPE;
            CameraConfiguration.Facing facing = (cameraType == CameraType.BACK)
                    ? CameraConfiguration.Facing.BACK
                    : CameraConfiguration.Facing.FRONT;
            int ae = (audioEncoding == AudioEncoding.ENCODING_PCM_16BIT)
                    ? AudioFormat.ENCODING_PCM_16BIT
                    : AudioFormat.ENCODING_PCM_8BIT;
            int frequency = (audioFrequency == AudioFrequency.FREQUENCY_16000HZ) ? 8000 : 16000;
            int mOutputWidth;
            int mOutputHeight;

            if (!isOutVideoSizeLocked && orientation == Orientation.LANDSCAPE){
                mOutputWidth = outputHeight;
                mOutputHeight = outputWidth;
            }else {
                mOutputWidth = outputWidth;
                mOutputHeight = outputHeight;
            }

            LiveEngine component = new LiveEngine(livingView);

            CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder()
                    .setPreview(previewHeight, previewWidth)
                    .setFps(previewFps)
                    .setFacing(facing)
                    .setOrientation(co);
            VideoConfiguration videoConfiguration = new VideoConfiguration.Builder()
                    .setBps(minVideoBps, maxVideoBps)
                    .setFps(outFps)
                    .setIfi(iFrameInterval)
                    .setSize(mOutputWidth, mOutputHeight)
                    .build();
            AudioConfiguration audioConfiguration = new AudioConfiguration.Builder()
                    .setAec(ace)
                    .setEncoding(ae)
                    .setFrequency(frequency)
                    .build();

            component.initLiveView(logEnable);
            component.initCamera(cameraBuilder);
            component.initVideo(videoConfiguration);
            component.initAudio(audioConfiguration);
            component.initPacker(audioConfiguration);
            component.initSender(serverUrl, mOutputWidth, mOutputHeight, audioConfiguration);
            component.setEffect(effect);
            component.setLiveCameraListener(cameraListener);
            component.setLiveConnectionListener(connectionListener);
            component.setLiveOpenListener(openListener);

            return component;
        }
    }

    public enum Orientation {
        LANDSCAPE,
        PORTRAIT
    }

    public enum CameraType{
        FRONT,
        BACK
    }
    
    public enum AudioEncoding{
        ENCODING_PCM_16BIT,
        ENCODING_PCM_8BIT
    }
    
    public enum AudioFrequency{
        FREQUENCY_8000HZ,
        FREQUENCY_16000HZ
    }
    
    

    private class SenderListener implements RtmpSender.OnSenderListener{

        @Override
        public void onConnecting() {
            if (connectionListener != null)
                connectionListener.onConnecting();
        }

        @Override
        public void onConnected() {

            if (connectionListener != null)
                connectionListener.onConnected();

            livingView.start();
        }

        @Override
        public void onDisConnected() {
            if (connectionListener != null)
                connectionListener.onDisConnected();

            livingView.stop();
        }

        @Override
        public void onPublishFail() {
            if (connectionListener != null)
                connectionListener.onPublishFail();
        }

        @Override
        public void onNetGood() {
            if (connectionListener != null)
                connectionListener.onNetGood();
        }

        @Override
        public void onNetBad() {
            if (connectionListener != null)
                connectionListener.onNetBad();
        }
    }
}
