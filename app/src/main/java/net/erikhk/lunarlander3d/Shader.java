package net.erikhk.lunarlander3d;

import android.opengl.GLES20;

import java.util.Vector;

/**
 * Created by erikhk on 25/1/2016.
 */
public class Shader {


/*
    private final static String vertcode =
            //"attribute vec4 a_pos;"+
            "attribute vec3 inPosition;"+
                    "uniform mat4 matrix;" +
                    "uniform mat4 projmatrix;" +
                    "uniform float ang;" +
                    "mat4 test;" +
                    "void main(){" +
                    "test = mat4(cos(ang),-sin(ang),0,0, sin(ang),cos(ang),0,0, 0,0,1,0, 0,0,0,1);" +
                    //"gl_Position = projmatrix * matrix * vec4(a_pos.x, a_pos.y, a_pos.z, 1);"+
                    "gl_Position = projmatrix * matrix * vec4(inPosition.x, inPosition.y, inPosition.z, 1.0);"+
                    "}";
                    */

    private final static String fragcode =
            "precision mediump float;"+
                    "uniform vec4 u_color;"+
                    "void main(){"+
                    "gl_FragColor = u_color * gl_FragCoord.z;"+
                    "}";


    public static int program;

    public static int positionhandle;
    public static int normalhandle;
    public static int colorhandle;
    public static int texturehandle;
    public static int rothandle;
    public static int anghandle;


    public static void makeprogram()
    {

        int vertexshader = loadshader(GLES20.GL_VERTEX_SHADER, MainActivity.vertshader);
        int fragmentshader = loadshader(GLES20.GL_FRAGMENT_SHADER, MainActivity.fragshader);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertexshader);
        GLES20.glAttachShader(program, fragmentshader);
        GLES20.glLinkProgram(program);

        positionhandle = GLES20.glGetAttribLocation(program, "inPosition");
        normalhandle = GLES20.glGetAttribLocation(program, "inNormal");
        texturehandle = GLES20.glGetAttribLocation(program, "inTexCoord");
        colorhandle = GLES20.glGetUniformLocation(program, "u_color");
        rothandle = GLES20.glGetUniformLocation(program, "matrix");
        anghandle = GLES20.glGetUniformLocation(program, "ang");

        GLES20.glUseProgram(program);

    }

    private static int loadshader(int type, String shadertext)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shadertext);
        GLES20.glCompileShader(shader);

        return shader;

    }
}
