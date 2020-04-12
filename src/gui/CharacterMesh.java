package gui;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class CharacterMesh
{

    public int vaoID;
    List<Integer> activeVBOs = new ArrayList<>();
    public int nrOfIndices;

    public CharacterMesh(int vaoID, List<Integer> activeVBOs, int nrOfIndices)
    {
        this.vaoID = vaoID;
        this.nrOfIndices = nrOfIndices;
    }

    public void destroy()
    {
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }

        GL30.glDeleteVertexArrays(vaoID);
    }
}
