package com.example.karai.renderer;

/**
 * Created by karai on 9/18/2017.
 */

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer extends Activity implements GLSurfaceView.Renderer {

    // Objects to draw
   // private Triangle mTriangle;
    private Torus mTorus;
    private Light mLight;

    // Matrices
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private float[] mRotationMatrix = new float[16];
    private float[] mModelMatrix_light = new float[16];
    private final float [] lPosition = {0.0f, 0.0f, 0.0f, 1.0f};

    // Rotation of the object
    public volatile float mAngle;

    public float getAngle()
    {
        return mAngle;
    }

    public void setAngle(float angle)
    {
        mAngle = angle;
    }

    // Called once to set up the view's OpenGl ES environment
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Initialize a torus
        mTorus = new Torus();

        // Initialize a light source
        mLight = new Light();

        initialize_matrices();
    }

    public void initialize_matrices()
    {
        // (1) View matrix
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.f, 0.0f, 1.0f, 0.0f);

        // (2) Projection matrix
        //Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1, 1, 2, 9);

        // Set model matrix for torus
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, 4.0f, 0.0f, -7.0f);
        Matrix.rotateM(mModelMatrix, 0, 20, 1.0f, 0.0f, 0.0f);// second parameter is angle in degrees

        // Set model matrix for light
        Matrix.setIdentityM(mModelMatrix_light, 0);
        Matrix.translateM(mModelMatrix_light, 0, 1.0f, 2.0f, 0.0f);
        //Matrix.rotateM(mModelMatrix_light, 0, 0, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        mTorus.setM(mModelMatrix);
        mLight.setM(mModelMatrix_light);
        mLight.setPosition(lPosition);
    }

    // Called for each redraw of the view
    public void onDrawFrame(GL10 unused)
    {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Must cull face
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Create a rotation
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f);
        mLight.rotate(mRotationMatrix);

        // Draw light
        mLight.draw(mViewMatrix, mProjectionMatrix);

        // Draw torus
        mTorus.draw(mViewMatrix, mProjectionMatrix, mLight.getPositionMV());

        // *** Later add Rotation matrix for x-y dimension rotation
        //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
    }

    // Called if the geometry of the view changes e.g. when the device's orientation changes
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float)width/height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 9);
    }

}