package com.easy.raibu.effects;

import android.content.Context;
import android.opengl.GLES20;

import com.laifeng.sopcastsdk.video.GLSLFileUtils;
import com.laifeng.sopcastsdk.video.effect.Effect;

/**
 * @author chenp
 * @version 2017-03-07 15:28
 */

public class BrooklynEffect extends Effect {
    private static final String BEAUTIFY_EFFECT_VERTEX = "shaders/vertexs/vertexshader.glsl";
    private static final String BEAUTIFY_EFFECT_FRAGMENT = "shaders/fragments/brooklyn.glsl";

    private int[] inputTextureHandles = {-1,-1,-1};
    private int[] inputTextureUniformLocations = {-1,-1,-1};
    private int mGLStrengthLocation;
    private Context context;

    public BrooklynEffect(Context context){
        super();
        this.context = context;
        String vertexShader = GLSLFileUtils.getFileContextFromAssets(context, BEAUTIFY_EFFECT_VERTEX);
        String fragmentShader = GLSLFileUtils.getFileContextFromAssets(context, BEAUTIFY_EFFECT_FRAGMENT);
        super.setShader(vertexShader, fragmentShader);
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
    public void prepare() {
        super.prepare();

        setFloat(mGLStrengthLocation, 1.0f);
        runOnDraw(new Runnable(){
            public void run(){
                inputTextureHandles[0] = GLSLFileUtils.loadTexture(context, "shaders/imgs/brooklynCurves1.png");
                inputTextureHandles[1] = GLSLFileUtils.loadTexture(context, "shaders/imgs/filter_map_first.png");
                inputTextureHandles[2] = GLSLFileUtils.loadTexture(context, "shaders/imgs/brooklynCurves2.png");
            }
        });
    }

    @Override
    protected void loadOtherParams() {
        super.loadOtherParams();

        for(int i=0; i < inputTextureUniformLocations.length; i++)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
            mGLStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
    }

    @Override
    public void release() {
        super.release();

        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
            inputTextureHandles[i] = -1;
    }
}
