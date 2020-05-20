package state;

import camera.Camera;
import camera.FreeCamera;
import camera.Player;
import camera.ProjectionMatrix;
import camera.RayCaster;
import gui.GUI;
import light.LightHandler;
import light.ShadowHandler;
import model.ModelHandler;
import model.Skybox;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;
import water.WaterHandler;

public class GameState extends State
{

    int currentFPS = 0;

    ProjectionMatrix projectionMatrix = new ProjectionMatrix();

    ModelHandler models;
    TerrainHandler terrain;
    Skybox skybox;
    
    LightHandler lights;
    ShadowHandler shadows;

    WaterHandler water;
    boolean renderWater = false;

    GUI gui;

    FreeCamera birdCamera;
    Player player;
    Camera currentCamera;

    RayCaster rayCaster;

    @Override
    public void init()
    {
        terrain = new TerrainHandler(projectionMatrix.get());

        models = new ModelHandler(projectionMatrix.get());
        models.init();

        birdCamera = new FreeCamera(new Vector3f(-100, 100, -100), new Vector3f(30, 20, 30));
        player = new Player(new Vector3f(-110f, 0, -270f), models, projectionMatrix.get());
        currentCamera = player.thirdPersonCamera;
        rayCaster = new RayCaster(projectionMatrix.get());

        skybox = new Skybox(projectionMatrix.get());

        water = new WaterHandler(projectionMatrix.get());

        //lights
        lights = new LightHandler(projectionMatrix.get());
        shadows = new ShadowHandler(lights);

        lights.addPosLight(new Vector3f(-100, 4, -10), new Vector3f(0.0f, 1.0f, 0.0f));

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
        lights.update(deltaTime);

        if (counter % 20 == 0)
        {
            gui.setTextString("fps", "" + currentFPS);
        }
    }

    @Override
    public void render(long window)
    {
        if (renderWater)
        {
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            
            water.bindRefractionFrameBuffer();
            renderScene(new Vector4f(0, -1, 0, water.getHeight() + 1f), currentCamera, projectionMatrix.get());

            //move camera under water to create reflection texture
            float distance = 2 * (currentCamera.getPosition().y - water.getHeight());
            currentCamera.getPosition().y -= distance;
            currentCamera.getDirection().y *= -1;

            water.bindReflectionFrameBuffer();
            renderScene(new Vector4f(0, 1, 0, -water.getHeight() + 0.1f), currentCamera, projectionMatrix.get());
            water.unbindCurrentFrameBuffer();
            
            //move camera back
            currentCamera.getPosition().y += distance;
            currentCamera.getDirection().y *= -1;
            
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        }
        
        shadows.render(lights.getSun().getSunCamera(currentCamera), models, terrain);
        renderScene(new Vector4f(0, 0, 0, 0), currentCamera, projectionMatrix.get());

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

        Vector3f fogColor = new Vector3f(0.5f,0.6f,0.7f);
        skybox.render(camera, fogColor);
        lights.render(camera);
        terrain.render(camera, lights, clippingPlane, projectionMatrix, shadows);
        models.render(camera, lights, clippingPlane, projectionMatrix, shadows);
        player.render(camera, lights, clippingPlane, projectionMatrix);
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
        } else if (glfwGetKey(window, GLFW_KEY_KP_4) == GLFW_PRESS)
        {
            //free moving flying camera
            currentCamera = lights.getSun().getSunCamera(currentCamera);
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
            birdCamera.checkInput(window, models);
        }

        //save and exit
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            changeState = "menu";
        }
    }
    
    @Override
    public void activateState(long window, TransitionInformation t)
    {
    }

    @Override
    public TransitionInformation deactivateState(long window)
    {
        
        return new TransitionInformation(name());
    }

    @Override
    public String name()
    {
        return "game";
    }
}
