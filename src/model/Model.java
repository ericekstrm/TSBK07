package model;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import org.lwjgl.opengl.GL30;
import shader.Shader;

public class Model extends Movable
{

    List<Integer> textureIDs = new ArrayList<>();
    List<Integer> activeVAOs = new ArrayList<>();
    List<Integer> activeVBOs = new ArrayList<>();
    int nrIndices = 0;

    public Model(Shader shader, RawData... datas)
    {
        for (RawData data : datas)
        {
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            setVBOs(data);

            //texture binding
            glBindTexture(GL_TEXTURE_2D, data.textureID);
            glUniform1i(glGetUniformLocation(shader.getProgramID(), "texUnit"), 0);
            textureIDs.add(data.textureID);
        }
    }

    private void setVBOs(RawData data)
    {
        activeVBOs.add(ModelLoader.loadVertexVBO(Shader.POS_ATTRIB, data.vertices));

        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrIndices = data.indices.length;

        activeVBOs.add(ModelLoader.loadTextureVBO(Shader.TEX_ATTRIB, data.textureCoords));
        
        activeVBOs.add(ModelLoader.loadNormalsVBO(Shader.NORMAL_ATTRIB, data.normals));

        GL30.glBindVertexArray(0);
    }

    public void render(Shader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            //bind current model-to-world transformation
            FloatBuffer translation = BufferUtils.createFloatBuffer(16);
            getModelToViewMatrix().toBuffer(translation);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "modelToWorld"), false, translation);

            //draw!
            glBindTexture(GL_TEXTURE_2D, textureIDs.get(i));
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrIndices, GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.NORMAL_ATTRIB);
    }

    public void destroy()
    {
        deactivate();
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
}
