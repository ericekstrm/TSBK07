package state;

import camera.BuildCamera;
import camera.Camera;
import camera.ObjectPlacer;
import gui.GUI;
import light.LightHandler;
import light.ShadowHandler;
import loader.SceneSaver;
import model.Model;
import model.ModelHandler;
import particle_system.Particle;
import model.Skybox;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import particle_system.Smoke;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;
import water.WaterHandler;

public class LevelEditorState extends State implements GLFWMouseButtonCallbackI
{

    int currentFPS = 0;

    public static final float nearPlane = 1f;
    public static final float farPlane = 1000.0f;
    public static final float rightPlane = 0.5f;
    public static final float leftPlane = -0.5f;
    public static final float topPlane = 0.5f;
    public static final float bottomPlane = -0.5f;
    Matrix4f projectionMatrix = Matrix4f.frustum_new(nearPlane, farPlane, rightPlane, leftPlane, topPlane, bottomPlane);

    ModelHandler models;
    TerrainHandler terrain;
    Skybox skybox;
    Smoke smokeParticles;

    LightHandler lights;
    ShadowHandler shadows;

    WaterHandler water;
    boolean renderWater = false;

    GUI gui;

    BuildCamera buildCamera;
    Camera currentCamera;

    ObjectPlacer placer;

    @Override
    public void init()
    {
        terrain = new TerrainHandler(projectionMatrix);

        models = new ModelHandler(projectionMatrix);
        models.init();

        buildCamera = new BuildCamera(new Vector3f());
        currentCamera = buildCamera;

        skybox = new Skybox(projectionMatrix);
        
        smokeParticles = new Smoke(projectionMatrix, new Vector3f(-114.314f,-0.157f,-267.456f));
        
        water = new WaterHandler(projectionMatrix);

        //lights
        lights = new LightHandler(projectionMatrix);
        shadows = new ShadowHandler(lights);

        lights.addPosLight(new Vector3f(-100, 4, -10), new Vector3f(0.0f, 1.0f, 0.0f));

        //light for lamp post
        gui = new GUI();
        gui.addText("" + currentFPS, "fps", -1, -0.95f);
        gui.addText("Controls on numpad:", "controls1", -1, 0.90f);
        gui.addText("4/6 to change model. * to rotate. +/- to scale.", "controls2", -1, 0.97f);

        placer = new ObjectPlacer(projectionMatrix);
    }

    int counter = 0;

    @Override
    public void update(float deltaTime)
    {
        counter++;
        //convert to seconds
        deltaTime /= 1000;

        water.update(deltaTime);
        lights.update(deltaTime);

        if (counter % 20 == 0)
        {
            gui.setTextString("fps", "" + currentFPS);
        }
        smokeParticles.update(deltaTime, projectionMatrix);
    }

    @Override
    public void render(long window)
    {
        if (renderWater)
        {
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            
            water.bindRefractionFrameBuffer();
            renderScene(new Vector4f(0, -1, 0, water.getHeight() + 1f), currentCamera, projectionMatrix);

            //move camera under water to create reflection texture
            float distance = 2 * (currentCamera.getPosition().y - water.getHeight());
            currentCamera.getPosition().y -= distance;
            currentCamera.getDirection().y *= -1;

            water.bindReflectionFrameBuffer();
            renderScene(new Vector4f(0, 1, 0, -water.getHeight() + 0.1f), currentCamera, projectionMatrix);
            water.unbindCurrentFrameBuffer();
            
            //move camera back
            currentCamera.getPosition().y += distance;
            currentCamera.getDirection().y *= -1;
            
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        }
        
        shadows.render(lights.getSun().getSunCamera(currentCamera), models, terrain);
        renderScene(new Vector4f(0, 0, 0, 0), currentCamera, projectionMatrix);

        water.render(currentCamera, lights, water.getFrameBuffer());
        gui.render();

        glfwSwapBuffers(window);
    }

    /**
     * Renders all objects in the scene. Skybox, lights, terrain, models and
     * player are rendered.
     *
     * @param clippingPlane - specifies a plane that cuts out part of the scene
     * from rendering.
     * @param camera - The camera to use where rendering the scene.
     */
    private void renderScene(Vector4f clippingPlane, Camera camera, Matrix4f projectionMatrix)
    {
        //prepare
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Vector3f fogColor = new Vector3f(0.5f, 0.6f, 0.7f);
        skybox.render(camera, fogColor);
        lights.render(camera);
        terrain.render(camera, lights, clippingPlane, projectionMatrix, shadows);
        models.render(camera, lights, clippingPlane, projectionMatrix, shadows);
        placer.render(camera, lights, clippingPlane, projectionMatrix);
        smokeParticles.render(camera);
    }

    @Override
    public void checkInput(long window)
    {
        /* ===| Controls |===
    	 * 
    	 * Arrows : move the camera around 
         * I, O to toggle of water is visable
         */

        //toggle water render
        if (glfwGetKey(window, GLFW_KEY_I) == GLFW_PRESS)
        {
            renderWater = true;
        } else if (glfwGetKey(window, GLFW_KEY_O) == GLFW_PRESS)
        {
            renderWater = false;
        }

        buildCamera.checkInput(window, models);

        //save and exit
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            changeState = "menu";
        } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
        {
            SceneSaver.saveScene("test", models, terrain, lights, water);
            changeState = "menu";
        }

        placer.update(window, currentCamera, terrain);
    }

    @Override
    public void activateState(long window, TransitionInformation t)
    {
        glfwSetMouseButtonCallback(window, this);
        glfwSetScrollCallback(window, buildCamera);
        glfwSetKeyCallback(window, new GLFWKeyCallback()
                   {
                       @Override
                       public void invoke(long window, int key, int scancode, int action, int mods)
                       {
                           if (key == GLFW_KEY_KP_4 && action == GLFW_PRESS)
                           {
                               placer.prevModel();
                           }
                           if (key == GLFW_KEY_KP_6 && action == GLFW_PRESS)
                           {
                               placer.nextModel();
                           }
                       }
                   });
    }

    @Override
    public TransitionInformation deactivateState(long window)
    {
        glfwSetMouseButtonCallback(window, null);
        glfwSetScrollCallback(window, null);
        glfwSetKeyCallback(window, null);
        return new TransitionInformation(name());
    }

    @Override
    public String name()
    {
        return "levelEditor";
    }

    /**
     * Callback for mouse clicks.
     *
     * @param window
     * @param button
     * @param action
     * @param mods
     */
    @Override
    public void invoke(long window, int button, int action, int mods)
    {
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)
        {
            models.add(new Model(placer.getModel()));
        }
    }
}
