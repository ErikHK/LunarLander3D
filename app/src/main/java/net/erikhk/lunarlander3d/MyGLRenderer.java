package net.erikhk.lunarlander3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

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
        Mat4 projectionMatrix = VecMath.frustum(-.1f * ratio, .1f * ratio, -.1f, .1f, 0.2f, 7.5f);

        Mat4 cam = VecMath.lookAt(0f, 1f, 2f,
                0f, 0f, 0f,
                0.0f, 1.0f, 0.0f);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "projmatrix"), 1, true, makedoublebuffer(projectionMatrix.m));
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true, makedoublebuffer(cam.m));

        spaceship = new Spaceship(c);
        terrain = new Terrain(c);

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

        terrain.DrawModel();
        spaceship.DrawModel();

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
