package net.erikhk.lunarlander3d;

/**
 * Created by erikhk on 27/1/2016.
 */
public class Mat4 {
    public float[] m = new float[16];

    public Mat4()
    {
        for(int i=0;i<16;i++)
            m[i] = 0;
    }

    public Mat4(float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12,
                float x13, float x14, float x15, float x16)
    {
        m[0] = x1;
        m[1] = x2;
        m[2] = x3;
        m[3] = x4;
        m[4] = x5;
        m[5] = x6;
        m[6] = x7;
        m[7] = x8;
        m[8] = x9;
        m[9] = x10;
        m[10] = x11;
        m[11] = x12;
        m[12] = x13;
        m[13] = x14;
        m[14] = x15;
        m[15] = x16;


    }

}
