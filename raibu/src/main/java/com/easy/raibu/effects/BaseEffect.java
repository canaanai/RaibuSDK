package com.easy.raibu.effects;

import android.content.Context;

import com.laifeng.sopcastsdk.video.GLSLFileUtils;
import com.laifeng.sopcastsdk.video.effect.Effect;

/**
 * @author chenp
 * @version 2017-03-09 09:58
 */

public class BaseEffect extends Effect {
    private static final String EFFECT_VERTEX_PATH = "shaders/vertexs/";
    private static final String EFFECT_FRAGMENT_PATH = "shaders/fragments/";

    public BaseEffect(Context context, String vertexShaderName, String fragmentShaderName){
        super();

        String vertexShaderPath = "shaders/vertexs/vertexshader.glsl";
        String fragmentShaderPath = "null/fragmentshader.glsl";

        if (vertexShaderName != null)
            vertexShaderPath = EFFECT_VERTEX_PATH + vertexShaderName;
        if (fragmentShaderName != null)
            fragmentShaderPath = EFFECT_FRAGMENT_PATH + fragmentShaderName;

        String vertexShader = GLSLFileUtils.getFileContextFromAssets(context, vertexShaderPath);
        String fragmentShader = GLSLFileUtils.getFileContextFromAssets(context, fragmentShaderPath);
        super.setShader(vertexShader, fragmentShader);
    }


}
