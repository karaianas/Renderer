package com.example.karai.renderer;

/**
 * Created by karai on 9/18/2017.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.app.Activity;
import android.content.res.Resources;
import android.opengl.GLES20;

import java.io.InputStream;
import android.content.Context;
import java.nio.charset.Charset;

import android.content.res.AssetManager;

import java.io.FileReader;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.opengl.Matrix;
import android.util.Log;

public class Torus{

    private List<String> verticesList;
    private List<String> facesList;
    private List<String> normalsList;

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private FloatBuffer normalsBuffer;

    private int program;

    //float[] projectionMatrix = new float[16];
    //float[] viewMatrix = new float[16];
    //float[] productMatrix = new float[16];

    int positionAttribute;
    int normalAttribute;

    // Somehow the getAssets() throws exception
    public Torus() {

        //Context context = getApplicationContext();

        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        normalsList = new ArrayList<>();

        // This is the only way to get context
        MyApp myApp = new MyApp();
        Context context = myApp.getAppContext();

        InputStream is = context.getResources().openRawResource(R.raw.torus);
        //InputStream is = Resources.getSystem().openRawResource(R.raw.torus);

        Scanner scanner = new Scanner(is);

        // Open the OBJ file with a Scanner
        // Somehow this throws an IO exception
        //Scanner scanner = new Scanner(context.getAssets().open("torus.obj"));

        // Loop through all its lines
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.startsWith("v ")) {
                // Add vertex line to list of vertices
                verticesList.add(line);
                //Log.d("STATE", line);
            } else if(line.startsWith("f ")) {

                // Add face line to faces list
                // ***Optimize the pattern matching later. Currently it's matching all 6 numbers
                Pattern p = Pattern.compile("(\\d+)//(\\d+) (\\d+)//(\\d+) (\\d+)//(\\d+)");
                Matcher m = p.matcher(line);
                m.find();
                String v1 = m.group(1);
                String v2 = m.group(3);
                String v3 = m.group(5);

                //Log.d("STATE", v1 + " " + v2 + " " + v3);

                //facesList.add(line);
                facesList.add(v1 + " " + v2 + " " + v3);
            }
            else if(line.startsWith("vn "))
            {
                normalsList.add(line);
                //Log.d("STATE", line);
            }
        }

        // Close the scanner
        scanner.close();

        // Create buffer for vertices
        ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
        buffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = buffer1.asFloatBuffer();

        // Create buffer for faces
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
        buffer2.order(ByteOrder.nativeOrder());
        facesBuffer = buffer2.asShortBuffer();

        // Create buffer for (vertex) normals
        ByteBuffer buffer3 = ByteBuffer.allocateDirect(normalsList.size() * 3 * 4);
        buffer3.order(ByteOrder.nativeOrder());
        normalsBuffer = buffer3.asFloatBuffer();

        for(String vertex: verticesList) {
            String coords[] = vertex.split(" "); // Split by space
            float x = Float.parseFloat(coords[1]);
            float y = Float.parseFloat(coords[2]);
            float z = Float.parseFloat(coords[3]);
            verticesBuffer.put(x);
            verticesBuffer.put(y);
            verticesBuffer.put(z);
            //Log.d("STATE", "x:" + x + " y:" + y + " z:" + z);
        }
        verticesBuffer.position(0);

        //verticesBuffer = ByteBuffer.allocateDirect(verticesList.size() * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //verticesBuffer.position(0);

        for(String face: facesList) {
            String vertexIndices[] = face.split(" ");
            //Log.d("STATE", "x:" + vertexIndices[0] + " y:" + vertexIndices[1] + " z:" + vertexIndices[2]);
            short vertex1 = Short.parseShort(vertexIndices[0]);
            short vertex2 = Short.parseShort(vertexIndices[1]);
            short vertex3 = Short.parseShort(vertexIndices[2]);
            facesBuffer.put((short)(vertex1 - 1));
            facesBuffer.put((short)(vertex2 - 1));
            facesBuffer.put((short)(vertex3 - 1));
        }
        facesBuffer.position(0);

        for(String normal: normalsList)
        {
            String values[] = normal.split(" ");
            float x = Float.parseFloat(values[1]);
            float y = Float.parseFloat(values[2]);
            float z = Float.parseFloat(values[3]);
            normalsBuffer.put(x);
            normalsBuffer.put(y);
            normalsBuffer.put(z);
            //Log.d("STATE", "x:" + x + " y:" + y + " z:" + z);
        }
        normalsBuffer.position(0);

        // Convert vertex_shader.txt to a string
        Scanner vScanner = new Scanner( context.getResources().openRawResource(R.raw.vertex_shader), "UTF-8" );
        String vertexShaderCode = vScanner.useDelimiter("\\A").next();
        vScanner.close();
        //Log.d("STATE", vertexShaderCode);

        Scanner fScanner = new Scanner( context.getResources().openRawResource(R.raw.fragment_shader), "UTF-8" );
        String fragmentShaderCode = fScanner.useDelimiter("\\A").next();
        fScanner.close();
        //Log.d("STATE", fragmentShaderCode);

        // Somehow IOUtils.toString() method throws exception
        //InputStream vertexShaderStream = context.getResources().openRawResource(R.raw.vertex_shader);
        //String vertexShaderCode = IOUtils.toString(vertexShaderStream, Charset.defaultCharset());
        //vertexShaderStream.close();

        // Convert fragment_shader.txt to a string
        //InputStream fragmentShaderStream = context.getResources().openRawResource(R.raw.fragment_shader);
        //String fragmentShaderCode = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset());
        //fragmentShaderStream.close();

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
        GLES20.glBindAttribLocation(program, 0, "a_position");
        GLES20.glBindAttribLocation(program, 1, "a_normal");
        // ----------------------------------

        // Link and start using the program
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

    }

    public void draw(float[] mMVPMatrix)
    {
        /*
        int position = GLES20.glGetAttribLocation(program, "position");
        GLES20.glEnableVertexAttribArray(position);

        GLES20.glVertexAttribPointer(position,
                3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);


        int matrix = GLES20.glGetUniformLocation(program, "matrix");
        //GLES20.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0);
        GLES20.glUniformMatrix4fv(matrix, 1, false, mMVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        GLES20.glDisableVertexAttribArray(position);
        */

        positionAttribute = GLES20.glGetAttribLocation(program, "a_position");
        normalAttribute = GLES20.glGetAttribLocation(program, "a_normal");


        verticesBuffer.position(0);
        GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionAttribute);
        //int position = GLES20.glGetAttribLocation(program, "a_position");
        //GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        //GLES20.glEnableVertexAttribArray(position);

        normalsBuffer.position(0);
        GLES20.glVertexAttribPointer(normalAttribute, 3, GLES20.GL_FLOAT, false, 3 * 4, normalsBuffer);
        GLES20.glEnableVertexAttribArray(normalAttribute);
        //Log.d("STATE", "values: " + normalAttribute);


        int matrix = GLES20.glGetUniformLocation(program, "matrix");
        //GLES20.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0);
        GLES20.glUniformMatrix4fv(matrix, 1, false, mMVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        Log.d("STATE", "values: " + positionAttribute + " " + normalAttribute + " " + matrix);

        //GLES20.glDisableVertexAttribArray(position);

    }
}