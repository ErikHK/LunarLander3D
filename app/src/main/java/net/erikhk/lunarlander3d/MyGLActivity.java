package net.erikhk.lunarlander3d;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


public class MyGLActivity extends Activity {

    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new MyGLRenderer());
        this.setContentView(glView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glView.onPause();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glView.onResume();
    }

}
