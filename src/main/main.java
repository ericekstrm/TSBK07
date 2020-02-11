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

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;

    long window;

    TexturedModel model1;
    TexturedModel model2;
    TexturedModel floor;
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
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void initModel()
    {
        shader = new Shader("test.vert", "test.frag");

        model1 = Loader.loadObjModel("res\\bunnyplus.obj");
        model1.setTexture("tex.jpg", shader);

        model2 = Loader.loadObjModel("res\\bunnyplus.obj");
        model2.setTexture("tex2.jpg", shader);
        model2.setPosition(1, 0, 1);

        floor = Loader.loadObjModel("res\\flat.obj");
        floor.setTexture("grass.jpg", shader);
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
        skybox = new Skybox(skyboxTextures);
    }

    long time = 0;

    public void update()
    {

    }

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            //draw skybox
            skybox.prepareForRender(camera);
            skybox.render(shader);
            
            shader.start();

            //world-to-view matrix
            FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
            camera.getWorldtoViewMatrix().toBuffer(worldToView);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);

            //render
            model1.render(shader);
            model2.render(shader);
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
