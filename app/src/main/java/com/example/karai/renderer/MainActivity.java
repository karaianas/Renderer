package com.example.karai.renderer;

import android.os.Bundle;

// --- My addition
import android.opengl.GLSurfaceView;
import android.app.Activity;


public class MainActivity extends Activity {
    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // When your OpenGL app is memory intensive, de-allocate objects here

        mGLView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // When your OpenGL app is memory intensive, de-allocate objects here

        mGLView.onResume();
    }
}

