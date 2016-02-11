package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by kl on 2016-02-10.
 */
public class Speedometer {
    Model m, mb;

    public Speedometer(Context c)
    {
        m = new Model(c, R.raw.plane_verts, R.raw.plane_normals, R.raw.plane_texture,
                R.drawable.landing_point_texture, GLES20.GL_TEXTURE0);

        mb = new Model(c, R.raw.plane_verts, R.raw.plane_normals, R.raw.plane_texture,
                R.drawable.landing_point_texture, GLES20.GL_TEXTURE0);
    }

    public void update(Spaceship s)
    {
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometerf"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer_bg"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer_bgf"), 1);
        mb.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer_bg"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer_bgf"), 0);
        m.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometer"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_speedometerf"), 0);
    }

}
