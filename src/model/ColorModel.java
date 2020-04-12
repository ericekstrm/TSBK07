package model;

import loader.RawData;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;

public class ColorModel extends Model
{

    public ColorModel(RawData... data)
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
            GL30.glBindVertexArray(0);
        }
    }

    public ColorModel(List<RawData> data)
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

            GL30.glBindVertexArray(0);
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

            shader.loadModelToWorldMatrix(getModelToViewMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i));

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }

    public void update(float deltaTime)
    {
    }
}
