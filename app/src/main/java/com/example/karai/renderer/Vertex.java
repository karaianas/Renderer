package com.example.karai.renderer;

import android.util.Log;

/**
 * Created by karai on 9/19/2017.
 */

public class Vertex {
    private float p[] = new float[3];
    private float n[] = new float[3];

    public Vertex(float x, float y, float z)
    {
        p[0] = x;
        p[1] = y;
        p[2] = z;

        n[0] = 0.0f;
        n[1] = 0.0f;
        n[2] = 0.0f;
    }

    public void add_normal(float nx, float ny, float nz)
    {
        n[0] += nx;
        n[1] += ny;
        n[2] += nz;
    }

    public void print_pos()
    {
        Log.d("STATE", "Position: " + p[0] + " " + p[1] + " " + p[2]);
    }

}
