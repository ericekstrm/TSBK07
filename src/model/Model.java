package model;

import loader.MaterialProperties;
import loader.RawData;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import util.Matrix4f;

public class Model extends Movable
{

    protected List<Integer> textureIDs = new ArrayList<>();
    protected List<Integer> activeVAOs = new ArrayList<>();
    protected List<Integer> activeVBOs = new ArrayList<>();
    protected List<Integer> nrOfIndices = new ArrayList<>();
    protected List<MaterialProperties> matProperties = new ArrayList<>();

    protected List<Matrix4f> internalTransform = new ArrayList<>();

    public Model(Shader shader, RawData... datas)
    {
        for (RawData data : datas)
        {
            internalTransform.add(new Matrix4f());
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            setVBOs(data);

            //materials
            matProperties.add(data.matprop);

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

            bindUniforms(shader, i);

            //draw!
            glBindTexture(GL_TEXTURE_2D, textureIDs.get(i));
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }

    protected void bindUniforms(Shader shader, int i)
    {
        //bind current model-to-world transformation
        FloatBuffer translation = BufferUtils.createFloatBuffer(16);
        getModelToViewMatrix().multiply(internalTransform.get(i)).toBuffer(translation);
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "modelToWorld"), false, translation);

        glUniform1f(glGetUniformLocation(shader.getProgramID(), "Ka"), matProperties.get(i).Ka);
        glUniform1f(glGetUniformLocation(shader.getProgramID(), "Kd"), matProperties.get(i).Kd);
        glUniform1f(glGetUniformLocation(shader.getProgramID(), "Ks"), matProperties.get(i).Ks);
        glUniform1f(glGetUniformLocation(shader.getProgramID(), "specularExponent"), matProperties.get(i).specularExponent);
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

    public void setMaterialProperties(int index, MaterialProperties matProp)
    {
        matProperties.set(index, matProp);
    }

    public void update()
    {
    }
}
