package model;

import camera.Camera;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import light.LightHandler;
import loader.Loader;
import loader.RawData;
import shader.ColorModelShader;
import shader.TextureModelShader;
import terrain.TerrainHandler;
import util.Util;
import util.Vector4f;

public class ModelHandler
{

    Map<String, Model> models = new HashMap<>();
    Windmill windmill;

    TextureModelShader shader;
    ColorModelShader colorModelShader;

    public ModelHandler()
    {
        shader = new TextureModelShader();
        colorModelShader = new ColorModelShader();
    }

    public void init(TerrainHandler terrain)
    {
        //models

        models.put("lamp_post", new ColorModel(Loader.loadObj("Lamp.obj")));
        models.get("lamp_post").setPosition(60, 0, 60);
        models.get("lamp_post").setScale(2, 2, 2);

        models.put("pine", new TextureModel(Loader.loadRawData("pine.obj", "pine.png")));
        models.get("pine").setPosition(30, 0, 20);

        models.put("tree2", new TextureModel(Loader.loadObj("tree2.obj")));
        models.get("tree2").setPosition(40, 0, 20);

        List<RawData> data = Loader.loadObj("wooden_fence.obj");
        for (int i = 0; i < 10; i++)
        {
            Model fence = new ColorModel(data);
            fence.setPosition(70 + 1*i, -1, 140 + 5.2f* i);

            models.put("fence" + i, fence);
        }

        //a bunch of trees
        data = Loader.loadObj("tree1.obj");
        for (int i = 0; i < 200; i++)
        {
            Model tree = new ColorModel(data);
            float x = 0;
            float z = 0;
            float y = 0;
            do
            {
                x = (float) Util.rand(0, 400);
                z = (float) Util.rand(0, 400);
                y = terrain.getHeight(x, z);
            } while (y < -0.5f);
            tree.setPosition(x, y, z);
            float scale = Util.rand(2, 3) * 0.3f;
            tree.setScale(scale, scale, scale);
            tree.setRotation(0, Util.rand(0, 360), 0);
            models.put("tree" + i, tree);
        }
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane)
    {
        //render objects
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);
        shader.stop();

        colorModelShader.start();
        colorModelShader.loadLights(lights.getPointLights(), lights.getDirLights());
        colorModelShader.loadWorldToViewMatrix(camera);
        colorModelShader.loadClippingPlane(clippingPlane);
        colorModelShader.stop();

        //render
        for (Model m : models.values())
        {
            if (m instanceof ColorModel)
            {
                colorModelShader.start();
                m.render(colorModelShader);
                colorModelShader.stop();
            } else if (m instanceof TextureModel)
            {
                shader.start();
                m.render(shader);
                shader.stop();
            }
        }
    }
    
    public Model get(String name)
    {
        return models.get(name);
    }

    public void set(String name, Model model)
    {
        models.put(name, model);
    }
    
    public void destroy()
    {
        for (Model m : models.values())
        {
            m.destroy();
        }
    }
}
