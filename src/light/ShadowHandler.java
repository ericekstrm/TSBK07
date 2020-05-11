package light;

import camera.Camera;
import camera.Player;
import framebuffer.DepthFrameBuffer;
import model.Model;
import model.ModelHandler;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import shader.ShadowShader;
import static state.LevelEditorState.farPlane;
import static state.LevelEditorState.nearPlane;
import terrain.Terrain;
import terrain.TerrainHandler;
import util.Matrix4f;

public class ShadowHandler
{

    float shadowBoxSize = 200;

    DepthFrameBuffer shadowMap;
    Matrix4f shadowProjectionMatrix = Matrix4f.orthographic(-shadowBoxSize, shadowBoxSize, -shadowBoxSize, shadowBoxSize, nearPlane, farPlane);//Matrix4f.shadowProjectionMatrix(20, nearPlane, farPlane);
    Matrix4f lightSpaceMatrix;

    ShadowShader shader;

    public ShadowHandler(LightHandler lights)
    {
        shadowMap = new DepthFrameBuffer();

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
        lightSpaceMatrix = shadowProjectionMatrix.multiply(camera.getWorldtoViewMatrix());

        GL11.glCullFace(GL11.GL_FRONT);

        //prepare
        shadowMap.bindFrameBuffer();
        shader.start();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);

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

        GL11.glCullFace(GL11.GL_BACK);
        //player
        shader.loadModelToViewMatrix(player.getModel().getModelToWorldMatrix(), camera);
        renderModel(player.getModel());

        shader.stop();
        shadowMap.unbindFrameBuffer();
    }

    public void render(Camera camera, ModelHandler models, TerrainHandler terrain)
    {
        lightSpaceMatrix = shadowProjectionMatrix.multiply(camera.getWorldtoViewMatrix());

        //prepare
        shadowMap.bindFrameBuffer();
        shader.start();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);

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
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

            if (m.getTextureIDs().get(i) != 0)
            {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, m.getTextureIDs().get(i));
                shader.loadHasTexture(true);
            } else
            {
                shader.loadHasTexture(false);
            }

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
