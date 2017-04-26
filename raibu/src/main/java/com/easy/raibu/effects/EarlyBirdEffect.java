package com.easy.raibu.effects;

import android.content.Context;
import android.opengl.GLES20;

import com.laifeng.sopcastsdk.video.GLSLFileUtils;

/**
 * @author chenp
 * @version 2017-03-09 10:22
 */

public class EarlyBirdEffect extends BaseEffect {
    private int[] inputTextureHandles = {-1,-1,-1,-1,-1};
    private int[] inputTextureUniformLocations = {-1,-1,-1,-1,-1};
    //protected int mGLStrengthLocation;
    private Context context;

    public EarlyBirdEffect(Context context) {
        super(context, null, "earlybird.glsl");

        this.context = context;
    }

    @Override
    protected void onDrawArraysAfter() {
        super.onDrawArraysAfter();

        for(int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != -1; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();

        for(int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != -1; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
            GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
        }
    }

    @Override
    protected void loadOtherParams() {
        super.loadOtherParams();

        for(int i=0; i < inputTextureUniformLocations.length; i++)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
        /*mGLStrengthLocation = GLES20.glGetUniformLocation(getProgram(),
                "strength");*/
    }

    @Override
    public void prepare() {
        super.prepare();

        //setFloat(mGLStrengthLocation, 1.0f);
        runOnDraw(new Runnable(){
            public void run(){
                inputTextureHandles[0] = GLSLFileUtils.loadTexture(context, "shaders/imgs/earlybirdcurves.png");
                inputTextureHandles[1] = GLSLFileUtils.loadTexture(context, "shaders/imgs/earlybirdoverlaymap_new.png");
                inputTextureHandles[2] = GLSLFileUtils.loadTexture(context, "shaders/imgs/vignettemap_new.png");
                inputTextureHandles[3] = GLSLFileUtils.loadTexture(context, "shaders/imgs/earlybirdblowout.png");
                inputTextureHandles[4] = GLSLFileUtils.loadTexture(context, "shaders/imgs/earlybirdmap.png");
            }
        });
    }

    @Override
    public void release() {
        super.release();

        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
            inputTextureHandles[i] = -1;
    }
}
