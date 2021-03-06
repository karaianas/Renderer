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

    float [] M = new float [16];
    float [] MV = new float [16];
    float [] MVP = new float [16];
    float [] position = {0.0f, 0.0f, 0.0f, 1.0f};

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

    public void setM(float [] modelMatrix)
    {
        M = modelMatrix;
    }

    public float [] getPositionMV()
    {
        float [] position_MV = new float[4];
        Matrix.multiplyMV(position_MV, 0, MV, 0, position, 0);

        return position_MV;
    }

    public void setPosition(float [] lPosition)
    {
        position = lPosition;
    }

    public void rotate(float [] R)
    {
        Matrix.multiplyMM(M, 0, R, 0, M, 0);
    }

    public void draw(float[] V, float[] P)
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
        GLES20.glVertexAttrib3f(positionAttribute, position[0], position[1], position[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(positionAttribute);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(MV, 0, V, 0, M, 0);
        Matrix.multiplyMM(MVP, 0, P, 0, MV, 0);
        GLES20.glUniformMatrix4fv(MVPmtx, 1, false, MVP, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}
