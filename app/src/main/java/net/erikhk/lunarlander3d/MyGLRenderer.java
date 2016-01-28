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

    final int vertbuff[] = new int[3];


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Shader.makeprogram();

        GLES20.glEnableVertexAttribArray(Shader.positionhandle);

        float ratio = width / height;
        Mat4 projectionMatrix = VecMath.frustum(-.1f * ratio, .1f * ratio, -.1f, .1f, 0.2f, 7.5f);
        //Mat4 projectionMatrix = VecMath.S(1, ratio, 1);
        //Mat4 projectionMatrix = VecMath.IdentityMatrix();

        //Mat4 cam = VecMath.lookAt(0f, 1f, 1f,
        //        0f, 0f, 0f,
        //        0.0f, 1.0f, 0.0f);

        Mat4 cam = VecMath.IdentityMatrix();

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "projmatrix"), 1, true, makedoublebuffer(projectionMatrix.m));
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true, makedoublebuffer(cam.m));

        float[] verts =
                {
                        0.0f, 1f, 0.0f,
                        0.0f, 0.0f, 0.0f,
                        1f, 1f, 0.0f
                };



        //double[] verts2 = ObjFiles.finVerts;

        //vertbuffer = makedoublebuffer(toFloatArray(verts2));

        //vertbuffer = makedoublebuffer(verts);
        vertbuffer = makedoublebuffer(MainActivity.spaceship_verts);
        normbuffer = makedoublebuffer(MainActivity.spaceship_normals);


        GLES20.glGenBuffers(3, vertbuff, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertbuff[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuffer.capacity() * 4, vertbuffer, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertbuff[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuffer.capacity() * 4, normbuffer, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);



        //GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, );

        GLES20.glClearColor(1f, 1f, 1f, 1f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //gl.glDisable(gl.GL_CULL_FACE);
        //gl.glEnable(gl.GL_BLEND);
        //gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

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

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertbuff[0]);
        GLES20.glEnableVertexAttribArray(Shader.positionhandle);
        GLES20.glVertexAttribPointer(Shader.positionhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, MainActivity.spaceshipNumVerts);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertbuff[1]);
        GLES20.glEnableVertexAttribArray(Shader.normalhandle);
        GLES20.glVertexAttribPointer(Shader.normalhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        //GLES20.glVertexAttribPointer(Shader.positionhandle, 3, GLES20.GL_FLOAT, false, 0, vertbuffer);
        //GLES20.glVertexAttribPointer(Shader.normalhandle, 3, GLES20.GL_FLOAT, true, 0, normbuffer);

        Vec3 n = new Vec3(0,-1,0);
        Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, MainActivity.init_phone_n);
        //Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, n);
        Mat4 rot = VecMath.Mult(VecMath.T(0,0,-1f), VecMath.ArbRotate(VecMath.Normalize(cross), MainActivity.phone_ang));
        //Mat4 rot = VecMath.ArbRotate(VecMath.Normalize(MainActivity.phone_n), MainActivity.phone_ang);
        //Mat4 rot = VecMath.ArbRotate(VecMath.Normalize(cross), 0);
        //Mat4 rot = VecMath.ArbRotate(new Vec3(0,.7f,.7f), 0);
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makedoublebuffer(rot.m));

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, MainActivity.spaceshipNumVerts);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, MainActivity.spaceshipNumVerts, GLES20.GL_UNSIGNED_INT,0);

        angz+=.01;
        angy+=.02;
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
