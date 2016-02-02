package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;


public class FuelBar {
    Model m;
    public float fuel;
    public FuelBar(Context c)
    {
        m = new Model(c, R.raw.plane_verts, R.raw.plane_normals, R.raw.plane_texture,
                R.drawable.landing_point_texture, GLES20.GL_TEXTURE0);
    }

    public void update(float fuel_)
    {
        Mat4 cam = VecMath.IdentityMatrix();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true,
                VecMath.makefloatbuffer(cam.m));
        fuel = fuel_;
    }

    public void DrawModel()
    {
        m.DrawModel();
    }
}