package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

import java.util.Random;

/**
 * Created by kl on 2016-02-12.
 */
public class Coin {
    Model m;
    Vec3 pos;
    Mat4 Rr;
    Mat4 T;
    Mat4 M;

    public Coin(Context c, Terrain t)
    {
        m = new Model(c, R.raw.landingpoint_verts, R.raw.landingpoint_normals, R.raw.landing_point_texture,
                R.drawable.landing_point_texture, GLES20.GL_TEXTURE0);


        Random r = new Random();

        pos.x = 20*r.nextFloat() + 10;
        pos.z = 20*r.nextFloat() + 10;

        float ydiff = r.nextFloat()*3f + 2f;

        pos.y = t.getHeight(pos.x, pos.z) + ydiff;

    }

    public void update(float t)
    {
        Rr = VecMath.Ry(t/100.0f);
        T = VecMath.T(pos.x, pos.y, pos.z);
        M = VecMath.Mult(T, VecMath.Mult(VecMath.S(.1f, 1f, 1f), Rr));
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, VecMath.makefloatbuffer(M.m));
    }

    public void DrawModel()
    {
        m.DrawModel();
    }



}
