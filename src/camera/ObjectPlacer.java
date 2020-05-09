package camera;

import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import loader.Loader;
import model.Model;
import shader.ModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector4f;

public class ObjectPlacer extends RayCaster
{

    List<Model> allModels = new ArrayList<>();

    int currentModel;
    ModelShader shader;

    public ObjectPlacer(Matrix4f projectionMatrix)
    {
        super(projectionMatrix);
        shader = new ModelShader(projectionMatrix);
        currentModel = 0;
        
        //allModels = Loader.loadAllObjects();
    }

    public void update(long window, Camera camera, TerrainHandler terrain)
    {
        //updates ray
        super.update(window, camera);

        allModels.get(currentModel).setPosition(getTerrainPosition(terrain, camera));
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
    }

    public void prevModel()
    {
        currentModel--;
        if (currentModel < 0)
        {
            currentModel = allModels.size() - 1;
        }
    }
}
