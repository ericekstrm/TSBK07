package main;

import util.Loader;
import java.nio.*;
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
import util.Vector3f;

public class main
{

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    long window;

    Model model1;
    Model model2;
    Model floor;
    Model tree;
    Skybox skybox;

    int tex;

    Shader shader;

    Camera camera = new Camera(new Vector3f(2, 1, 2), new Vector3f(0, 0, 0));

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

        model1.destroy();
        model2.destroy();
        tree.destroy();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void initModel()
    {
        shader = new Shader("test.vert", "test.frag");

        model1 = new Model(shader, Loader.loadRawData("res\\bunnyplus.obj", "tex2.jpg"));

        model2 = new Model(shader, Loader.loadRawData("res\\bunnyplus.obj", "tex.jpg"));
        model2.setPosition(3, 3, 0);

        tree = new Model(shader, Loader.loadRawData("res\\tree.obj", "green.jpg"));
        tree.setPosition(2, 0, 2);
        tree.setScale(0.1f, 0.1f, 0.1f);

        floor = new Model(shader, Loader.loadRawData("res\\flat.obj", "grass.jpg"));
        floor.setPosition(0, -0.1f, 0);
        floor.setScale(3, 1, 3);

        String[] skyboxTextures
                =
                {
                    "tex2.jpg",
                    "tex2.jpg",
                    "tex2.jpg",
                    "tex2.jpg",
                    "tex2.jpg",
                    "tex2.jpg",
                };
        //skybox = new Skybox(skyboxTextures);
    }

    long time = 0;

    public void update()
    {
        time = System.currentTimeMillis() % 36000;
        model1.setRotation(0, time / 10, 0);
    }

    //light sources (TEMP)
    Vector3f[] lightSourcesColorsArr =
    {
        new Vector3f(1.0f, 0.0f, 0.0f), // Red light
        new Vector3f(0.0f, 1.0f, 0.0f), // Green light
        new Vector3f(0.0f, 0.0f, 1.0f), // Blue light
        new Vector3f(1.0f, 1.0f, 1.0f)  // White light 
    };

    int[] isDirectional =
    {
        0, 0, 1, 1
    };

    Vector3f[] lightSourcesDirectionsPositions =
    {
        new Vector3f(3.0f, 3.0f, 0.0f), // Red light, positional
        new Vector3f(0.0f, 5.0f, 10.0f), // Green light, positional
        new Vector3f(-1.0f, 0.0f, 0.0f), // Blue light along X
        new Vector3f(0.0f, 0.0f, -1.0f)  // White light along Z
    };

    float[] specularExponent =
    {
        100.0f, 200.0f, 60.0f, 50.0f, 300.0f, 150.0f
    };

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            //draw skybox
            //skybox.prepareForRender(camera);
            //skybox.render(shader);
            shader.start();

            //---------------------- lighting (TEMP) ---------------------------
            //hehe, there is room for improvement here!
            //pos/dir
            FloatBuffer lightSourcesDirPosArr = BufferUtils.createFloatBuffer(12);
            for (int i = 0; i < 4; i++)
            {
                lightSourcesDirPosArr.put(lightSourcesDirectionsPositions[i].x).put(lightSourcesDirectionsPositions[i].y).put(lightSourcesDirectionsPositions[i].z);
            }
            lightSourcesDirPosArr.flip();
            glUniform3fv(glGetUniformLocation(shader.getProgramID(), "lightSourcesDirPosArr"), lightSourcesDirPosArr);

            //color
            FloatBuffer lightSourcesColorArr = BufferUtils.createFloatBuffer(12);
            for (int i = 0; i < 4; i++)
            {
                lightSourcesColorArr.put(lightSourcesColorsArr[i].x).put(lightSourcesColorsArr[i].y).put(lightSourcesColorsArr[i].z);
            }
            lightSourcesColorArr.flip();
            glUniform3fv(glGetUniformLocation(shader.getProgramID(), "lightSourcesColorArr"), lightSourcesColorArr);

            //specular
            glUniform1f(glGetUniformLocation(shader.getProgramID(), "specularExponent"), specularExponent[0]);

            //directional
            glUniform1iv(glGetUniformLocation(shader.getProgramID(), "isDirectional"), isDirectional);

            //------------------------------------------------------------------
            //world-to-view matrix
            FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
            camera.getWorldtoViewMatrix().toBuffer(worldToView);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);

            //render
            model1.render(shader);
            model2.render(shader);
            tree.render(shader);
            floor.render(shader);

            shader.stop();

            glfwSwapBuffers(window);

            glfwPollEvents();
            checkInput();
        }

        glfwTerminate();
    }

    DoubleBuffer prevXpos = null;
    DoubleBuffer prevYpos = null;

    public void checkInput()
    {
        camera.checkInput(window);

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public static void main(String[] args)
    {
        main m = new main();
        m.initOpenGL();
        m.initModel();
        m.loop();
        m.destroyOpenGL();
    }
}
