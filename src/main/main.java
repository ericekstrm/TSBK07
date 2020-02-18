package main;

import util.Loader;
import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import shader.Shader;
import util.Matrix4f;
import util.Util;
import util.Vector3f;

public class main
{

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    long window;

    Map<String, Model> models = new HashMap<>();
    Skybox skybox;
    List<PositionalLight> pointLights = new ArrayList<>();
    List<DirectionalLight> dirLights = new ArrayList<>();

    int tex;

    Shader shader;
    Shader lightShader;
    Shader skyboxShader;

    Camera camera = new Camera(new Vector3f(2, 1, 2), new Vector3f(3, 3, 0));

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
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
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
        shader = new Shader("test.vert", "test.frag");
        lightShader = new Shader("light.vert", "light.frag");
        skyboxShader = new Shader("skybox.vert", "skybox.frag");

        Model floor = new Model(shader, Loader.loadRawData("flat.obj", "grass.jpg"));
        floor.setPosition(0, -0.1f, 0);
        floor.setScale(20, 1, 20);
        models.put("floor", floor);

        models.put("bunny", new Model(shader, Loader.loadRawData("bunnyplus.obj", "tex2.jpg")));
        models.get("bunny").setPosition(1, 0, 1);

        Model windmill = new Windmill(shader);
        windmill.setPosition(10, 0, -10);
        windmill.setRotation(0, 180, 0);
        models.put("windmill", windmill);

        RawData data = Loader.loadRawData("tree.obj", "green.jpg");
        for (int i = 0; i < 100; i++)
        {

            Model tree = new Model(shader, data);
            tree.setPosition((float) Util.randu(20), 0, (float) Util.randu(20));
            tree.setScale(0.1f, 0.1f, 0.1f);
            models.put("tree" + i, tree);
        }

        pointLights.add(new PositionalLight(new Vector3f(15.0f, 3.0f, 0.0f),
                                            new Vector3f(1.0f, 0.0f, 0.0f)));
        pointLights.add(new PositionalLight(new Vector3f(0.0f, 5.0f, 5.0f),
                                            new Vector3f(0.0f, 1.0f, 0.0f)));

        //dirLights.add(new DirectionalLight(new Vector3f(0.0f, 0.0f, -1.0f),
        //                                   new Vector3f(1.0f, 1.0f, 1.0f)));

        skybox = new Skybox(skyboxShader, Loader.loadRawData("skybox.obj", "SkyBox512.tga"));
    }

    long time = 0;

    public void update()
    {
        models.get("windmill").update();
        time = System.currentTimeMillis() % 36000;
        models.get("bunny").setRotation(0, time / 10, 0);

        pointLights.get(0).setPosition(Matrix4f.rotate(0, 2, 0).multiply(pointLights.get(0).getPosition()));
    }

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();
            
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

        //draw skybox
        skyboxShader.start();

        skybox.prepareForRender(camera, skyboxShader);
        skybox.render(shader);

        skyboxShader.stop();

        //draw lights
        lightShader.start();

        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        camera.getWorldtoViewMatrix().toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(lightShader.getProgramID(), "worldToView"), false, worldToView);

        for (PositionalLight light : pointLights)
        {
            light.render(lightShader);
        }
        lightShader.stop();

        shader.start();
        loadLights();

        FloatBuffer viewPos = BufferUtils.createFloatBuffer(3);
        camera.getPosition().toBuffer(viewPos);
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "viewPos"), viewPos);

        //world-to-view matrix
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);

        //render
        for (Map.Entry<String, Model> m : models.entrySet())
        {
            m.getValue().render(shader);
        }

        shader.stop();

        glfwSwapBuffers(window);
    }

    public void checkInput()
    {
        camera.checkInput(window);

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public void loadLights()
    {
        //Pointlights position
        FloatBuffer pointLightPosArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (PositionalLight light : pointLights)
        {
            Vector3f pos = light.getPosition();
            pointLightPosArr.put(pos.x).put(pos.y).put(pos.z);
        }
        pointLightPosArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightPosArr"), pointLightPosArr);

        //Pointlights color
        FloatBuffer pointLightColorArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (PositionalLight light : pointLights)
        {
            Vector3f color = light.getColor();
            pointLightColorArr.put(color.x).put(color.y).put(color.z);
        }
        pointLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightColorArr"), pointLightColorArr);

        //Directional lights directions
        FloatBuffer dirLightDirArr = BufferUtils.createFloatBuffer(6);
        for (DirectionalLight dirLight : dirLights)
        {
            Vector3f dir = dirLight.getDirection();
            dirLightDirArr.put(dir.x).put(dir.y).put(dir.z);
        }
        dirLightDirArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightDirArr"), dirLightDirArr);

        //Directional lights color
        FloatBuffer dirLightColorArr = BufferUtils.createFloatBuffer(6);
        for (DirectionalLight dirLight : dirLights)
        {
            Vector3f color = dirLight.getColor();
            dirLightColorArr.put(color.x).put(color.y).put(color.z);
        }
        dirLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightColorArr"), dirLightColorArr);
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
