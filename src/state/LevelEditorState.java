package state;

import camera.Camera;
import camera.FreeCamera;
import camera.ObjectPlacer;
import camera.Player;
import gui.GUI;
import light.LightHandler;
import light.ShadowHandler;
import loader.SceneSaver;
import model.Model;
import model.ModelHandler;
import model.Skybox;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;
import water.WaterFrameBuffer;
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

    LightHandler lights;
    ShadowHandler shadows;

    WaterHandler water;
    WaterFrameBuffer waterFrameBuffer;
    Vector4f clippingPlane = new Vector4f(0, -1, 0, 8);
    boolean renderWater = false;

    GUI gui;

    FreeCamera birdCamera;
    Camera currentCamera;

    ObjectPlacer placer;

    @Override
    public void init()
    {
        terrain = new TerrainHandler(projectionMatrix);

        models = new ModelHandler(projectionMatrix);
        models.init(terrain);

        birdCamera = new FreeCamera(new Vector3f(-100, 100, -100), new Vector3f(30, 20, 30));
        currentCamera = birdCamera;

        skybox = new Skybox(projectionMatrix);

        water = new WaterHandler(projectionMatrix);
        waterFrameBuffer = new WaterFrameBuffer();

        //lights
        lights = new LightHandler(projectionMatrix);
        shadows = new ShadowHandler(lights);

        lights.addPosLight(new Vector3f(-100, 4, -10), new Vector3f(0.0f, 1.0f, 0.0f));

        //light for lamp post
        gui = new GUI();
        gui.addText("" + currentFPS, "fps", -1, -0.95f);
        gui.addImageNormalized(shadows.getDepthMap(), 0.5f, -0.5f, 0.5f, -0.5f);

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
    }

    @Override
    public void render(long window)
    {
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        /*if (renderWater)
        {
            waterFrameBuffer.bindRefractionFrameBuffer();
            renderScene(new Vector4f(0, -1, 0, water.getHeight() + 1f), currentCamera);

            //move camera under water to create reflection texture
            float distance = 2 * (currentCamera.position.y - water.getHeight());
            currentCamera.position.y -= distance;
            currentCamera.direction.y *= -1;

            waterFrameBuffer.bindReflectionFrameBuffer();
            renderScene(new Vector4f(0, 1, 0, -water.getHeight() + 0.1f), currentCamera);
            currentCamera.position.y += distance;
            currentCamera.direction.y *= -1;
            waterFrameBuffer.unbindCurrentFrameBuffer();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        }*/
        //shadowMap.bindFrameBuffer();
        //renderScene(new Vector4f(0, 0, 0, 0), lights.getSun().getSunCamera(), shadowProjectionMatrix);
        //shadowMap.unbindFrameBuffer();
        shadows.render(lights.getSun().getSunCamera(currentCamera), models, terrain, new Player(new Vector3f(), models, projectionMatrix));

        renderScene(new Vector4f(0, 0, 0, 0), currentCamera, projectionMatrix);

        //water.render(currentCamera, lights, waterFrameBuffer);
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

        birdCamera.checkInput(window, models);

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
        glfwSetScrollCallback(window, new GLFWScrollCallback()
                      {
                          @Override
                          public void invoke(long window, double xoffset, double yoffset)
                          {
                              if (yoffset > 0)
                              {
                                  placer.nextModel();
                              } else if (yoffset < 0)
                              {
                                  placer.prevModel();
                              }
                          }
                      });
    }

    @Override
    public TransitionInformation deactivateState(long window)
    {
        glfwSetMouseButtonCallback(window, null);
        glfwSetScrollCallback(window, null);
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
