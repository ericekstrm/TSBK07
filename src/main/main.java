package main;

import water.Water;
import loader.RawData;
import loader.Loader;
import java.util.HashMap;
import java.util.Map;
import light.Lights;
import loader.Material;
import model.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import shader.ModelShader;
import water.WaterShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Util;
import util.Vector3f;

public class main
{

    public static Matrix4f projectionMatrix = Matrix4f.frustum_new();

    //window size
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    long window;

    Map<String, Model> models = new HashMap<>();
    Windmill windmill;
    TerrainHandler terrain;
    Skybox skybox;
    Water water;
    Lights lights;

    ModelShader shader;

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
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void destroyOpenGL()
    {
        // Disable the VBO index from the VAO attributes list
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        for (Map.Entry<String, Model> m : models.entrySet())
        {
            m.getValue().destroy();
        }
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void initModel()
    {
        camera = new Camera(new Vector3f(2, 1, 2), new Vector3f(3, 3, 0), window);

        shader = new ModelShader();
        shader.start();
        shader.loadProjectionMatrix(Matrix4f.frustum_new());
        shader.connectTextureUnits();
        shader.stop();

        skybox = new Skybox(new ModelShader("skybox.vert", "skybox.frag"),
                            Loader.loadRawData("skybox.obj", "SkyBox512.tga"));
        skybox.setPosition(0, -3, 0);
        terrain = new TerrainHandler();

        water = new Water(new WaterShader());
        water.setPosition(200, -5, 200);
        water.setScale(3, 3, 3);

        lights = new Lights();
        lights.addPosLight(new Vector3f(15.0f, 3.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        lights.addPosLight(new Vector3f(9.0f, 7.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        lights.addDirLight(new Vector3f(0.0f, 1.0f, 0.5f), new Vector3f(0.5f, 0.5f, 0.5f));

        models.put("bunny", new Model(shader, Loader.loadRawData("bunnyplus.obj", "tex2.jpg")));
        models.get("bunny").setPosition(1, 0, 1);
        models.get("bunny").setMaterialProperties(0, new Material());

        windmill = new Windmill(shader);
        windmill.setPosition(10, 0, -10);
        windmill.setRotation(0, 180, 0);
        
        models.put("house", new Model(shader, Loader.loadRawData("House.obj", "")));
        models.get("house").setPosition(-20, 10, -20);

        //a bunch of trees
        RawData data = Loader.loadRawData("tree.obj", "green.jpg");
        for (int i = 0; i < 20; i++)
        {
            Model tree = new Model(shader, data);
            float x = (float) Util.randu(800);
            float z = (float) Util.randu(800);
            tree.setPosition(x, terrain.getHeight(x, z), z);
            tree.setScale(0.3f, 0.3f, 0.3f);
            tree.setRotation(0, Util.rand(0, 360), 0);
            models.put("tree" + i, tree);
        }

        //a bunch of arrows
        /*data = Loader.loadRawData("arrow.obj", "green.jpg");
        for (int i = 0; i < 1000; i++)
        {
            Model tree = new Model(shader, data);
            float x = (float) Util.randu(100);
            float z = (float) Util.randu(100);
            tree.setPosition(x, terrain.getHeight(x, z), z);
            tree.setScale(0.3f, 0.3f, 0.3f);
            Vector3f rotationaxis = new Vector3f(0, 1, 0).cross(terrain.getNormal(x, z));
            if (rotationaxis.length() == 0) {
				rotationaxis = new Vector3f(0,1,0);
			}
            float angle = (float) Math.acos(terrain.getNormal(x, z).dot(new Vector3f(0, 1, 0))) / (2 * (float) Math.PI) * 360;

            tree.setRotation(Matrix4f.rotate(angle, rotationaxis).toMatrix3f());
            models.put("arrow" + i, tree);
        }*/
        /*data = Loader.loadRawData("ball.obj", "green.jpg");
        for (int i = 0; i < 50; i++)
        {
            for (int j = 0; j < 50; j++)
            {
                RigidSphere ball = new RigidSphere(shader, data);
                ball.setPosition(-50 - i, 10, 50 + j);
                ball.setScale(0.5f, 0.5f, 0.5f);
                models.put("ball" + i + "" + j, ball);
            }
        }*/

    }

    long time = 0;

    public void update(float deltaTime)
    {
        //convert to seconds
        deltaTime /= 1000;

        windmill.update(time);

        time = System.currentTimeMillis() % 36000;
        models.get("bunny").setRotation(0, time / 100, 0);

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

    void loop()
    {
        prevTime = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window))
        {
            long currentTime = System.currentTimeMillis();
            update(System.currentTimeMillis() - prevTime);
            prevTime = currentTime;

            render();

            glfwPollEvents();
            checkInput();
        }

        glfwTerminate();
    }

    public void render()
    {
        //prepare
        glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        //render skybox
        skybox.render(camera);

        //render lights
        lights.render(camera);

        //render terrain
        terrain.render(camera, lights);

        //render water
        water.render(camera);

        //render objects
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrix(camera);
        //render
        for (Map.Entry<String, Model> m : models.entrySet())
        {
            m.getValue().render(shader);
        }
        windmill.render(shader);

        shader.stop();

        glfwSwapBuffers(window);
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
        m.loop();
        m.destroyOpenGL();
    }
}
