package model;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import org.lwjgl.opengl.GL15;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class SimpleModel {
    
    int vaoID;
    int vboID;
    
    
    
    
    public void load()
    {
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = loadVertices();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    }
    
    private FloatBuffer createVertexBuffer()
    {
        
    }
    
    
    
    public static SimpleModel loadModel(String filename)
    {
        SimpleModel model = new SimpleModel();
        
        return model;
    }
}
