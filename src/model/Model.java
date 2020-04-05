package model;

import java.util.ArrayList;
import java.util.List;
import loader.Material;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;

public abstract class Model extends Movable
{

    protected List<Integer> activeVAOs = new ArrayList<>();
    protected List<Integer> activeVBOs = new ArrayList<>();
    protected List<Integer> nrOfIndices = new ArrayList<>();
    protected List<Material> matProperties = new ArrayList<>();

    public abstract void render(ModelShader shader);
    
    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.NORMAL_ATTRIB);
    }

    public void destroy()
    {
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }
        for (int vao : activeVAOs)
        {

            GL30.glDeleteVertexArrays(vao);
        }

        //TODO: remove textures
    }
    
    public void setMaterialProperties(int index, Material matProp)
    {
        matProperties.set(index, matProp);
    }
}
