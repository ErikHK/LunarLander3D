package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

import java.util.Random;

/**
 * Created by kl on 2016-01-29.
 */
public class LandingPoint {

    public static Vec3 pos;
    public static Model m;
    public static int size;
    public static int scale;

    public LandingPoint(Context c, Terrain t)
    {
        pos = new Vec3();
        Random r = new Random();

        pos.x = 20*r.nextFloat() + 10;
        pos.z = 20*r.nextFloat() + 10;

        int tries=0;

        float distToSpaceship = (float)Math.sqrt((pos.x-2)*(pos.x-2) + (pos.y-27)*(pos.y-27));
            while (t.getHeight(pos.x, pos.z) <= 0.0f && tries < 100 && distToSpaceship > 1) {
                distToSpaceship = (float) Math.sqrt((pos.x - 2) * (pos.x - 2) + (pos.y - 27) * (pos.y - 27));
                pos.x = 30 * r.nextFloat() + 1;
                pos.z = 30 * r.nextFloat() + 1;

                tries++;
            }

        pos.y = t.getHeight(pos.x, pos.z);
        if(pos.y < 0)
            pos.y = -.1f;

        m = new Model(c, R.raw.landingpoint_verts, R.raw.landingpoint_normals, R.raw.landing_point_texture,
                R.drawable.landing_point_texture, GLES20.GL_TEXTURE0);
        size = t.size;
        scale = t.scale;
    }



    public void randomize_pos(Terrain t)
    {
        pos = new Vec3();
        Random r = new Random();

        pos.x = 20*r.nextFloat() + 10;
        pos.z = 20*r.nextFloat() + 10;

        int tries=0;

        while(t.getHeight(pos.x, pos.z) <= 0.0f && tries < 200)
        {
            pos.x = 25*r.nextFloat()+2;
            pos.z = 25*r.nextFloat()+2;

            tries++;
        }

        pos.y = t.getHeight(pos.x, pos.z);
        if(pos.y < 0)
            pos.y = -.1f;
    }

    public void DrawModel()
    {

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m.textureHandle[0]);

        Mat4 M = VecMath.Mult(VecMath.T((pos.x), pos.y, (pos.z)), VecMath.S(4,3f,4));
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, VecMath.makefloatbuffer(M.m));
        m.DrawModel();
        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "landing_point_pos"), pos.x, pos.y, pos.z);

    }
}
