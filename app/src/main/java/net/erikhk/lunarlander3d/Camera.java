package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;
import android.widget.Space;

/**
 * Created by kl on 2016-01-31.
 */
public class Camera {

    public Camera()
    {


    }

    public static void update(Spaceship s)
    {

        Mat4 cam;

        if(MainActivity.faraway)
            cam = VecMath.lookAt(-20f+16, 35f, 20f+16,
                    16f, 0f, 16f,
                    0.0f, 1.0f, 0.0f);
        else
            cam = VecMath.lookAt(s.pos.x, s.pos.y + 3f, s.pos.z + 4f,
                    s.pos.x, s.pos.y, s.pos.z,
                    0.0f, 1.0f, 0.0f);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true, VecMath.makefloatbuffer(cam.m));

    }


}
