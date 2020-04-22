package light;

import camera.Camera;
import loader.Loader;
import model.ColorModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.LightShader;
import shader.Shader;
import util.Matrix4f;
import util.Vector3f;

public class Sun extends ColorModel
{

    LightShader shader;

    Vector3f color = new Vector3f(1, 0.7f, 0.7f);

    public Sun(Vector3f position, Matrix4f projectionMatrix)
    {
        super(Loader.loadObj("sun"));
        setPosition(position);

        shader = new LightShader(projectionMatrix);
    }

    public void render(Camera camera)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
        shader.start();
        //shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrixNoTranslation(camera);
        shader.loadModelToWorldMatrix(getModelToWorldMatrix());
        shader.loadColor(color);

        GL30.glBindVertexArray(activeVAOs.get(0));
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(0), GL11.GL_UNSIGNED_INT, 0);
        deactivate();
        shader.stop();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    DirectionalLight getDirLight()
    {
        return new DirectionalLight(position.normalize().scale(-1), color);
    }
}
