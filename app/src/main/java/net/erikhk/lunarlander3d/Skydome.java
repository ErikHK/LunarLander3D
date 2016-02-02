package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by kl on 2016-02-01.
 */
public class Skydome {

    Model m;
    public Skydome(Context c)
    {

        m = new Model(c, R.raw.plane_verts, R.raw.plane_normals);


    }

    public void DrawModel()
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m.textureHandle[0]);



    }
}
