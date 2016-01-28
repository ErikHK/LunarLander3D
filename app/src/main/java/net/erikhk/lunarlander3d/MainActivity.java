package net.erikhk.lunarlander3d;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    public static Vec3 phone_n = new Vec3();
    public static Vec3 init_phone_n = new Vec3();
    public static float phone_ang = 0;

    private GLSurfaceView glView;
    public static String spaceship_verts_s;
    public static String spaceship_normals_s;
    public static int spaceshipNumVerts = 3924;
    public static int spaceshipNumNormals = 3924;

    public static float[] spaceship_verts;
    public static float[] spaceship_normals;

    public static String vertshader;
    public static String fragshader;

    public static boolean hasSetFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setPreserveEGLContextOnPause(true);
        glView.setRenderer(new MyGLRenderer(this));
        this.setContentView(glView);

        spaceship_verts_s = readTxt(R.raw.spaceship_verts);
        spaceship_verts = stringToFloats(spaceship_verts_s);

        spaceship_normals_s = readTxt(R.raw.spaceship_normals);
        spaceship_normals = stringToFloats(spaceship_normals_s);

        vertshader = readTxt(R.raw.vertshader);
        fragshader = readTxt(R.raw.fragshader);



        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Sensor mySensor = sensorEvent.sensor;

        //if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = -sensorEvent.values[1];
            float z = -sensorEvent.values[2];

        float[] test1 = {x,y,z};
        float[] test = canonicalOrientationToScreenOrientation(1,test1);
        //float[] test = test1;

        if(!hasSetFirst)
        {
            init_phone_n.x = test[0];
            init_phone_n.y = test[1];
            init_phone_n.z = test[2];

            hasSetFirst = true;
        }

            phone_n = new Vec3(test[0],test[1],test[2]);
            //Vec3 n = new Vec3(0,-1,0);
            Vec3 cross = VecMath.CrossProduct(phone_n, init_phone_n);

            phone_ang = (float)Math.asin(VecMath.Norm(cross)/(VecMath.Norm(phone_n) * VecMath.Norm(init_phone_n) ));

        //}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private String readTxt(int id){

        InputStream inputStream = getResources().openRawResource(id);

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
        float verts[] = new float[3*spaceshipNumVerts];
        for(int i=0;i<m.length-1;i++)
        {
            verts[i] = Float.valueOf(m[i]);
        }

        return verts;
    }


    static float[] canonicalOrientationToScreenOrientation(
            int displayRotation, float[] canVec)
    {
        float[] screenVec = new float[3];
        final int axisSwap[][] = {
                {  1,  -1,  0,  1  },     // ROTATION_0
                {-1,  -1,  1,  0  },     // ROTATION_90
                {-1,    1,  0,  1  },     // ROTATION_180
                {  1,    1,  1,  0  }  }; // ROTATION_270

        final int[] as = axisSwap[displayRotation];
        screenVec[0]  =  (float)as[0] * canVec[ as[2] ];
        screenVec[1]  =  (float)as[1] * canVec[ as[3] ];
        screenVec[2]  =  canVec[2];

        return screenVec;
    }

}