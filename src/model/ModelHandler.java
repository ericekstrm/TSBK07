package model;

import camera.Camera;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import light.LightHandler;
import light.ShadowHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shader.ModelShader;
import shader.TextureModelShader;
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
        shader = new TextureModelShader(projectionMatrix);
    }

    public void init(TerrainHandler terrain)
    {

        addNormalized(new Model("barrel"), -3, 0, -3);
        addNormalized(new Model("boulder"), -10, 0, -20);
        addNormalized(new Model("crate"), -80, -4, -30);
        addNormalized(new Model("pine"), -10, 0, -50);

        /*models.put("fir", new Model("fir"));
        models.get("fir").setPosition(-10, 0, -40);
        models.get("fir").normalizeHeight();

        models.put("Carrot", new Model("Carrot"));
        models.get("Carrot").setPosition(-10, 0, -60);
        models.get("Carrot").normalizeHeight();

        models.put("character", new Model("character"));
        models.get("character").setPosition(-10, 0, -70);
        models.get("character").normalizeHeight();

        models.put("forrest", new Model("forrest"));
        models.get("forrest").setPosition(-10, 0, -80);
        models.get("forrest").normalizeHeight();

        models.put("House", new Model("Floating_House"));
        models.get("House").setPosition(-10, 0, -90);
        models.get("House").normalizeHeight();

        models.put("Lamp", new Model("Lamp"));
        models.get("Lamp").setPosition(-10, 0, -100);
        models.get("Lamp").normalizeHeight();

        models.put("Low_Poly_Mill", new Model("Low_Poly_Mill"));
        models.get("Low_Poly_Mill").setPosition(-20, 0, -10);
        models.get("Low_Poly_Mill").normalizeHeight();

        models.put("Medieval_Inn", new Model("Medieval_Inn"));
        models.get("Medieval_Inn").setPosition(-20, 0, -20);
        models.get("Medieval_Inn").normalizeHeight();

        models.put("tree1", new Model("tree1"));
        models.get("tree1").setPosition(-20, 0, -30);
        models.get("tree1").normalizeHeight();

        models.put("tree2", new Model("tree2"));
        models.get("tree2").setPosition(-20, 0, -40);
        models.get("tree2").normalizeHeight();

        models.put("freelowpolytreespack", new Model("freelowpolytreespack"));
        models.get("freelowpolytreespack").setPosition(-20, 0, -50);
        models.get("freelowpolytreespack").normalizeHeight();

        models.put("treeX14", new Model("treeX14"));
        models.get("treeX14").setPosition(-20, 0, -60);
        models.get("treeX14").normalizeHeight();

        models.put("log1", new Model("log1"));
        models.get("log1").setPosition(-20, 0, -70);
        models.get("log1").normalizeHeight();

        models.put("log2", new Model("log2"));
        models.get("log2").setPosition(-20, 0, -80);
        models.get("log2").normalizeHeight();

        models.put("stump", new Model("stump"));
        models.get("stump").setPosition(-20, 0, -90);
        models.get("stump").normalizeHeight();

        models.put("well", new Model("well"));
        models.get("well").setPosition(-20, 0, -100);
        models.get("well").normalizeHeight();

        models.put("brick_ruins", new Model("brick_ruins"));
        models.get("brick_ruins").setPosition(-30, 0, -10);
        models.get("brick_ruins").normalizeHeight();

        models.put("house", new Model("house"));
        models.get("house").setPosition(-30, 0, -20);
        models.get("house").normalizeHeight();

        models.put("house2", new Model("house2"));
        models.get("house2").setPosition(-30, 0, -30);
        models.get("house2").normalizeHeight();

        models.put("grass", new Model("grass"));
        models.get("grass").setPosition(-30, 0, -40);
        models.get("grass").normalizeHeight();
        
        models.put("chair", new Model("chair"));
        models.get("chair").setPosition(-30, 0, -50);
        models.get("chair").normalizeHeight();
        
        models.put("sewer_bricks", new Model("sewer_bricks"));
        models.get("sewer_bricks").setPosition(-30, 0, -60);
        models.get("sewer_bricks").normalizeHeight();
        
        models.put("wellcome_sign", new Model("wellcome_sign"));
        models.get("wellcome_sign").setPosition(-30, 0, -70);
        models.get("wellcome_sign").normalizeHeight();

        models.put("barn", new Model("barn"));
        models.get("barn").setPosition(-30, 0, -80);
        models.get("barn").normalizeHeight();
        
        models.put("alpine_cabin", new Model("alpine_cabin"));
        models.get("alpine_cabin").setPosition(-30, 0, -90);
        models.get("alpine_cabin").normalizeHeight();
       
        models.put("potted_plant", new Model("potted_plant"));
        models.get("potted_plant").setPosition(-30, 0, -100);
        models.get("potted_plant").normalizeHeight();
        
        models.put("bucket", new Model("bucket"));
        models.get("bucket").setPosition(-40, 0, -10);
        models.get("bucket").normalizeHeight();
        models.put("street_plant", new Model("street_plant"));
        models.get("street_plant").setPosition(-40, 0, -20);
        models.get("street_plant").normalizeHeight();

        models.put("bamboo", new Model("bamboo"));
        models.get("bamboo").setPosition(-40, 0, -30);
        models.get("bamboo").normalizeHeight();*/

        addNormalized(new Model("gold_monkey"), 0, 10, 10);

        /*addNormalized("street_light", new Model("street_light"), 50, 0, 50);
        addNormalized("tree_forsell", new Model("tree_forsell"), 60, 0, 50);
        addNormalized("fence", new Model("fence"), 70, 0, 50);
        addNormalized("fantasy_house", new Model("fantasy_house"), 80, 0, 50);
        addNormalized("door", new Model("door"), 90, 0, 50);
        addNormalized("fence2", new Model("fence2"), 100, 0, 50);
        add("tire", new Model("tire"), 110, 0, 50);
        addNormalized("grass2", new Model("grass2"), 120, 0, 50);
        addNormalized("rockpack", new Model("rockpack"), 50, 0, 20);
        addNormalized("treepack", new Model("treepack"), 70, 0, 20);
        addNormalized("forrestpack", new Model("forrestpack"), 90, 0, 20);
        addNormalized("barrel1", new Model("barrel1"), 20, 0, 60);
        addNormalized("barrel2", new Model("barrel2"), 25, 0, 60);
        addNormalized("barrel3", new Model("barrel3"), 30, 0, 60);
        addNormalized("bonfire", new Model("bonfire"), 40, 0, 60);
        
        addNormalized(new Model("Mushroom_01"), 50, 0, 60);
        addNormalized(new Model("Mushroom_02"), 60, 0, 60);
        addNormalized(new Model("Mushroom_03"), 70, 0, 60);
        addNormalized(new Model("FlowerA_01"), 80, 0, 60);
        addNormalized(new Model("FlowerA_02"), 90, 0, 60);
        addNormalized(new Model("FlowerA_03"), 100, 0, 60);
        addNormalized(new Model("FlowerB_01"), 80, 0, 70);
        addNormalized(new Model("FlowerB_02"), 90, 0, 70);
        addNormalized(new Model("FlowerB_03"), 100, 0, 70);
        addNormalized(new Model("FlowerC_01"), 80, 0, 80);
        addNormalized(new Model("FlowerC_02"), 90, 0, 80);
        addNormalized(new Model("FlowerC_03"), 100, 0, 80);
        addNormalized(new Model("Fern_01"), 100, 0, 90);
        
        addNormalized("treasure", new Model("treasure"), 100, 0, 20);/*

        //models.put("sponza", new Model(Loader.loadObj("sponza")));
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
