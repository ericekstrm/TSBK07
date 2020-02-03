package model;

import org.lwjgl.opengl.GL30;

public class TexturedModel extends Model {

    public TexturedModel(float[] vertices, int[] indices, float[] textures)
    {
        super();
        
        loadVertexVBO(0, vertices);
        activeAttribs.add(0);
        loadIndicesVBO(indices);
        loadTextureVBO(2, textures);
        activeAttribs.add(2);

        GL30.glBindVertexArray(0);
    }
}
