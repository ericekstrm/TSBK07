package water;

import loader.Loader;
import main.Camera;
import model.Model;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import water.WaterShader;

public class Water extends Model
{

    WaterShader shader;

    public Water(WaterShader shader)
    {
        super(shader, Loader.loadRawData("water.obj", "water.jpg"));
        this.shader = shader;
    }

    public void render(Camera camera)
    {
        shader.start();
        shader.loadWorldToViewMatrix(camera);
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToViewMatrix());

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureIDs.get(i).get(0));

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(0), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
            shader.stop();
        }
    }
}
