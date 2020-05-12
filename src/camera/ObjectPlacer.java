package camera;

import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import loader.Loader;
import model.Model;
import static org.lwjgl.glfw.GLFW.*;
import shader.ModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class ObjectPlacer extends RayCaster
{

    List<Model> allModels = new ArrayList<>();

    int currentModel;
    float modelScale;
    float modelHeight;
    ModelShader shader;

    public ObjectPlacer(Matrix4f projectionMatrix)
    {
        super(projectionMatrix);
        shader = new ModelShader(projectionMatrix);
        currentModel = 0;
        modelScale = 1;
        modelHeight = 0;

        allModels = Loader.loadAllObjects();
    }

    public void update(long window, Camera camera, TerrainHandler terrain)
    {
        //updates ray
        super.update(window, camera);

        Vector3f terrainPos = getTerrainPosition(terrain, camera);
        terrainPos.y += modelHeight;
        allModels.get(currentModel).setPosition(terrainPos);

        if (glfwGetKey(window, GLFW_KEY_KP_ADD) == GLFW_PRESS)
        {
            modelScale += 0.05f;
        }
        if (glfwGetKey(window, GLFW_KEY_KP_SUBTRACT) == GLFW_PRESS)
        {
            modelScale -= 0.05f;
        }
        allModels.get(currentModel).setScale(modelScale, modelScale, modelScale);

        if (glfwGetKey(window, GLFW_KEY_KP_MULTIPLY) == GLFW_PRESS)
        {
            allModels.get(currentModel).rotate(0, 1, 0);
        }

        if (glfwGetKey(window, GLFW_KEY_KP_8) == GLFW_PRESS)
        {
            modelHeight += 0.1f;
        }
        if (glfwGetKey(window, GLFW_KEY_KP_2) == GLFW_PRESS)
        {
            modelHeight -= 0.1f;
        }
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane, Matrix4f projectionMatrix)
    {
        //render objects
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);

        //render
        allModels.get(currentModel).render(shader);

        shader.stop();
    }

    public Model getModel()
    {
        return allModels.get(currentModel);
    }

    public void nextModel()
    {
        currentModel++;
        if (currentModel == allModels.size())
        {
            currentModel = 0;
        }
        modelScale = 1;
        modelHeight = 0;
    }

    public void prevModel()
    {
        currentModel--;
        if (currentModel < 0)
        {
            currentModel = allModels.size() - 1;
        }
        modelScale = 1;
        modelHeight = 0;
    }
}
