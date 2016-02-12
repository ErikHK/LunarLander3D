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

        pos = new Vec3(0, 0, 2);
        Random r = new Random();

        pos.x = 20*r.nextFloat() + 10;
        pos.z = 20*r.nextFloat() + 10;

        float ydiff = r.nextFloat()*3f + 2f;

        pos.y = Math.max(t.getHeight(pos.x, pos.z),0) + ydiff;

    }

    public void update(float t)
    {
        Rr = VecMath.Mult(VecMath.Ry(2*t), VecMath.Rz((float)Math.PI/2));
        T = VecMath.T(pos.x, pos.y, pos.z);
        M = VecMath.Mult(T, VecMath.Mult(Rr, VecMath.S(2f, .2f, 2f)));
        //GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, VecMath.makefloatbuffer(T.m));
    }

    public void DrawModel()
    {
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, VecMath.makefloatbuffer(M.m));
        m.DrawModel();
    }



}
