package net.erikhk.lunarlander3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import java.nio.FloatBuffer;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertbuffer, normbuffer;
    int count = 0;
    float angz=0, angy=0;
    public float t;
    public Terrain terrain;
    public Spaceship spaceship;
    public LandingPoint lp;
    public FuelBar fuelbar;
    public Camera camera;
    public Speedometer speedometer;
    public Sky sky;
    public Menu menu;
    public ParticleSystem particleSystem;
    public Coin coin;
    public Context c;
    //public static Bitmap bitm;

    public MyGLRenderer(Context c_)
    {
        this.c = c_;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {

        t = 0;
        Shader.makeprogram();

        GLES20.glEnableVertexAttribArray(Shader.positionhandle);

        float ratio = MainActivity.swidth / MainActivity.sheight;
        Mat4 projectionMatrix = VecMath.frustum(-.1f * ratio, .1f * ratio, -.1f, .1f, 0.2f, 750f);

        Mat4 cam = VecMath.lookAt(0f, 1f, 20f,
                0f, 0f, 0f,
                0.0f, 1.0f, 0.0f);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "projmatrix"), 1, true, VecMath.makefloatbuffer(projectionMatrix.m));
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(Shader.program, "cammatrix"), 1, true, VecMath.makefloatbuffer(cam.m));

        spaceship = new Spaceship(c);
        terrain = new Terrain(c);
        lp = new LandingPoint(c, terrain);
        spaceship.setMaxFuel(lp);
        fuelbar = new FuelBar(c);
        camera = new Camera();
        menu = new Menu(c);
        sky = new Sky(c);
        coin = new Coin(c, terrain);

        speedometer = new Speedometer(c);
        particleSystem = new ParticleSystem();

        GLES20.glClearColor(.6f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "fuel"), spaceship.fuel);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "max_fuel"), spaceship.max_fuel);

    }

    //@Override
    public void onSurfaceChanged(GL10 gl, int width_, int height_) {
        GLES20.glViewport(0, 0, width_, height_);
    }

    //@Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                | GLES20.GL_DEPTH_BUFFER_BIT);

        //increase and upload time
        t += .01f;
        GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "t"), t);

        if(t > 100.0*Math.PI)
            t = 0;


        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_skydome"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_skydomef"), 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        sky.DrawModel();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_skydome"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_skydomef"), 0);

        coin.update(spaceship, t);
        //Draw models
        terrain.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_landing_point"), 1);
        lp.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_landing_point"), 0);
        coin.DrawModel();

        fuelbar.update(spaceship.fuel);
        camera.update(spaceship);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hud"), 1);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hudf"), 1);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        fuelbar.DrawModel();
        speedometer.update(spaceship);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hud"), 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_hudf"), 0);


        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_spaceship"), 1);
        spaceship.DrawModel();
        GLES20.glUniform1i(GLES20.glGetUniformLocation(Shader.program, "draw_spaceship"), 0);

        particleSystem.DrawModel(spaceship);

        if(MainActivity.istapping)
            spaceship.isthrusting = true;
        else
            spaceship.isthrusting = false;

        float hh = 0;
        if(spaceship.pos.x >= 0 && spaceship.pos.x < 30 && spaceship.pos.z >= 0 && spaceship.pos.z < 30)
            hh = terrain.getHeight(spaceship.pos.x, spaceship.pos.z);

        if(hh < 0)
            hh = 0;

        float dist = VecMath.DistanceXZ(spaceship.pos, lp.pos);

        if(spaceship.pos.y > hh && dist > 1.0f)
            spaceship.move();
        else if(dist < 1.0f && spaceship.pos.y > lp.pos.y + 2f)
            spaceship.move();
        else if(dist < 1.0f && spaceship.pos.y > lp.pos.y + 1.8f && (float)Math.abs(spaceship.speed.y) < .065f)
            spaceship.haslanded = true;
        else //has crashed
            spaceship.hascrashed = true;

        if(spaceship.hascrashed || spaceship.haslanded) {

            if(spaceship.hascrashed)
                menu.drawGameOver();
            else if(spaceship.haslanded)
                menu.drawSuccess();

            //tell it to wait for restart
            MainActivity.waitForRestart = true;

            if(MainActivity.restart)
              reset();
        }


    }

    public void reset()
    {
        spaceship = null;
        terrain = null;
        lp = null;
        fuelbar = null;
        camera = null;
        menu = null;
        particleSystem = null;
        sky = null;

        spaceship = new Spaceship(c);
        terrain = new Terrain(c);
        lp = new LandingPoint(c, terrain);
        spaceship.setMaxFuel(lp);

        GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "fuel"), spaceship.fuel);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(Shader.program, "max_fuel"), spaceship.max_fuel);

        fuelbar = new FuelBar(c);
        camera = new Camera();
        menu = new Menu(c);
        sky = new Sky(c);
        particleSystem = new ParticleSystem();

        MainActivity.waitForRestart = false;
        MainActivity.restart = false;
        t = 0;
    }


    float[] toFloatArray(double[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float)arr[i];
        }
        return ret;
    }


}
