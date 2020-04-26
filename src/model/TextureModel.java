package model;

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
    protected List<Texture> bumpmapIDs = new ArrayList<>();

    public TextureModel(RawData... data)
    {
        for (RawData d : data)
        {
            //add new vao to list
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);
            
            //add data that is the same for all vaos (this is where there is a lot of memory waste.)
            activeVBOs.add(ModelLoader.loadVertexVBO(d.vertices));
            activeVBOs.add(ModelLoader.loadTextureVBO(d.textureCoords));
            activeVBOs.add(ModelLoader.loadNormalsVBO(d.normals));
            
            //add data that is specific to that vao
            activeVBOs.add(ModelLoader.loadIndicesVBO(d.indices));
            nrOfIndices.add(d.indices.length);
            
            //materials
            matProperties.add(d.material);
            
            //texture binding
            textureIDs.add(new Texture(d.material.Kd_map));
            bumpmapIDs.add(new Texture(d.material.bump_map));
            
            GL30.glBindVertexArray(0);
            
            //find the maximum height of the model.
            if (d.maxHeight > maxHeight)
            {
                maxHeight = d.maxHeight;
            }
        }
    }

    @Override
    public void render(ModelShader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToWorldMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i));
            shader.loadHasTexture(true);
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
