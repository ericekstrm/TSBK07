package main;

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
import util.Matrix4f;
import util.Vector3f;

public class main
{

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;

    long window;

    TexturedModel model1;
    TexturedModel model2;
    
    int tex;

    Shader shader;

    Camera camera = new Camera(new Vector3f(5, -3, 5), new Vector3f(0, 0, 0));

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
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
    }

    public void destroyOpenGL()
    {
        // Disable the VBO index from the VAO attributes list
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Delete the VAO and VBOs
        model1.destroy();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    Matrix4f rotationMatrix;
    Matrix4f projectionMatrix;
    Matrix4f translationMatrix;

    public void initModel()
    {
        shader = new Shader("src\\main\\test.vert", "src\\main\\test.frag");

        model1 = Loader.loadObjModel("res\\bunnyplus.obj");
        model1.setTexture("tex.jpg", shader);
        model2 = Loader.loadObjModel("res\\bunnyplus.obj");
        model2.setTexture("tex2.jpg", shader);
        model2.setPosition(1, 0, 1);
    }

    long time = 0;

    public void update()
    {
        time = System.currentTimeMillis() % (1080 * 10);
        projectionMatrix = Matrix4f.frustum(-1f, 1f, -1f, 1f, 30.0f, 1.0f);
        translationMatrix = Matrix4f.translate(0, 0, 0).multiply(Matrix4f.scale(0.2f, 0.2f, 0.2f));
    }

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.start();
            

            
            //projection matrix
            FloatBuffer projection = BufferUtils.createFloatBuffer(16);
            projectionMatrix.toBuffer(projection);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "projection"), false, projection);

            //world-to-view matrix
            FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
            camera.getWorldtoViewMatrix().toBuffer(worldToView);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);

            //render
            
            //glBindTexture(GL_TEXTURE_2D, tex);

            model1.render(shader);
            model2.render(shader);

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

//        DoubleBuffer xpos = null;
//        DoubleBuffer ypos = null;
//        glfwGetCursorPos(window, xpos, ypos);
//        
//        if  (prevXpos != null && prevYpos != null)
//        {
//            int diffX = xpos.compareTo(prevXpos);
//            int diffY = ypos.compareTo(prevYpos);
//            Matrix4f rotation = Matrix4f.rota
//            camera.lookAt = camera.lookAt.;
//        }

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
