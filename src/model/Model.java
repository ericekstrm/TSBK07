package model;

import loader.MaterialProperties;
import loader.RawData;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import loader.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;

public class Model extends Movable
{

    protected List<Texture> textureIDs = new ArrayList<>();
    protected List<Integer> activeVAOs = new ArrayList<>();
    protected List<Integer> activeVBOs = new ArrayList<>();
    protected List<Integer> nrOfIndices = new ArrayList<>();
    protected List<MaterialProperties> matProperties = new ArrayList<>();

    public Model(Shader shader, RawData data)
    {
        for (int i = 0; i < data.indices.size(); i++)
        {
            //add new vao to list
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            //add data that is the same for all vaos (this is where there is a lot of memory waste.)
            activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));
            activeVBOs.add(ModelLoader.loadTextureVBO(data.textureCoords));
            activeVBOs.add(ModelLoader.loadNormalsVBO(data.normals));

            //add data that is specific to that vao
            activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices.get(i)));
            nrOfIndices.add(data.indices.get(i).length);

            //materials
            matProperties.add(data.matprop.get(i));

            //texture binding
            Texture tex = data.textures.get(i);
            for (int j = 0; j < tex.size(); j++)
            {
                //glActiveTexture(GL_TEXTURE0 + j);
                //glBindTexture(GL_TEXTURE_2D, tex.get(j));
                textureIDs.add(tex);
            }

            GL30.glBindVertexArray(0);
        }
    }

    public void render(ModelShader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToViewMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i).Ka,
                                                  matProperties.get(i).Kd,
                                                  matProperties.get(i).Ks,
                                                  matProperties.get(i).specularExponent);
            //textures
            for (int j = 0; j < textureIDs.get(i).size(); j++)
            {
                glActiveTexture(GL_TEXTURE0 + j);
                glBindTexture(GL_TEXTURE_2D, textureIDs.get(i).get(j));
            }

            //draw!
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

    public void setMaterialProperties(int index, MaterialProperties matProp)
    {
        matProperties.set(index, matProp);
    }

    public void update()
    {
    }
}
