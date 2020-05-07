package light;

import camera.Camera;
import camera.Player;
import framebuffer.DepthFrameBuffer;
import model.Model;
import model.ModelHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import shader.ShadowShader;
import static state.GameState.farPlane;
import static state.GameState.nearPlane;
import terrain.Terrain;
import terrain.TerrainHandler;
import util.Matrix4f;

public class ShadowHandler
{

    DepthFrameBuffer shadowMap;
    Matrix4f shadowProjectionMatrix = Matrix4f.shadowProjectionMatrix(30, nearPlane, farPlane);
    Matrix4f lightSpaceMatrix;

    ShadowShader shader;

    public ShadowHandler(LightHandler lights)
    {
        shadowMap = new DepthFrameBuffer();
        lightSpaceMatrix = shadowProjectionMatrix.multiply(lights.getSun().getSunCamera().getWorldtoViewMatrix());

        shader = new ShadowShader(shadowProjectionMatrix);
    }

    /**
     * Render pass for shadows.
     *
     * @param camera - the camera that is placed at the light that is to cast
     * the shadow.
     * @param models
     * @param terrain
     * @param player
     */
    public void render(Camera camera, ModelHandler models, TerrainHandler terrain, Player player)
    {
        //prepare
        shadowMap.bindFrameBuffer();
        shader.start();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);

        //terrain
        for (Terrain t : terrain.getTerrainTiles().values())
        {
            shader.loadModelToViewMatrix(t.getModelToWorldMatrix(), camera);

            renderModel(t);
        }

        //models
        for (Model m : models.getModels())
        {
            shader.loadModelToViewMatrix(m.getModelToWorldMatrix(), camera);
            renderModel(m);
        }

        //player
        shader.loadModelToViewMatrix(player.getModel().getModelToWorldMatrix(), camera);
        renderModel(player.getModel());

        GL11.glCullFace(GL11.GL_BACK);
        shader.stop();
        shadowMap.unbindFrameBuffer();
    }

    private void renderModel(Model m)
    {
        for (int i = 0; i < m.getActiveVAOs().size(); i++)
        {
            GL30.glBindVertexArray(m.getActiveVAOs().get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, m.getNrOfIndices().get(i), GL11.GL_UNSIGNED_INT, 0);
        }
    }

    public int getDepthMap()
    {
        return shadowMap.getDepthMap();
    }

    public Matrix4f getLightSpaceMatrix()
    {
        return lightSpaceMatrix;
    }
}
