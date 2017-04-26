package com.laifeng.sopcastsdk.video;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author AleXQ
 * @Date 16/3/15
 */

public class GLSLFileUtils {

	public static String getFileContextFromAssets(Context context, String fileName) {
		String fileContent = "";
		try {
			InputStream is = context.getAssets().open(fileName);
			fileContent = inputStream2String(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileContent;
	}

	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1; ) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

    public static int loadTexture(final Context context, final String name){
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0){

            // Read in the resource
            final Bitmap bitmap = getImageFromAssetsFile(context,name);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0){
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private static Bitmap getImageFromAssetsFile(Context context, String fileName){
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try{
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }
}
