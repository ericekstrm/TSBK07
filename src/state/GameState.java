package state;

import camera.Camera;
import camera.FreeCamera;
import camera.RayCaster;
import gui.GUI;
import light.LightHandler;
import main.Player;
import main.SceneSaver;
import model.ModelHandler;
import model.Skybox;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;
import water.WaterFrameBuffer;
import water.WaterHandler;

public class GameState extends State
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

    WaterHandler water;
    WaterFrameBuffer waterFrameBuffer;
    Vector4f clippingPlane = new Vector4f(0, -1, 0, 8);
    boolean renderWater = false;

    GUI gui;

    FreeCamera birdCamera;
    Player player;
    Camera currentCamera;

    RayCaster rayCaster;

    @Override
    public void init()
    {
        terrain = new TerrainHandler(projectionMatrix);

        models = new ModelHandler(projectionMatrix);
        models.init(terrain);

        birdCamera = new FreeCamera(new Vector3f(-100, 100, -100), new Vector3f(30, 20, 30));
        player = new Player(new Vector3f(0, 0, 0), models, projectionMatrix);
        currentCamera = player.thirdPersonCamera;
        rayCaster = new RayCaster(player.getCamera(), projectionMatrix);

        skybox = new Skybox(projectionMatrix);

        water = new WaterHandler(projectionMatrix);
        waterFrameBuffer = new WaterFrameBuffer();

        //lights
        lights = new LightHandler(projectionMatrix);

        lights.addPosLight(new Vector3f(-100, 4, -10), new Vector3f(0.0f, 1.0f, 0.0f));
        //lights.addDirLight(new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1f, 1f, 1f));
        lights.addPosLight(new Vector3f(0.0f, 5.0f, 0.0f), new Vector3f(1f, 1f, 1f));

        //light for lamp post
        lights.addPosLight(new Vector3f(60.0f, 7.0f, 60.0f), new Vector3f(1.0f, 0.3f, 0.3f));

        gui = new GUI();
        gui.addText("" + currentFPS, "fps", -1, -0.95f);
    }

    int counter = 0;

    @Override
    public void update(float deltaTime)
    {
        counter++;
        //convert to seconds
        deltaTime /= 1000;

        water.update(deltaTime);

        if (counter % 20 == 0)
        {
            gui.setTextString("fps", "" + currentFPS);
        }

        //rotating sun around origin
        //lights.rotateDirLight(0, Matrix4f.rotate(0.005f, new Vector3f(0, 0, 1)));
    }

    @Override
    public void render(long window)
    {
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        if (renderWater)
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
        }

        renderScene(new Vector4f(0, 0, 0, 0), currentCamera);

        water.render(currentCamera, lights, waterFrameBuffer);

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
    private void renderScene(Vector4f clippingPlane, Camera camera)
    {
        //prepare
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        skybox.render(camera);
        lights.render(camera);
        terrain.render(camera, lights, clippingPlane);
        models.render(camera, lights, clippingPlane);
        player.render(camera, lights, clippingPlane);
    }

    @Override
    public void checkInput(long window)
    {
        /* ===| Controls |===
    	 * 
    	 * Arrows : move the camera around 
         * I, O to toggle of water is visable
         * NUMPAD 1,2,3 to switch between cameras
         */

        //switch between cameras
        if (glfwGetKey(window, GLFW_KEY_KP_1) == GLFW_PRESS)
        {
            //3d person camera
            currentCamera = player.thirdPersonCamera;
        } else if (glfwGetKey(window, GLFW_KEY_KP_2) == GLFW_PRESS)
        {
            //1st person camera
            currentCamera = player.firstPersonCamera;
        } else if (glfwGetKey(window, GLFW_KEY_KP_3) == GLFW_PRESS)
        {
            //free moving flying camera
            currentCamera = birdCamera;
        }

        //toggle water render
        if (glfwGetKey(window, GLFW_KEY_I) == GLFW_PRESS)
        {
            renderWater = true;
        } else if (glfwGetKey(window, GLFW_KEY_O) == GLFW_PRESS)
        {
            renderWater = false;
        }

        if (currentCamera == player.firstPersonCamera
                || currentCamera == player.thirdPersonCamera)
        {
            player.checkInput(window, terrain);
        } else if (currentCamera == birdCamera)
        {
            birdCamera.checkInput(window);
        }

        //save and exit
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
        {
            SceneSaver.saveScene("test", models, terrain, lights, water);
            glfwSetWindowShouldClose(window, true);
        }
    }

    @Override
    public String name()
    {
        return "game";
    }
}
