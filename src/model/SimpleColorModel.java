package model;

import org.lwjgl.opengl.GL30;

public class SimpleColorModel extends Model
{

    /**
     * A simple model with indexed points, colored by each vertex.
     *
     * @param vertices
     * @param indices
     * @param colors
     */
    public SimpleColorModel(float[] vertices, int[] indices, float[] colors)
    {
        super();

        loadVertexVBO(0, vertices);
        activeAttribs.add(0);
        loadIndicesVBO(indices);
        loadColorVBO(1, colors);
        activeAttribs.add(1);

        GL30.glBindVertexArray(0);
    }
}
