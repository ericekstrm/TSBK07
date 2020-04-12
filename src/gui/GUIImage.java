package gui;

import java.util.ArrayList;
import java.util.List;
import loader.Loader;
import loader.RawData;
import model.ModelLoader;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.GUIShader;
import shader.Shader;
import util.Vector2f;

public class GUIImage
{

    int vaoID;
    List<Integer> activeVBOs = new ArrayList<>();
    int nrOfIndices;

    GUIShader imageShader;

    int texture;
    Vector2f position = new Vector2f();
    Vector2f size = new Vector2f(1, 1);

    public GUIImage(int texture, Vector2f position, Vector2f size)
    {
        this.texture = texture;
        this.position = position;
        this.size = size;

        RawData data = Loader.loadQuad(position.x, position.y - size.y, size.x, size.y);
        //add new vao to list
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        //add data that is the same for all vaos (this is where there is a lot of memory waste.)
        activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));
        activeVBOs.add(ModelLoader.loadTextureVBO(data.textureCoords));

        //add data that is specific to that vao
        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrOfIndices = data.indices.length;

        GL30.glBindVertexArray(0);

        imageShader = new GUIShader();
    }

    public void render()
    {
        imageShader.start();

        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

        glActiveTexture(GL_TEXTURE0);

        //textures
        glBindTexture(GL_TEXTURE_2D, texture);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);

        imageShader.stop();
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
