package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

import java.util.Random;

/**
 * Created by kl on 2016-02-02.
 */
public class ParticleSystem {
    public final int numParticles = 15;
    Model[] m = new Model[numParticles];
    Random r = new Random();

    public ParticleSystem()
    {
        float[] verts = {-0.5f, -0.5f, 0,
                -0.5f, 0.5f, 0,
                0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0,
                0.5f, 0.5f, 0};

        float[] normals = {0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0};

        for (int i = 0; i < numParticles; i++) {
            m[i] = new Model(verts, normals);
        }
    }

    public void DrawModel(Spaceship s)
    {
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_particlesf"), 1);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        if(s.isthrusting) {
            for (int i = 0; i < numParticles; i++) {
                //Vec3 dir = new Vec3(.5f*r.nextFloat()-.2f, 1.0f*r.nextFloat()-1.4f, 0.5f*r.nextFloat());
                Vec3 dir = new Vec3(.5f*r.nextFloat()-.2f, .7f*r.nextFloat()-.9f-.4f, (i-numParticles/2.0f)*.1f);
                float distScale = 1.0f*(.4f-dir.y);

                //Vec3 dir = new Vec3(0, -.8f, 0);
                //randomize position
                float scale = (r.nextFloat()*.2f) + .4f*distScale;
                Mat4 T = VecMath.Mult( VecMath.T(dir),
                        VecMath.S(scale*.67f + .3f, scale*1.1f + .3f, scale + .3f));
                Mat4 M = VecMath.Mult(VecMath.Mult(s.T, s.Ro), VecMath.Mult(T, VecMath.InvertMat4(s.Ro) ));
                GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, VecMath.makefloatbuffer(M.m));
                float shade = r.nextFloat()*.3f;
                GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "draw_particle_color"),
                        .7f+shade, .7f+shade, .7f+shade);
                m[i].DrawModel();
            }
        }
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_particlesf"), 0);
        GLES20.glDisable(GLES20.GL_BLEND);

    }

}
