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
import util.Matrix4f;

public class Model extends Movable
{

    List<Integer> textureIDs = new ArrayList<>();
    List<Integer> activeVAOs = new ArrayList<>();
    List<Integer> activeVBOs = new ArrayList<>();
    List<Integer> nrOfIndices = new ArrayList<>();
    
    List<Matrix4f> internalTransform = new ArrayList<>();

    public Model(Shader shader, RawData... datas)
    {
        for (RawData data : datas)
        {
            internalTransform.add(new Matrix4f());
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
        activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));

        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrOfIndices.add(data.indices.length);

        activeVBOs.add(ModelLoader.loadTextureVBO(data.textureCoords));

        activeVBOs.add(ModelLoader.loadNormalsVBO(data.normals));

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
            getModelToViewMatrix().multiply(internalTransform.get(i)).toBuffer(translation);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "modelToWorld"), false, translation);

            //draw!
            glBindTexture(GL_TEXTURE_2D, textureIDs.get(i));
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
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
    
    public void setInternalTransform(int VAOindex, Matrix4f transform)
    {
        internalTransform.set(VAOindex, transform);
    }
    
    public void update()
    {
        internalTransform.set(3, internalTransform.get(3).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(4, internalTransform.get(4).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(5, internalTransform.get(5).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(6, internalTransform.get(6).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
    }
}
