package com.easy.raibu.effects;

import android.content.Context;
import android.opengl.GLES20;

import com.laifeng.sopcastsdk.video.GLSLFileUtils;
import com.laifeng.sopcastsdk.video.effect.Effect;

/**
 * @author chenp
 * @version 2017-02-28 11:17
 */

public class BeautifyEffect extends Effect {
    private static final String BEAUTIFY_EFFECT_VERTEX = "shaders/vertexs/vertexshader.glsl";
    private static final String BEAUTIFY_EFFECT_FRAGMENT = "shaders/fragments/beautiful.glsl";

    private int mSingleStepOffsetLocation;
    private int mParamsLocation;

    public BeautifyEffect(Context context){
        super();
        String vertexShader = GLSLFileUtils.getFileContextFromAssets(context, BEAUTIFY_EFFECT_VERTEX);
        String fragmentShader = GLSLFileUtils.getFileContextFromAssets(context, BEAUTIFY_EFFECT_FRAGMENT);
        super.setShader(vertexShader, fragmentShader);
    }

    @Override
    protected void loadOtherParams() {
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mParamsLocation = GLES20.glGetUniformLocation(getProgram(), "params");

        setBeautifyLevel(1);
    }

    @Override
    protected void onSizeInited(int width, int height) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {2.0f / width, 2.0f / height});
    }

    public void setBeautifyLevel(int level){
        switch (level) {
            case 0:
                setFloat(mParamsLocation, 1.0f);
                break;
            case 1:
                setFloat(mParamsLocation, 0.8f);
                break;
            case 2:
                setFloat(mParamsLocation,0.6f);
                break;
            case 3:
                setFloat(mParamsLocation, 0.4f);
                break;
            case 4:
                setFloat(mParamsLocation,0.33f);
                break;
            default:
                break;
        }
    }
}
