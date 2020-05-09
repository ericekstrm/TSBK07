package model;

import camera.Camera;
import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import light.ShadowHandler;
import loader.Loader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shader.ModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class ModelHandler
{

    List<Model> models = new ArrayList<>();

    ModelShader shader;

    public ModelHandler(Matrix4f projectionMatrix)
    {
        shader = new ModelShader(projectionMatrix);
    }

    public void init(TerrainHandler terrain)
    {
        models = Loader.loadAllObjects();

        int i = 0;
        int j = 10;
        for (Model m : models)
        {
            i += 20;
            if (i > 300)
            {
                i = 20;
                j += 20;
            }
            m.setPosition(new Vector3f(-i, 0, -j));
        }
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane, Matrix4f projectionMatrix, ShadowHandler shadows)
    {
        //render objects
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);

        //shadows
        shader.loadLightSpaceMatrix(shadows.getLightSpaceMatrix());
        GL13.glActiveTexture(GL13.GL_TEXTURE10);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadows.getDepthMap());

        //render
        for (Model m : models)
        {
            if (!frustumCulled(m, camera))
            {
                m.render(shader);
            }
        }
        shader.stop();
    }

    public void add(Model model)
    {
        models.add(model);
    }

    public void add(Model model, int x, int y, int z)
    {
        model.setPosition(new Vector3f(x, y, z));
        models.add(model);
    }

    public void addNormalized(Model model, int x, int y, int z)
    {
        model.setPosition(new Vector3f(x, y, z));
        model.normalizeHeight();
        models.add(model);
    }

    public void destroy()
    {
        for (Model m : models)
        {
            m.destroy();
        }
    }

    /**
     *
     * @param m - model to be tested
     * @param c - the current camera
     * @return true if the models should be removed
     */
    public boolean frustumCulled(Model m, Camera c)
    {
        return false;
        /*Vector3f cameraDir = c.direction.normalize();
        Vector3f positionDir = m.position.subtract(c.position);

        if (positionDir.normalize().dot(cameraDir) < 0.70)
        {
            return true;
        } else if (positionDir.length() > main_old.farPlane)
        {
            return true;
        }
        return false;*/
    }

    public List<Model> getModels()
    {
        return models;
    }
}
