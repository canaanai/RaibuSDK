package com.easy.raibu.effects;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @author chenp
 * @version 2017-03-09 10:17
 */

public class CrayonEffect extends BaseEffect {
    private int mSingleStepOffsetLocation;
    //1.0 - 5.0
    private int mStrengthLocation;

    public CrayonEffect(Context context) {
        super(context, null, "crayon.glsl");
    }

    @Override
    protected void loadOtherParams() {
        super.loadOtherParams();

        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
        //setFloat(mStrengthLocation, 2.0f);
    }

    @Override
    public void prepare() {
        super.prepare();

        //setFloat(mStrengthLocation, 2.0f);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
    }

    @Override
    protected void onSizeInited(int width, int height) {
        super.onSizeInited(width, height);

        setTexelSize(width, height);
    }
}
