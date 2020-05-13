package light;

import java.util.ArrayList;
import java.util.List;
import model.ModelLoader;
import loader.RawData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import loader.Loader;
import model.Movable;
import shader.LightShader;
import util.Vector3f;

public class PositionalLight extends Movable
{

    private int activeVAO;
    private List<Integer> activeVBOs = new ArrayList<>();
    private int nrIndices = 0;

    private Vector3f color;
    private float intensity = 1;
    private float radius = 10;
    
    public PositionalLight(Vector3f position, Vector3f color)
    {
        setPosition(position);
        setScale(radius, radius, radius);
        this.color = color;
        RawData data = Loader.loadObj("light.obj")[0];
        activeVAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(activeVAO);

        activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));

        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrIndices = data.indices.length;

        GL30.glBindVertexArray(0);
    }

    public void render(LightShader shader)
    {
        GL30.glBindVertexArray(activeVAO);
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);

        shader.loadModelToWorldMatrix(getModelToWorldMatrix());
        shader.loadColor(color);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrIndices, GL11.GL_UNSIGNED_INT, 0);
        deactivate();
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
    }

    public void destroy()
    {
        deactivate();
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }
        GL30.glDeleteVertexArrays(activeVAO);
    }
    
    public Vector3f getColor()
    {
        return color;
    }

    public float getR()
    {
        return radius;
    }

    public float getIntensity()
    {
        return intensity;
    }
}
