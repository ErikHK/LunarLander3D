package net.erikhk.lunarlander3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by kl on 2016-01-31.
 */
public class Menu {

    public Bitmap gameOver;
    public Model square;

    public Menu(Context c)
    {

        gameOver = Bitmap.createBitmap(256*4, 256*4, Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(gameOver);

        cs.drawColor(Color.BLUE);
        //Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(40 * 4);
        //cs.drawPaint(p);

        cs.drawText("OH NO! YOU CRASHED!", 40, 40 * 4, p);
        cs.drawCircle(10, 10, 20, p);

        //square = new Model(c, R.raw.spaceship_verts, R.raw.spaceship_normals, R.raw.spaceship_texture,
        //        R.drawable.texture, GLES20.GL_TEXTURE0);
        square = new Model(c, R.raw.plane2_verts, R.raw.plane2_normals, R.raw.plane2_texture, gameOver);

        //square = new Model(c, R.raw.plane_verts, R.raw.plane_normals, R.raw.plane_texture, R.drawable.texture, GLES20.GL_TEXTURE0);
    }

    public void drawGameOver()
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, square.textureHandle[0]);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 1);

        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glDepthMask(false);
        square.DrawModel();
        //GLES20.glDepthMask(true);
        //GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameoverf"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_gameover"), 0);
    }

    public static void drawWin()
    {


    }

    public static void drawMainMenu()
    {

    }
}
