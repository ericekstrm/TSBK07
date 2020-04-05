package model;

import loader.Material;
import loader.RawData;
import java.util.ArrayList;
import java.util.List;
import loader.Texture;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;

public class TextureModel extends Model
{

    protected List<Texture> textureIDs = new ArrayList<>();

    public TextureModel(Shader shader, RawData... data)
    {
        for (int i = 0; i < data.length; i++)
        {
            //add new vao to list
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            //add data that is the same for all vaos (this is where there is a lot of memory waste.)
            activeVBOs.add(ModelLoader.loadVertexVBO(data[i].vertices));
            activeVBOs.add(ModelLoader.loadTextureVBO(data[i].textureCoords));
            activeVBOs.add(ModelLoader.loadNormalsVBO(data[i].normals));

            //add data that is specific to that vao
            activeVBOs.add(ModelLoader.loadIndicesVBO(data[i].indices));
            nrOfIndices.add(data[i].indices.length);

            //materials
            matProperties.add(data[i].material);

            //texture binding
            textureIDs.add(data[i].textures);

            GL30.glBindVertexArray(0);
        }
    }

    public TextureModel(Shader shader, List<RawData> data)
    {
        for (int i = 0; i < data.size(); i++)
        {
            //add new vao to list
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            //add data that is the same for all vaos (this is where there is a lot of memory waste.)
            activeVBOs.add(ModelLoader.loadVertexVBO(data.get(i).vertices));
            activeVBOs.add(ModelLoader.loadTextureVBO(data.get(i).textureCoords));
            activeVBOs.add(ModelLoader.loadNormalsVBO(data.get(i).normals));

            //add data that is specific to that vao
            activeVBOs.add(ModelLoader.loadIndicesVBO(data.get(i).indices));
            nrOfIndices.add(data.get(i).indices.length);

            //materials
            matProperties.add(data.get(i).material);

            //texture binding
            textureIDs.add(data.get(i).textures);

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
            shader.loadMaterialLightingProperties(matProperties.get(i));
            //textures
            if (textureIDs.get(i) != null)
            {
                for (int j = 0; j < textureIDs.get(i).size(); j++)
                {
                    glActiveTexture(GL_TEXTURE0 + j);
                    glBindTexture(GL_TEXTURE_2D, textureIDs.get(i).get(j));
                }
            }

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }

    @Override
    public void destroy()
    {
        super.destroy();
        
        //TODO: remove textures
    }

    public void update(float deltaTime)
    {
    }
}
