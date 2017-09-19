package com.example.karai.renderer;

/**
 * Created by karai on 9/19/2017.
 */

public class Vertex {
    private float p[] = new float[3];
    private float n[] = new float[3];

    public Vertex(float [] position)
    {
        p[0] = position[0];
        p[1] = position[1];
        p[2] = position[2];

        n[0] = 0.0f;
        n[1] = 0.0f;
        n[2] = 0.0f;
    }

    public void add_normal(float [] normal)
    {
        n[0] += normal[0];
        n[1] += normal[1];
        n[2] += normal[2];
    }

}
