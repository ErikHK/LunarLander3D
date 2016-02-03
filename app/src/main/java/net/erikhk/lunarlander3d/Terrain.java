package net.erikhk.lunarlander3d;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Terrain {

    public static Model m;
    static int[] p = new int[512];
    static int repeat = 0;
    static int size = 16;
    static float[][] heights = new float[size][size];
    Random r = new Random();
    static int scale = 1;
    public float[] vertexArray = new float[size*size*3];

    public Terrain(Context c)
    {
        //m = new Model(c, R.raw.terrain_verts, R.raw.terrain_normals);
        for (int i = 0; i < size*size*3; i++) {
            vertexArray[i] = 0.0f;
        }
        init_perlin(16);
        generate_terrain(size);

    }

    public float getHeight(float x, float z)
    {
        //if(vertexArray == null)
        //    return 0;

        int quad = (int)( (Math.floor(x/2.0) + Math.floor(z/2.0)*size )*3);
        boolean choose_upper = false;

        Vec3[] corners = new Vec3[3];
        corners[0] = new Vec3();
        corners[1] = new Vec3();
        corners[2] = new Vec3();

        if( (x-Math.floor(x)) + (z-Math.floor(z)) > 1)
                choose_upper = true;

            if(choose_upper)
            {
                corners[0].x = vertexArray[quad + (1 + 1*size)*3 + 0];
                corners[0].y = vertexArray[quad + (1 + 1*size)*3 + 1];
                corners[0].z = vertexArray[quad + (1 + 1*size)*3 + 2];

                corners[1].x = vertexArray[quad + (0 + 1*size)*3 + 0];
                corners[1].y = vertexArray[quad + (0 + 1*size)*3 + 1];
                corners[1].z = vertexArray[quad + (0 + 1*size)*3 + 2];

                corners[2].x = vertexArray[quad + (1 + 0*size)*3 + 0];
                corners[2].y = vertexArray[quad + (1 + 0*size)*3 + 1];
                corners[2].z = vertexArray[quad + (1 + 0*size)*3 + 2];

            }else{

                corners[0].x = vertexArray[quad + (0 + 0*size)*3 + 0];
                corners[0].y = vertexArray[quad + (0 + 0*size)*3 + 1];
                corners[0].z = vertexArray[quad + (0 + 0*size)*3 + 2];

                corners[1].x = vertexArray[quad + (1 + 0*size)*3 + 0];
                corners[1].y = vertexArray[quad + (1 + 0*size)*3 + 1];
                corners[1].z = vertexArray[quad + (1 + 0*size)*3 + 2];

                corners[2].x = vertexArray[quad + (0 + 1*size)*3 + 0];
                corners[2].y = vertexArray[quad + (0 + 1*size)*3 + 1];
                corners[2].z = vertexArray[quad + (0 + 1*size)*3 + 2];
            }

            Vec3 vec1, vec2, normal;
            //Plane equation is given as Ax + By + Cz + D = 0
            float A,B,C,D;
            vec1 = VecMath.VectorSub(corners[1], corners[0]);
            vec2 = VecMath.VectorSub(corners[2], corners[0]);
            normal = VecMath.Normalize(VecMath.CrossProduct(vec2, vec1));
            A = normal.x;
            B = normal.y;
            C = normal.z;
            D = -(A*corners[0].x + B*corners[0].y + C*corners[0].z);

            float y = (-D-C*z-A*x)/B;

            return y;
        //return corners[0].y;
        //return heights[(int)(x/2)][(int)(z/2)];
    }

    public void DrawModel()
    {

        //Mat4 M = VecMath.Mult(VecMath.T(-scale*size/2,0,-scale*size/2), VecMath.S(1.f,1.f,1.f));
        Mat4 M = VecMath.Mult(VecMath.T(0,0,0), VecMath.S(1.f,1.f,1.f));
        GLES20.glUniformMatrix4fv(Shader.rothandle, 1, true, makefloatbuffer(M.m));

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m.buffers[0]);
        //GLES20.glEnableVertexAttribArray(Shader.positionhandle);
        GLES20.glVertexAttribPointer(Shader.positionhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(Shader.positionhandle);

        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m.buffers[1]);
        //GLES20.glEnableVertexAttribArray(Shader.normalhandle);
        GLES20.glVertexAttribPointer(Shader.normalhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(Shader.normalhandle);
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertbuff.capacity() * 4 * 32 );

        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[3]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, (size-1) * (size-1) * 2 * 3, GLES20.GL_UNSIGNED_INT, 0);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertbuff.capacity() * 4 * 32 );

        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    public void reset()
    {
        //m = generate_terrain(c, 16);

    }


    public FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }


    public void generate_terrain(int size)
    {
            //init_perlin();
            //tex->width *= 1.5;
            //tex->height *= 1.5;

            int vertexCount = size*size;
            int triangleCount = (size-1) * (size-1) * 2;
            int x, z;

        float[] vertexArray = new float[size*size*3];
        float[] normalArray = new float[size*size*3];
        float[] texCoordArray = new float[size*size*2];
        int[] indexArray = new int[triangleCount * 3];

        //Point3D tmp_normal;
        Vec3 tmp_normal = new Vec3();

            for (x = 0; x < size; x++)
                for (z = 0; z < size; z++)
                {
                    //float height = 1 * (float)OctavePerlin(x / (8.0 * 16 * 4 * 4), z / (8.0 * 16 * 4 * 4), 0, 10, 10.0);
                    float height = 30 * (float)OctavePerlin(x / (16.0 * 16 * 4 * 4 ), z / (16.0 * 16 * 4 * 4 ), 0, 10, 10.0) - 15f;
                    //float height = 1;

                    /*
                    if (height > highest.y)
                    {
                        highest.x = x;
                        highest.z = z;
                        highest.y = height;
                    }
                    */
                    // Vertex array. You need to scale this properly
                    vertexArray[(x + z * size)*3 + 0] = 2*x;//scale*(x-size/2);
                    float mx=(x-7.5f)/7.5f, mz=(z-7.5f)/7.5f;
                    float f = 0.0f;
                    //f = sinc(mx, mz);
                    f = linear_attenuation(mx, mz);

                    vertexArray[(x + z * size) * 3 + 1] = height*f - .2f;
                    vertexArray[(x + z * size)*3 + 2] = 2*z;//scale*(z-size/2);

                    //store height for collision tests
                    heights[x][z] = height*f-.2f;

                    // Normal vectors. You need to calculate these.
                    tmp_normal = calc_normal(vertexArray, x, z, size);
                    //printf("%f %f %f\n", tmp_normal.x, tmp_normal.y, tmp_normal.z);

                    normalArray[(x + z * size)*3 + 0] = tmp_normal.x;
                    normalArray[(x + z * size)*3 + 1] = tmp_normal.y;
                    normalArray[(x + z * size)*3 + 2] = tmp_normal.z;

                    //normalArray[(x + z * size)*3 + 0] = 0f;
                    //normalArray[(x + z * size)*3 + 1] = 1f;
                    //normalArray[(x + z * size)*3 + 2] = 0f;

                    // Texture coordinates. You may want to scale them.
                    texCoordArray[(x + z * size)*2 + 0] = x/20.0f; // (float)x / tex->width;
                    texCoordArray[(x + z * size)*2 + 1] = z/20.0f; // (float)z / tex->height;
                }
            for (x = 0; x < size-1; x++)
                for (z = 0; z < size-1; z++)
                {
                    // Triangle 1
                    indexArray[(x + z * (size-1))*6 + 0] = x + z * size;
                    indexArray[(x + z * (size-1))*6 + 1] = x + (z+1) * size;
                    indexArray[(x + z * (size-1))*6 + 2] = x+1 + z * size;
                    // Triangle 2
                    indexArray[(x + z * (size-1))*6 + 3] = x+1 + z * size;
                    indexArray[(x + z * (size-1))*6 + 4] = x + (z+1) * size;
                    indexArray[(x + z * (size-1))*6 + 5] = x+1 + (z+1) * size;
                }

            // End of terrain generation

        this.vertexArray = vertexArray;

            // Create Model and upload to GPU:

            Model model = new Model(
                    vertexArray,
                    normalArray,
                    texCoordArray,
                    indexArray,
                    vertexCount,
                    triangleCount*3);

        m = model;

        vertexArray = null;
        normalArray = null;
        texCoordArray = null;
        indexArray  = null;
    }

    public static float sinc(float mx, float mz)
    {
        float f = 0.0f;
        if(mx != 0.0f && mz != 0.0f)
            //f = (float) (Math.sin(Math.PI * mx) * Math.sin(Math.PI * mz) / (mx*mz*Math.PI*Math.PI));
            f = (float) (Math.sin(Math.PI * mx) * Math.sin(Math.PI * mz) /(mx*mz*Math.PI*Math.PI) );
        else if(mx != 0.0f)
            f = (float) (Math.sin(Math.PI * mx) /(mx*Math.PI) );
        else if(mz != 0.0f)
            f = (float) (Math.sin(Math.PI * mz) /(mz*Math.PI) );

        return f;
    }

    public static float linear_attenuation(float mx, float mz)
    {
        //mx and mz goes from -1 to 1
        if(Math.abs(mx) < .95 && Math.abs(mz) < .95)
            return 1.0f;

        //if(Math.abs(mx) >= .85 && Math.abs(mz) >= .85)
        //    return .5f;

        //if(Math.abs(mx) >= .9 || Math.abs(mz) >= .9)
        //    return 0.0f;


        return 0.0f;

    }


    //calculate normals of terrain
    public static Vec3 calc_normal(float[] vertexArray, int x, int z, int width)
    {
        //Point3D vec1, vec2;
        Vec3 vec1 = new Vec3();
        Vec3 vec2 = new Vec3();
        Vec3 normal = new Vec3();

        if(x > 0 && z > 0 && x < width-1 && z < width-1)
        {
            vec1.x = vertexArray[(x-1 + z * width)*3 + 0] -
                    vertexArray[(x + z * width)*3 + 0];

            vec1.y = vertexArray[(x-1 + z * width)*3 + 1] -
                    vertexArray[(x + z * width)*3 + 1];

            vec1.z = vertexArray[(x-1 + z * width)*3 + 2] -
                    vertexArray[(x + z * width)*3 + 2];


            vec2.x = vertexArray[(x + (z+1) * width)*3 + 0] -
                    vertexArray[(x + z * width)*3 + 0];

            vec2.y = vertexArray[(x + (z+1) * width)*3 + 1] -
                    vertexArray[(x + z * width)*3 + 1];

            vec2.z = vertexArray[(x + (z+1) * width)*3 + 2] -
                    vertexArray[(x + z * width)*3 + 2];

            normal = VecMath.Normalize(VecMath.CrossProduct(vec2, vec1));

            /*
            if(normal.y < 0)
                normal = VecMath.ScalarMult(normal, -1);
            if(normal.y == 0)
                normal = new Vec3(0,1,0);
            if(Math.abs(normal.y) < .3)
                normal = new Vec3(0,1,0);
                */

            return normal;
        }

        return new Vec3(0,1,0);
    }


    float random()
    {
        float test1 = r.nextFloat();

        return test1;
    }



    void init_perlin(int size) {

        ArrayList<Integer> perm = new ArrayList<Integer>();
        for (int i = 0; i < 256; i++) {
            perm.add(i, i);
        }

        long seed = System.nanoTime();
        Collections.shuffle(perm, new Random(seed));
        Integer[] permints = perm.toArray(new Integer[perm.size()]);


        for (int x = 0; x<512; x++) {
            //p[x] = permutation[x % 256] + (int)((random()-.5)*4);
            p[x] = permints[x % 256];
            if (p[x] > 254)
                p[x] = 253;
        }

        //throw away perm and permints
        perm = null;
        permints = null;
    }

    public static int inc(int num) {
        num++;
        if (repeat > 0) num %= repeat;

        return num;
    }

    public static double perlin(double x, double y, double z) {
        if (repeat > 0) {									// If we have any repeat on, change the coordinates to their "local" repetitions
            x = ((int)x)%repeat;
            y = ((int)y)%repeat;
            z = ((int)z)%repeat;
        }

        int xi = (int)x & 255;								// Calculate the "unit cube" that the point asked will be located in
        int yi = (int)y & 255;								// The left bound is ( |_x_|,|_y_|,|_z_| ) and the right bound is that
        int zi = (int)z & 255;								// plus 1.  Next we calculate the location (from 0.0 to 1.0) in that cube.
        double xf = x - (int)x;								// We also fade the location to smooth the result.
        double yf = y - (int)y;
        double zf = z - (int)z;
        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

        int aaa, aba, aab, abb, baa, bba, bab, bbb;
        aaa = p[p[p[xi] + yi] + zi];
        aba = p[p[p[xi] + inc(yi)] + zi];
        aab = p[p[p[xi] + yi] + inc(zi)];
        abb = p[p[p[xi] + inc(yi)] + inc(zi)];
        baa = p[p[p[inc(xi)] + yi] + zi];
        bba = p[p[p[inc(xi)] + inc(yi)] + zi];
        bab = p[p[p[inc(xi)] + yi] + inc(zi)];
        bbb = p[p[p[inc(xi)] + inc(yi)] + inc(zi)];

        double x1, x2, y1, y2;
        x1 = lerp(grad(aaa, xf, yf, zf),				// The gradient function calculates the dot product between a pseudorandom
                grad(baa, xf - 1, yf, zf),				// gradient vector and the vector from the input coordinate to the 8
                u);										// surrounding points in its unit cube.
        x2 = lerp(grad(aba, xf, yf - 1, zf),				// This is all then lerped together as a sort of weighted average based on the faded (u,v,w)
                grad(bba, xf - 1, yf - 1, zf),				// values we made earlier.
                u);
        y1 = lerp(x1, x2, v);

        x1 = lerp(grad(aab, xf, yf, zf - 1),
                grad(bab, xf - 1, yf, zf - 1),
                u);
        x2 = lerp(grad(abb, xf, yf - 1, zf - 1),
                grad(bbb, xf - 1, yf - 1, zf - 1),
                u);
        y2 = lerp(x1, x2, v);

        return (lerp(y1, y2, w) + 1) / 2;						// For convenience we bound it to 0 - 1 (theoretical min/max before is -1 - 1)
    }




    public static double grad(int hash, double x, double y, double z) {
        int h = hash & 15;									// Take the hashed value and take the first 4 bits of it (15 == 0b1111)
        double u = h < 8 /* 0b1000 */ ? x : y;				// If the most significant bit (MSB) of the hash is 0 then set u = x.  Otherwise y.

        double v;											// In Ken Perlin's original implementation this was another conditional operator (?:).  I
        // expanded it for readability.

        if (h < 4 /* 0b0100 */)								// If the first and second significant bits are 0 set v = y
            v = y;
        else if (h == 12 /* 0b1100 */ || h == 14 /* 0b1110*/)// If the first and second significant bits are 1 set v = x
            v = x;
        else 												// If the first and second significant bits are not equal (0/1, 1/0) set v = z
            v = z;

        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v); // Use the last 2 bits to decide if u and v are positive or negative.  Then return their addition.
    }

    public static double lerp(double a, double b, double x) {
        return a + x * (b - a);
    }

    public static double fade(double t) {
        // Fade function as defined by Ken Perlin.  This eases coordinate values
        // so that they will "ease" towards integral values.  This ends up smoothing
        // the final output.
        return t * t * t * (t * (t * 6 - 15) + 10);			// 6t^5 - 15t^4 + 10t^3
    }

    int permutation[] = { 151, 160, 137, 91, 90, 15,					// Hash lookup table as defined by Ken Perlin.  This is a randomly
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,	// arranged array of all numbers from 0-255 inclusive.
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    };


    public static double OctavePerlin(double x, double y, double z, int octaves, double persistence) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;			// Used for normalizing result to 0.0 - 1.0
        for (int i = 0; i<octaves; i++) {
            total += perlin(x * frequency, y * frequency, z * frequency) * amplitude;

            maxValue += amplitude;

            amplitude *= persistence;
            frequency *= 2;
        }

        return total / maxValue;
    }
}