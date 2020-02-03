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

public class main
{

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;

    long window;

    int vboID;
    int vaoID;
    //SimpleColorModel model;
    TexturedModel model;
    
    Shader shader;

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
        //model.destroy();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    Matrix4f rotationMatrix;
    Matrix4f projectionMatrix;
    Matrix4f translationMatrix;

    public void initTriangle()
    {
        shader = new Shader("C:\\Users\\Eric\\Documents\\NetBeansProjects\\\\ComputerGraphics-3Dgame\\src\\main\\test.vert",
                "C:\\Users\\Eric\\Documents\\NetBeansProjects\\\\ComputerGraphics-3Dgame\\src\\main\\test.frag");

        //VBO
        float[] vertices =
        {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f
        };

        int[] indices =
        {
            2, 0, 1,
            0, 2, 3,
            1, 4, 2,
            3, 2, 4,
            0, 3, 4,
            1, 0, 4
        };

        float[] colors =
        {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f
        };
        
        float[] textureCoords = 
        {
            
        };

        //model = new SimpleColorModel(vertices, indices, colors);
        model = Loader.loadObjModel("C:\\Users\\Eric\\Documents\\NetBeansProjects\\ComputerGraphics-3Dgame\\src\\resources\\bunnyplus.obj");
        //model = new TexturedModel(vertices, indices, textureCoords);
    }

    long time = 0;

    public void update()
    {
        time = System.currentTimeMillis() % (1080 * 10);
        rotationMatrix = Matrix4f.rotate(time / 10, 0, 1, 0);
        projectionMatrix = Matrix4f.frustum(-0.5f, 0.5f, -0.5f, 0.5f, 30.0f, 1.0f);
        translationMatrix = Matrix4f.translate(0, 0, -28);
    }

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.start();
            
            //add transformation matrices.
            FloatBuffer rotation = BufferUtils.createFloatBuffer(16);
            rotationMatrix.toBuffer(rotation);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "rotation"), false, rotation);
            
            FloatBuffer projection = BufferUtils.createFloatBuffer(16);
            projectionMatrix.toBuffer(projection);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "projection"), false, projection);
            
            FloatBuffer translation = BufferUtils.createFloatBuffer(16);
            translationMatrix.toBuffer(translation);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "translation"), false, translation);
            
            //render
            model.activate();

            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getNrIndices(), GL11.GL_UNSIGNED_INT, 0);

            model.deactivate();
            
            shader.stop();

            glfwSwapBuffers(window);

            glfwPollEvents();
            checkInput();
        }

        glfwTerminate();
    }
    
    public void checkInput()
    {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
        }
    }

    public static void main(String[] args)
    {
        main m = new main();
        m.initOpenGL();
        m.initTriangle();
        m.loop();
        m.destroyOpenGL();
    }
}
