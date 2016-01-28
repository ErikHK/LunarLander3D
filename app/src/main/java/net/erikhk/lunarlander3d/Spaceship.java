package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by erikhk on 28/1/2016.
 */
public class Spaceship {

    Model m;


    public Spaceship(Context c)
    {
        m = new Model(c, R.raw.spaceship_verts, R.raw.spaceship_normals);
    }

    public void DrawModel()
    {

        Vec3 n = new Vec3(0,-1,0);
        Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, MainActivity.init_phone_n);
        Mat4 rot = VecMath.Mult(VecMath.T(0,0,-0f), VecMath.ArbRotate(VecMath.Normalize(cross), MainActivity.phone_ang));
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));

        m.DrawModel();
    }


    public FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }

}
