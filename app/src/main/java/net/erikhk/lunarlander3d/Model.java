package net.erikhk.lunarlander3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by erikhk on 28/1/2016.
 */
public class Model {

    public static int vb, ib, nb, tb, numverts;
    public static FloatBuffer vertbuff, normbuff, texbuff;
    public static IntBuffer indexbuff;
    int buffers[] = new int[4];

    String verts_s, norms_s, texture_s;
    float[] verts, norms, texture;

    boolean drawtex = false;


    /** Store our model data in a float buffer. */
    //private final FloatBuffer mCubeTextureCoordinates;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;
    final int[] textureHandle = new int[1];


    public Model(Context c, int vertresource, int normresource)
    {
        verts_s = readTxt(c, vertresource);
        verts = stringToFloats(verts_s);
        vertbuff = makefloatbuffer(verts);

        norms_s = readTxt(c, normresource);
        norms = stringToFloats(norms_s);
        normbuff = makefloatbuffer(norms);

        numverts = vertbuff.capacity();

        GLES20.glGenBuffers(4, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, vertbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, normbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public Model(Context c, int vertresource, int normresource, int textureresource, int bm)
    {
        drawtex = true;

        verts_s = readTxt(c, vertresource);
        verts = stringToFloats(verts_s);
        vertbuff = makefloatbuffer(verts);

        norms_s = readTxt(c, normresource);
        norms = stringToFloats(norms_s);
        normbuff = makefloatbuffer(norms);

        numverts = vertbuff.capacity();

        texture_s = readTxt(c, textureresource);
        texture = stringToFloats(texture_s);
        texbuff = makefloatbuffer(texture);

        GLES20.glGenBuffers(4, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, vertbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, normbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texbuff.capacity() * 2, texbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(c.getResources(), bm, options);
            //final Bitmap bitmap = MyGLRenderer.bitm;

            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            //bitmap.recycle();
        }

    }


    public Model(float[] verts, float[] norms, float[] texverts, int[] indexarray, int vertexcount, int indexcount)
    {

        vertbuff = makefloatbuffer(verts);

        normbuff = makefloatbuffer(norms);

        texbuff = makefloatbuffer(texverts);

        indexbuff = makeintbuffer(indexarray);

        numverts = vertbuff.capacity();

        GLES20.glGenBuffers(4, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, vertbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertbuff.capacity() * 4, normbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        //GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texbuff.capacity() * 2, texbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[3]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexbuff.capacity()*4, indexbuff, GLES20.GL_STATIC_DRAW);
        //unbind
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void DrawModel()
    {

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        //GLES20.glEnableVertexAttribArray(Shader.positionhandle);
        GLES20.glVertexAttribPointer(Shader.positionhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(Shader.positionhandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        //GLES20.glEnableVertexAttribArray(Shader.normalhandle);
        GLES20.glVertexAttribPointer(Shader.normalhandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(Shader.normalhandle);

        //TEXTURE
        mTextureUniformHandle = GLES20.glGetUniformLocation(Shader.program, "tex");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(Shader.program, "inTexCoord");

        // Set the active texture unit to texture unit 0.
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        //GLES20.glUniform1i(mTextureUniformHandle, 0);

        if(drawtex) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                    //0, texbuff);
                    0, 0);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertbuff.capacity() * 4 * 32 );

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }


    private String readTxt(Context c, int id){

        InputStream inputStream = c.getResources().openRawResource(id);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }


    private float[] stringToFloats(String input)
    {
        String[] m;
        m = input.split(",");
        int len = m.length;
        float verts[] = new float[3*len];
        for(int i=0;i<m.length-1;i++)
        {
            verts[i] = Float.valueOf(m[i]);
        }

        return verts;
    }

    public FloatBuffer makefloatbuffer(float[] array)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatbuff.put(array).position(0);

        return floatbuff;
    }

    public IntBuffer makeintbuffer(int[] array)
    {
        IntBuffer intbuff = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

        intbuff.put(array).position(0);

        return intbuff;
    }


}
