package com.example.karai.renderer;

/**
 * Created by karaianas on 10/14/2017.
 */

import android.opengl.GLES20;
import android.opengl.Matrix;

import android.content.Context;

import java.util.*;

public class Light {

    private int program;

    int positionAttribute;
    int MVPmtx;

    public Light()
    {
        // This is the only way to get context
        MyApp myApp = new MyApp();
        Context context = myApp.getAppContext();

        // Convert vertex_shader.txt to a string
        Scanner vScanner = new Scanner( context.getResources().openRawResource(R.raw.light_vertex_shader), "UTF-8" );
        String vertexShaderCode = vScanner.useDelimiter("\\A").next();
        vScanner.close();

        Scanner fScanner = new Scanner( context.getResources().openRawResource(R.raw.light_fragment_shader), "UTF-8" );
        String fragmentShaderCode = fScanner.useDelimiter("\\A").next();
        fScanner.close();

        // Create shader objects
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

        // Pass shader objects to the compiler
        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        // Create new program
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        // ----------------------------------
        //GLES20.glBindAttribLocation(program, 0, "a_position");
        //GLES20.glBindAttribLocation(program, 1, "a_normal");
        // ----------------------------------

        // Link the main program
        GLES20.glLinkProgram(program);
    }

    public void draw(float[] mMVPMatrix, float[] lPosition)
    {
        /*
        verticesBuffer.limit(0);
        verticesBuffer = null;
        normalsBuffer.limit(0);
        normalsBuffer = null;
        */

        GLES20.glUseProgram(program);

        positionAttribute = GLES20.glGetAttribLocation(program, "a_position");
        MVPmtx = GLES20.glGetUniformLocation(program, "u_MVP");

        // Pass in the position
        GLES20.glVertexAttrib3f(positionAttribute, lPosition[0], lPosition[1], lPosition[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(positionAttribute);

        // Pass in the transformation matrix.
        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(MVPmtx, 1, false, mMVPMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}
