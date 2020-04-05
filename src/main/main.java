package main;

import water.WaterTile;
import loader.RawData;
import loader.Loader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import light.LightHandler;
import loader.Material;
import model.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import shader.TextureModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;
import water.WaterFrameBuffer;
import water.WaterHandler;

public class main
{

    public static Matrix4f projectionMatrix = Matrix4f.frustum_new();

    //window size
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    long window;

    ModelHandler models;
    TerrainHandler terrain;
    Skybox skybox;

    WaterHandler water;
    WaterFrameBuffer waterFrameBuffer;
    Vector4f clippingPlane = new Vector4f(0, -1, 0, 8);

    LightHandler lights;

    Camera camera;

    void initOpenGL()
    {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //create a window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Mitt fönster", NULL, NULL);
        if (window == 0)
        {
            System.out.println("Failed to create window.");
            glfwTerminate();
            return;
        }

        glfwMakeContextCurrent(window);

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        glfwShowWindow(window);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        GL.createCapabilities();
        glClearColor(91.0f / 255.0f, 142f / 255.0f, 194.0f / 255.0f, 1);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        //for transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void destroyOpenGL()
    {
        // Disable the VBO index from the VAO attributes list
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        models.destroy();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void initModel()
    {
        camera = new Camera(new Vector3f(10, 10, 10), new Vector3f(-20, 0, -20), window);

        skybox = new Skybox(new TextureModelShader("skybox.vert", "skybox.frag"),
                            Loader.loadRawData("skybox.obj", "SkyBox512.tga"));
        skybox.setPosition(0, -3, 0);

        terrain = new TerrainHandler();
        models = new ModelHandler();
        models.init(terrain);

        water = new WaterHandler();
        waterFrameBuffer = new WaterFrameBuffer();

        //lights
        lights = new LightHandler();
        lights.addPosLight(new Vector3f(15.0f, 3.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        lights.addPosLight(new Vector3f(9.0f, 7.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        lights.addDirLight(new Vector3f(0.0f, 1.0f, 0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
        lights.addDirLight(new Vector3f(0.0f, 1.0f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
        
        //light for lamp post
        lights.addPosLight(new Vector3f(-50, 3.65f, -50), new Vector3f(0.97f, 0.84f, 0.11f), 0.7f);
        
        
    }

    public void update(float deltaTime)
    {
        //convert to seconds
        deltaTime /= 1000;
        
        water.update(deltaTime);

        lights.moveLight(0, Matrix4f.rotate(0, 2, 0));

        /*for (int i = 0; i < 50; i++)
        {
            for (int j = 0; j < 50; j++)
            {
                models.get("ball" + i + "" + j).update(deltaTime);

                RigidSphere m = (RigidSphere) models.get("ball" + i + "" + j);
                Vector3f collisionPoint = new Vector3f(m.getPosition().x, terrain.getHeight(m.getPosition().x, m.getPosition().z), m.getPosition().z);
                if (m.getPosition().y <= collisionPoint.y)
                {
                    m.collisionCallback(collisionPoint, terrain.getNormal(m.getPosition().x, m.getPosition().z));
                }
                m.move(deltaTime);
            }
        }*/
    }

    long prevTime;

    void mainLoop()
    {
        prevTime = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window))
        {
            long currentTime = System.currentTimeMillis();
            update(System.currentTimeMillis() - prevTime);
            prevTime = currentTime;

            masterRender();

            glfwPollEvents();
            checkInput();
        }

        glfwTerminate();
    }

    public void masterRender()
    {
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        waterFrameBuffer.bindRefractionFrameBuffer();
        render(new Vector4f(0, -1, 0, water.getHeight()+1f));

        //move camera under water to create reflection texture
        float distance = 2 * (camera.position.y - water.getHeight());
        camera.position.y -= distance;
        camera.direction.y *= -1;

        waterFrameBuffer.bindReflectionFrameBuffer();
        render(new Vector4f(0, 1, 0, -water.getHeight()+0.1f));
        camera.position.y += distance;
        camera.direction.y *= -1;
        waterFrameBuffer.unbindCurrentFrameBuffer();
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        render(new Vector4f(0, 0, 0, 0));
        water.render(camera, lights, waterFrameBuffer);

        glfwSwapBuffers(window);
    }

    public void render(Vector4f clippingPlane)
    {
        //prepare
        glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        //render skybox
        skybox.render(camera);

        //render lights
        lights.render(camera);

        //render terrain
        terrain.render(camera, lights, clippingPlane);

        models.render(camera, lights, clippingPlane);
    }

    public void checkInput()
    {
        /*
    	 * ===| Controls |===
    	 * 
    	 * Arrows : move the camera around 
    	 *      F : toggle flying mode for camera
    	 * scroll : change movement speed
         */

        camera.checkInput(window);

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public static void main(String[] args)
    {
        System.out.println("Working Directory = "
                + System.getProperty("user.dir"));

        main m = new main();
        m.initOpenGL();
        m.initModel();
        m.mainLoop();
        m.destroyOpenGL();
    }
}
