package com.easy.raibu.effects;

import android.opengl.GLES20;

import com.laifeng.sopcastsdk.video.effect.Effect;

import java.util.ArrayList;

/**
 * @author chenp
 * @version 2017-03-03 10:39
 */

public class EffectSet extends Effect {
    private ArrayList<Effect> effects;

    private int textureId = -1;
    private int effectTextureId = -1;

    public EffectSet(){
        effects = new ArrayList<>();
    }

    /**
     * 添加特效
     */
    public void addEffect(Effect effect){
        effects.add(effect);
    }

    @Override
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    @Override
    public int getEffertedTextureId() {
        return effectTextureId;
    }

    @Override
    public void prepare() {
        int size = effects.size();

        if (size > 0){
            Effect firstEffect = effects.get(0);

            firstEffect.setTextureId(textureId);
            firstEffect.prepare();

            int i;
            for (i = 1; i < size; i ++){
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                Effect effect = effects.get(i);

                effect.setTextureId(effects.get(i - 1).getEffertedTextureId(), false);
                effect.prepare();

                if (i < size -1){
                    GLES20.glBindTexture(GLES20.GL_FRAMEBUFFER, 0);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                }
            }

            effectTextureId = effects.get(i - 1).getEffertedTextureId();
        }
    }

    @Override
    public void draw(float[] tex_mtx) {
        for (int i = 0; i < effects.size(); i ++){
            effects.get(i).draw(tex_mtx);
        }
    }

    @Override
    public void release() {
        for (int i = 0; i < effects.size(); i ++){
            effects.get(i).release();
        }

        effects.clear();
    }
}
