package com.example.karai.renderer;

/**
 * Created by karai on 9/18/2017.
 */

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer extends Activity implements GLSurfaceView.Renderer {

    // Objects to draw
   // private Triangle mTriangle;
    private Torus mTorus;

    // Matrices
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    // Rotation of the object
    private float mAngle;

    // Called once to set up the view's OpenGl ES environment
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Initialize a triangle
        //mTriangle = new Triangle();

        // Initialize a torus
        mTorus = new Torus();
    }

    // Called for each redraw of the view
    public void onDrawFrame(GL10 unused)
    {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Must cull face
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        //mTriangle.draw();

        // Matrix set-up
        // (0) Model matrix

        // (1) View matrix
        // ***Later implement way to change view points
        //Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.f, 0.0f, 0.0f, 1.0f);
        //Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 3.0f, 5.0f, 0.0f, 0.0f, 0.f, 0.0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.f, 0.0f, 1.0f, 0.0f);

        // (2) Projection matrix
        //Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1, 1, 2, 9);

        // (3) projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mTorus.draw(mMVPMatrix, mViewMatrix);

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

    public static int loadShader(int type, String shaderCode)
    {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}