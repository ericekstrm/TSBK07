package model;

import camera.Camera;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import light.LightHandler;
import loader.Loader;
import loader.Material;
import loader.RawData;
import camera.FreeCamera;
import shader.ColorModelShader;
import shader.TextureModelShader;
import terrain.TerrainHandler;
import util.Util;
import util.Vector4f;

public class ModelHandler {
    
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
        models.put("bunny", new TextureModel(Loader.loadRawData("bunnyplus.obj", "tex2.jpg")));
        models.get("bunny").setPosition(1, 0, 1);
        models.get("bunny").setMaterialProperties(0, new Material());

        models.put("lamp_post", new ColorModel(Loader.loadObj("Lamp.obj")));
        models.get("lamp_post").setPosition(-50, 0, -50);

        models.put("lowpolymill", new ColorModel(Loader.loadObj("Low_Poly_Mill.obj")));
        models.get("lowpolymill").setPosition(0, 10, -40);
        models.get("lowpolymill").setScale(8, 8, 8);

        models.put("house", new ColorModel(Loader.loadObj("House.obj")));
        models.get("house").setPosition(-20, 10, -20);

        models.put("forrest", new ColorModel(Loader.loadObj("forrest.obj")));
        models.get("forrest").setPosition(-30, 0, -30);

        models.put("pine", new TextureModel(Loader.loadRawData("pine.obj", "pine.png")));
        models.get("pine").setPosition(-35, 0, -30);
        
        models.put("boulder", new TextureModel(Loader.loadRawData("boulder.obj", "boulder.png", "boulderNormal.png")));
        models.get("boulder").setPosition(-100, 10, -100);
        
        models.put("barrel", new TextureModel(Loader.loadRawData("barrel.obj", "barrel.png", "barrelNormal.png")));
        models.get("barrel").setPosition(-100, 10, -115);
        
        models.put("crate", new TextureModel(Loader.loadRawData("crate.obj", "crate.png", "crateNormal.png")));
        models.get("crate").setPosition(-115, 10, -100);
        models.get("crate").setScale(0.01f, 0.01f, 0.01f);

        //a bunch of trees
        List<RawData> data = Loader.loadObj("Lowpoly_tree_sample.obj");
        for (int i = 0; i < 100; i++)
        {
            Model tree = new ColorModel(data);
            float x = (float) Util.randu(400);
            float z = (float) Util.randu(400);
            tree.setPosition(x, terrain.getHeight(x, z), z);
            tree.setScale(0.3f, 0.3f, 0.3f);
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
    
    public void destroy()
    {
        for(Model m : models.values())
        {
            m.destroy();
        }
    }
}
