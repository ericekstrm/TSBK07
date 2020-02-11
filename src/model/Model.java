package model;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;

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

    public void render(Shader shader)
    {
        activate();
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrIndices, GL11.GL_UNSIGNED_INT, 0);
        deactivate();
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
