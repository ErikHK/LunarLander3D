package net.erikhk.lunarlander3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertbuffer, normbuffer;
    int count = 0;
    float angz=0, angy=0;
    public float width=922f;
    public float height=540f;
    public Terrain terrain;
    public Spaceship spaceship;
    public LandingPoint lp;
    public FuelBar fuelbar;
    public Camera camera;
    public Context c;

    final int vertbuff[] = new int[3];

    public MyGLRenderer(Context c_)
    {
        this.c = c_;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Shader.makeprogram();

        GLES20.glEnableVertexAttribArray(Shader.positionhandle);

        float ratio = width / height;
        Mat4 projectionMatrix = VecMath.frustum(-.1f * ratio, .1f * ratio, -.1f, .1f, 0.2f, 750f);

        Mat4 cam = VecMath.lookAt(0f, 1f, 20f,
                0f, 0f, 0f,
                0.0f, 1.0f, 0.0f);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "projmatrix"), 1, true, makedoublebuffer(projectionMatrix.m));
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true, makedoublebuffer(cam.m));

        spaceship = new Spaceship(c);
        terrain = new Terrain(c);
        lp = new LandingPoint(c, terrain);
        fuelbar = new FuelBar(c);
        camera = new Camera();

        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    //@Override
    public void onSurfaceChanged(GL10 gl, int width_, int height_) {
        width = width_;
        height = height_;
        GLES20.glViewport(0, 0, width_, height_);
    }

    //@Override
    public void onDrawFrame(GL10 gl)
    {
        //GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUniform4f(Shader.colorhandle, 1.0f, 0.0f, MainActivity.phone_ang, 1.0f);
        GLES20.glUniform1f(Shader.anghandle, angz);


        //Draw models
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "drawterrain2"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "drawterrain"), 1);
        terrain.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "drawterrain2"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "drawterrain"), 0);
        spaceship.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_landing_point"), 1);
        lp.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_landing_point"), 0);

        fuelbar.update(spaceship.fuel);
        camera.update(spaceship);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hud"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hudf"), 1);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        fuelbar.DrawModel();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hud"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hudf"), 0);
        
        if(MainActivity.istapping)
            spaceship.isthrusting = true;
        else
            spaceship.isthrusting = false;

        float hh = 0;
        if(spaceship.pos.x >= 0 && spaceship.pos.x < 32 && spaceship.pos.z >= 0 && spaceship.pos.z < 32)
            hh = terrain.getHeight(spaceship.pos.x, spaceship.pos.z);
        if(hh < 0)
            hh = 0;

        float dist = VecMath.DistanceXZ(spaceship.pos, lp.pos);


        if(spaceship.pos.y > hh && dist > 1.0f)
            spaceship.move();
        else if(dist < 1.0f && spaceship.pos.y > lp.pos.y + 2f)
            spaceship.move();

    }

    public FloatBuffer makedoublebuffer(float[] array)
    {

        FloatBuffer doublebuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        doublebuff.put(array).position(0);

        return doublebuff;
    }


    float[] toFloatArray(double[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float)arr[i];
        }
        return ret;
    }


}
