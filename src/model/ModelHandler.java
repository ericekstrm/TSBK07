package model;

import camera.Camera;
import java.util.HashMap;
import java.util.Map;
import light.LightHandler;
import loader.Loader;
import shader.ColorModelShader;
import shader.TextureModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector4f;

public class ModelHandler
{

    Map<String, Model> models = new HashMap<>();

    TextureModelShader textureModelShader;
    ColorModelShader colorModelShader;

    public ModelHandler(Matrix4f projectionMatrix)
    {
        textureModelShader = new TextureModelShader(projectionMatrix);
        colorModelShader = new ColorModelShader(projectionMatrix);
    }

    public void init(TerrainHandler terrain)
    {

        models.put("barrel", new TextureModel(Loader.loadObj("barrel")));
        models.get("barrel").setPosition(-10, 0, -10);
        models.get("barrel").normalizeHeight();

        models.put("boulder", new TextureModel(Loader.loadObj("boulder")));
        models.get("boulder").setPosition(-10, 0, -20);
        System.out.println(models.get("boulder").maxHeight);
        models.get("boulder").normalizeHeight();

        models.put("crate", new TextureModel(Loader.loadObj("crate")));
        models.get("crate").setPosition(-10, 0, -30);
        models.get("crate").normalizeHeight();

        models.put("fir", new TextureModel(Loader.loadObj("fir")));
        models.get("fir").setPosition(-10, 0, -40);
        models.get("fir").normalizeHeight();

        models.put("pine", new TextureModel(Loader.loadObj("pine")));
        models.get("pine").setPosition(-10, 0, -50);
        models.get("pine").normalizeHeight();

        models.put("Carrot", new ColorModel(Loader.loadObj("Carrot.obj")));
        models.get("Carrot").setPosition(-10, 0, -60);
        models.get("Carrot").normalizeHeight();

        models.put("character", new ColorModel(Loader.loadObj("character.obj")));
        models.get("character").setPosition(-10, 0, -70);
        models.get("character").normalizeHeight();

        models.put("forrest", new ColorModel(Loader.loadObj("forrest.obj")));
        models.get("forrest").setPosition(-10, 0, -80);
        models.get("forrest").normalizeHeight();

        models.put("House", new ColorModel(Loader.loadObj("House.obj")));
        models.get("House").setPosition(-10, 0, -90);
        models.get("House").normalizeHeight();

        models.put("Lamp", new ColorModel(Loader.loadObj("Lamp.obj")));
        models.get("Lamp").setPosition(-10, 0, -100);
        models.get("Lamp").normalizeHeight();

        models.put("Low_Poly_Mill", new ColorModel(Loader.loadObj("Low_Poly_Mill.obj")));
        models.get("Low_Poly_Mill").setPosition(-20, 0, -10);
        models.get("Low_Poly_Mill").normalizeHeight();

        models.put("Medieval_Inn", new ColorModel(Loader.loadObj("Medieval_Inn.obj")));
        models.get("Medieval_Inn").setPosition(-20, 0, -20);
        models.get("Medieval_Inn").normalizeHeight();

        models.put("tree1", new ColorModel(Loader.loadObj("tree1.obj")));
        models.get("tree1").setPosition(-20, 0, -30);
        models.get("tree1").normalizeHeight();

        models.put("tree2", new ColorModel(Loader.loadObj("tree2.obj")));
        models.get("tree2").setPosition(-20, 0, -40);
        models.get("tree2").normalizeHeight();

        models.put("freelowpolytreespack", new ColorModel(Loader.loadObj("freelowpolytreespack.obj")));
        models.get("freelowpolytreespack").setPosition(-20, 0, -50);
        models.get("freelowpolytreespack").normalizeHeight();

        models.put("treeX14", new TextureModel(Loader.loadObj("treeX14")));
        models.get("treeX14").setPosition(-20, 0, -60);
        models.get("treeX14").normalizeHeight();

        models.put("log1", new TextureModel(Loader.loadObj("log1")));
        models.get("log1").setPosition(-20, 0, -70);
        models.get("log1").normalizeHeight();

        models.put("log2", new TextureModel(Loader.loadObj("log2")));
        models.get("log2").setPosition(-20, 0, -80);
        models.get("log2").normalizeHeight();

        models.put("stump", new TextureModel(Loader.loadObj("stump")));
        models.get("stump").setPosition(-20, 0, -90);
        models.get("stump").normalizeHeight();

        models.put("well", new TextureModel(Loader.loadObj("well")));
        models.get("well").setPosition(-20, 0, -100);
        models.get("well").normalizeHeight();

        models.put("brick_ruins", new TextureModel(Loader.loadObj("brick_ruins")));
        models.get("brick_ruins").setPosition(-30, 0, -10);
        models.get("brick_ruins").normalizeHeight();

        models.put("house", new TextureModel(Loader.loadObj("house")));
        models.get("house").setPosition(-30, 0, -20);
        models.get("house").normalizeHeight();

        models.put("house2", new TextureModel(Loader.loadObj("house2")));
        models.get("house2").setPosition(-30, 0, -30);
        models.get("house2").normalizeHeight();

        models.put("grass", new TextureModel(Loader.loadObj("grass")));
        models.get("grass").setPosition(-30, 0, -40);
        models.get("grass").normalizeHeight();
        
        models.put("chair", new TextureModel(Loader.loadObj("chair")));
        models.get("chair").setPosition(-30, 0, -50);
        models.get("chair").normalizeHeight();
        
        models.put("sewer_bricks", new TextureModel(Loader.loadObj("sewer_bricks")));
        models.get("sewer_bricks").setPosition(-30, 0, -60);
        models.get("sewer_bricks").normalizeHeight();
        
        models.put("wellcome_sign", new TextureModel(Loader.loadObj("wellcome_sign")));
        models.get("wellcome_sign").setPosition(-30, 0, -70);
        models.get("wellcome_sign").normalizeHeight();

        models.put("barn", new TextureModel(Loader.loadObj("barn")));
        models.get("barn").setPosition(-30, 0, -80);
        models.get("barn").normalizeHeight();
        
        models.put("alpine_cabin", new TextureModel(Loader.loadObj("alpine_cabin")));
        models.get("alpine_cabin").setPosition(-30, 0, -90);
        models.get("alpine_cabin").normalizeHeight();
        
        models.put("potted_plant", new TextureModel(Loader.loadObj("potted_plant")));
        models.get("potted_plant").setPosition(-30, 0, -100);
        models.get("potted_plant").normalizeHeight();
        
        models.put("bucket", new TextureModel(Loader.loadObj("bucket")));
        models.get("bucket").setPosition(-40, 0, -10);
        models.get("bucket").normalizeHeight();
        
        models.put("street_plant", new TextureModel(Loader.loadObj("street_plant")));
        models.get("street_plant").setPosition(-40, 0, -20);
        models.get("street_plant").normalizeHeight();
        
        //models.put("sponza", new TextureModel(Loader.loadObj("sponza")));
        //models.get("sponza").setPosition(-100, 1, -10);
        //models.get("sponza").setScale(0.02f, 0.02f, 0.02f);

        /*RawData[] data = Loader.loadObj("wooden_fence");
        for (int i = 0; i < 10; i++)
        {
            Model fence = new ColorModel(data);
            fence.setPosition(70 + 1 * i, -1, 140 + 5.2f * i);

            models.put("fence" + i, fence);
        }

        //a bunch of trees
        data = Loader.loadObj("tree1.obj");
        for (int i = 0; i < 20; i++)
        {
            Model tree = new ColorModel(data);
            float x = 0;
            float z = 0;
            float y = 0;
            do
            {
                x = (float) Util.rand(0, 200);
                z = (float) Util.rand(0, 200);
                y = terrain.getHeight(x, z);
            } while (y < -0.5f);
            tree.setPosition(x, y, z);
            float scale = Util.rand(2, 3) * 0.3f;
            tree.setScale(scale, scale, scale);
            tree.setRotation(0, Util.rand(0, 360), 0);
            //models.put("tree" + i, tree);
        }*/
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane)
    {
        //render objects
        textureModelShader.start();
        textureModelShader.loadLights(lights.getPointLights(), lights.getDirLights());
        textureModelShader.loadWorldToViewMatrix(camera);
        textureModelShader.loadClippingPlane(clippingPlane);
        textureModelShader.stop();

        colorModelShader.start();
        colorModelShader.loadLights(lights.getPointLights(), lights.getDirLights());
        colorModelShader.loadWorldToViewMatrix(camera);
        colorModelShader.loadClippingPlane(clippingPlane);
        colorModelShader.stop();

        //render
        for (Model m : models.values())
        {
            if (!frustumCulled(m, camera))
            {
                if (m instanceof ColorModel)
                {
                    colorModelShader.start();
                    m.render(colorModelShader);
                    colorModelShader.stop();
                } else if (m instanceof TextureModel)
                {
                    textureModelShader.start();
                    m.render(textureModelShader);
                    textureModelShader.stop();
                }
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

    public Map<String, Model> getModels()
    {
        return models;
    }
}
