package model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Model
{

    int vaoID;
    List<Integer> activeVBOs = new ArrayList<>();
    List<Integer> activeAttribs = new ArrayList<>();
    
    int nrIndices = 0;

    public Model()
    {
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
    }

    protected void loadVertexVBO(int attribIndex, float[] vertices)
    {
        int vertexVBO = GL15.glGenBuffers();
        activeVBOs.add(vertexVBO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        FloatBuffer vertexBuffer = createFloatBuffer(vertices);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribIndex, 3, GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    protected void loadIndicesVBO(int[] indices)
    {
        int indexVBO = GL15.glGenBuffers();
        activeVBOs.add(indexVBO);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        IntBuffer indexBuffer = createIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        
        nrIndices = indices.length;
    }

    protected void loadColorVBO(int attribIndex, float[] colors)
    {
        int colorVBO = GL15.glGenBuffers();
        activeVBOs.add(colorVBO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVBO);
        FloatBuffer colorBuffer = createFloatBuffer(colors);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribIndex, 4, GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    protected void loadTextureVBO(int attribIndex, float[] colors)
    {
        int textureVBO = GL15.glGenBuffers();
        activeVBOs.add(textureVBO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVBO);
        FloatBuffer colorBuffer = createFloatBuffer(colors);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribIndex, 4, GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private FloatBuffer createFloatBuffer(float[] data)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private IntBuffer createIntBuffer(int[] data)
    {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void activate()
    {
        GL30.glBindVertexArray(vaoID);

        for (int attrib : activeAttribs)
        {
            GL20.glEnableVertexAttribArray(attrib);
        }
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        for (int attrib : activeAttribs)
        {
            GL20.glDisableVertexAttribArray(attrib);
        }
    }

    public void destroy()
    {
        deactivate();
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }

        GL30.glDeleteVertexArrays(vaoID);
    }
    
    public int getVaoID()
    {
        return vaoID;
    }

    public int getNrIndices()
    {
        return nrIndices;
    }
}
