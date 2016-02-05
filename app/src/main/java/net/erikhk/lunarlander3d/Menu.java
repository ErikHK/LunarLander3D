package net.erikhk.lunarlander3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by kl on 2016-01-31.
 */
public class Menu {

    public Bitmap gameOver, success;
    public Model squareGameOver;
    public Model squareSuccess;

    public Menu(Context c)
    {
        Rect areaRect = new Rect(0,0,(int)MainActivity.swidth, (int)MainActivity.sheight);

        gameOver = drawTextInMiddle("OH NO! YOU CRASHED!");
        squareGameOver = new Model(c, R.raw.plane2_verts, R.raw.plane2_normals, R.raw.plane2_texture, gameOver);


        success = drawTextInMiddle("YEY! YOU DID IT!");
        squareSuccess = new Model(c, R.raw.plane2_verts, R.raw.plane2_normals, R.raw.plane2_texture, success);

    }

    public Bitmap drawTextInMiddle(String text)
    {
        Rect areaRect = new Rect(0,0,(int)MainActivity.swidth, (int)MainActivity.sheight);

        Bitmap bm = Bitmap.createBitmap((int)MainActivity.swidth, (int)MainActivity.sheight, Bitmap.Config.ARGB_8888);

        Canvas cs = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(Color.argb(127, 127, 127, 127));
        cs.drawRect(0, (int)(MainActivity.sheight*.1), MainActivity.swidth, (int)(MainActivity.sheight*.9), p);

        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(MainActivity.sheight/6.5f);

        RectF bounds = new RectF(areaRect);
// measure text width
        bounds.right = p.measureText(text, 0, text.length());
// measure text height
        bounds.bottom = p.descent() - p.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        p.setColor(Color.WHITE);
        cs.drawText(text, bounds.left, bounds.top - p.ascent(), p);

        return bm;

    }

    public void drawGameOver()
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, squareGameOver.textureHandle[0]);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 1);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glDepthMask(false);
        squareGameOver.DrawModel();
        //GLES20.glDepthMask(true);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 0);
    }

    public void drawSuccess()
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, squareSuccess.textureHandle[0]);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 1);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glDepthMask(false);
        squareSuccess.DrawModel();
        //GLES20.glDepthMask(true);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 0);
    }

    public static void drawMainMenu()
    {

    }
}
