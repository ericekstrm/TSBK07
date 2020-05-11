package model;

import camera.Camera;
import camera.ProjectionMatrix;
import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import light.ShadowHandler;
import loader.SceneLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shader.ModelShader;
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

    public void init()
    {
        //models = SceneLoader.loadModels("test");
        add(new Model("barrel"), 0, 1, 0);
        add(new Model("boulder"), 0, 1, 10);
        add(new Model("crate"), 10, 1, 10);
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
            if (ProjectionMatrix.isModelInFrustum(m, camera))
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

    public void destroy()
    {
        for (Model m : models)
        {
            m.destroy();
        }
    }

    public List<Model> getModels()
    {
        return models;
    }
}
