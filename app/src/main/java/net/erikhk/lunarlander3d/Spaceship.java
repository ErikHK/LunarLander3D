package net.erikhk.lunarlander3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by erikhk on 28/1/2016.
 */
public class Spaceship {

    Model m,fire;
    Model[] sphere = new Model[3];
    Mat4 T;
    Mat4 Ro;
    Bitmap bm;

    public static final float gravity = -0.0006f;
    public boolean haslanded = false;
    public boolean hascrashed = false;
    public boolean isthrusting = false;
    public float fuel = 100.0f;
    public float max_fuel = 100.0f;
    public float thrust = .0007f*2f*1.5f;
    public Vec3 speed = new Vec3();
    public Vec3 pos = new Vec3();
    public Vec3 acc = new Vec3();
    public Vec3[] fin_pos = new Vec3[3];

    public Vec3 phone_n = new Vec3();
    public Vec3 init_phone_n = new Vec3();

    public Spaceship(Context c)
    {
        m = new Model(c, R.raw.spaceship_verts, R.raw.spaceship_normals, R.raw.spaceship_texture,
                R.drawable.texture, GLES20.GL_TEXTURE0);

        for (int i = 0; i < 3; i++) {
            sphere[i] = new Model(c, R.raw.diamond_verts, R.raw.diamond_normals, R.raw.diamond_texture,
                    R.drawable.texture, GLES20.GL_TEXTURE0);
        }


        fire = new Model(c, R.raw.plane_verts, R.raw.plane_normals);
        //T = VecMath.T(0,25f,0);
        T = VecMath.IdentityMatrix();
        Ro = VecMath.IdentityMatrix();
        pos = new Vec3(5f, 15f, 27f);

        for (int i = 0; i < 3; i++) {
            fin_pos[i] = new Vec3(0,0,0);
        }
    }

    public void setMaxFuel(LandingPoint lp)
    {
        float dist = VecMath.DistanceXZ(lp.pos, pos);

        if(dist >= 1.0f)
          max_fuel = (float)Math.pow(dist, 1.2)*3.7f;
        else
          max_fuel = dist*3.5f;

        if(max_fuel < 30f)
            max_fuel = 30f;

        max_fuel = 1000;
        fuel = max_fuel;
    }

    public void DrawModel()
    {

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m.textureHandle[0]);
        Mat4 rot;
        if(!haslanded) {
            Vec3 cross = VecMath.CrossProduct(MainActivity.phone_n, MainActivity.init_phone_n);
            phone_n = MainActivity.phone_n;
            init_phone_n = MainActivity.init_phone_n;

            Vec3 ny = new Vec3(0,0,-1);
            Vec3 cross2 = VecMath.CrossProduct(init_phone_n, ny);
            float ang2 = (float)Math.asin(VecMath.Norm(cross2)/(VecMath.Norm(init_phone_n) ));;
            Mat4 rotmat2 = VecMath.ArbRotate(cross2, ang2);
            //Mat4 rotmat = VecMath.ArbRotate(cross, MainActivity.phone_ang);
            Mat4 rotmat = VecMath.ArbRotate(VecMath.Normalize(cross), MainActivity.phone_ang);

            if( Math.abs(init_phone_n.z) > Math.abs(init_phone_n.y) )
                rotmat = VecMath.ArbRotate( VecMath.MultVec3(VecMath.Rx(-(float) Math.PI/2),  VecMath.Normalize(cross)), MainActivity.phone_ang);

            Mat4 rotmatf = VecMath.Mult(rotmat, rotmat2);

            Ro = rotmat;
            //Ro = VecMath.ArbRotate( VecMath.MultVec3(VecMath.Rx(-(float) Math.PI/2),  VecMath.Normalize(cross)), MainActivity.phone_ang);
            rot = VecMath.Mult(T, Ro);
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));
        }
        else
        {
            rot = T;
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(rot.m));
            //Ro = rot;
        }

        //GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(Ro.m));
        m.DrawModel();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //draw debug spheres
        for (int i = 0; i < 3; i++) {
            float r = .3f;
            float coss = (float) (r * Math.cos(Math.PI / 6 + i*2*Math.PI/3));
            float sinn = (float) (r * Math.sin(Math.PI / 6 + i * 2 * Math.PI / 3));
            Vec3 poss = new Vec3(coss, -.5f, sinn);
            Mat4 TT = VecMath.T(poss);

            Mat4 SS = VecMath.S(.2f, .2f, .2f);
            Mat4 tott = VecMath.Mult(T, VecMath.Mult(Ro, VecMath.Mult(TT, SS)));
            fin_pos[i] = VecMath.MultVec3(tott, poss); //fin position in absolute coords
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(tott.m));
            sphere[i].DrawModel();

        }


        if(isthrusting && fuel > .0f) {
            GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_exhaustf"), 1);
            Mat4 Tf = VecMath.Mult(rot, VecMath.T(0, -.8f, 0));
            GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(Tf.m));
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glDepthMask(false);
            fire.DrawModel();
            GLES20.glDepthMask(true);
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_exhaustf"), 0);
        }
    }

    public void move()
    {
        Vec3 cross = VecMath.Normalize(VecMath.CrossProduct(phone_n, init_phone_n));
        Vec3 ny = new Vec3(0,0,1);
        Vec3 cross2 = VecMath.CrossProduct(init_phone_n, ny);
        float ang2 = (float)Math.asin(VecMath.Norm(cross2)/(VecMath.Norm(init_phone_n) ));;
        Mat4 rotmat2 = VecMath.ArbRotate(cross2, ang2);
        //Mat4 rotmat = VecMath.ArbRotate(cross, MainActivity.phone_ang);
        float phone_ang = MainActivity.phone_ang;
        Mat4 rotmat = VecMath.ArbRotate(cross, MainActivity.phone_ang);

        if( Math.abs(init_phone_n.z) > Math.abs(init_phone_n.y) )
            rotmat = VecMath.ArbRotate( VecMath.MultVec3(VecMath.Rx(-(float) Math.PI/2),  VecMath.Normalize(cross)), MainActivity.phone_ang);

        Mat4 rotmatf = VecMath.Mult(rotmat, rotmat2);

        float nx = VecMath.MultVec3(rotmat, new Vec3(0,1,0)).x;
        //float nx = -VecMath.MultVec3(rotmat, VecMath.Normalize(init_phone_n)).x;
        //float nx = VecMath.MultVec3(rotmat, new Vec3(1,0,0)).x;
        float nz = VecMath.MultVec3(rotmat, new Vec3(0,1,0)).z;
        //float nz = -VecMath.MultVec3(rotmat, VecMath.Normalize(init_phone_n)).z;

        //float phone_ang = MainActivity.phone_ang;
        //float nx = VecMath.DotProduct(VecMath.Normalize(init_phone_n), new Vec3(0,0,0));
        //Vec3 init_n = VecMath.Normalize(init_phone_n);
        //float nx = phone_n.x * init_n.y;
        //float nz = phone_n.z * init_n.y;

        //Vec3 normn = VecMath.Normalize(init_phone_n);

        //Vec3 world_n = new Vec3(init_phone_n.x - phone_n.x, init_phone_n.y-phone_n.y, init_phone_n.z - phone_n.z);
        //Vec3 xy = new Vec3(world_n.x, world_n.y, 0);
        //Vec3 xz = new Vec3((phone_n.x-init_phone_n.x), 0, (phone_n.z-init_phone_n.z));
        //Vec3 y = new Vec3(0,init_phone_n.y,0);
        //Vec3 x = new Vec3(init_phone_n.x,0,0);


        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "spaceship_pos"), pos.x, pos.y, pos.z);
        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "spaceship_fin_pos0"), fin_pos[0].x, fin_pos[0].y, fin_pos[0].z);
        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "spaceship_speed"), speed.x, speed.y, speed.z);
        GLES20.glUniform3f(GLES20.glGetUniformLocation(Shader.program, "spaceship_speedf"), speed.x, speed.y, speed.z);

        float ang = MainActivity.phone_ang;

        if(isthrusting && fuel > 0.0f) {

            if(fuel > 0.0f)
              fuel -= .3;
            GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "fuel"), fuel);

            acc.x = thrust * nx;///5.0f;
            acc.y = thrust * (float)Math.cos(ang) + gravity;
            acc.z = thrust * nz;///5.0f;

        }
        else
            acc.y = gravity;

        speed.x += acc.x;
        speed.y += acc.y;
        speed.z += acc.z;

        pos.x += speed.x;
        pos.y += speed.y;
        pos.z += speed.z;

        T = VecMath.T(pos);

        speed.x *= 0.97f;
        speed.z *= 0.97f;

    }


    public FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }

}
