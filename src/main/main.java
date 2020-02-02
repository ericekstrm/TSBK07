package main;

import java.nio.*;
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

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    Matrix4f rotation;

    public void initTriangle()
    {
        shader = new Shader("C:\\Users\\Eric\\Documents\\NetBeansProjects\\3D-spel\\src\\pkg3d\\spel\\test.vert",
                "C:\\Users\\Eric\\Documents\\NetBeansProjects\\3D-spel\\src\\pkg3d\\spel\\test.frag");

        //VBO
        float[] vertices =
        {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f
        };
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices);
        vertexBuffer.flip();

        int[] indices =
        {
            2, 0, 1,
            0, 2, 3,
            1, 4, 2,
            3, 2, 4,
            0, 3, 4,
            1, 0, 4
        };
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        float[] colors =
        {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f
        };
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorBuffer.put(colors);
        colorBuffer.flip();

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        //vertex
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //index
        int y = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, y);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        
        //color
        int x = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, x);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        //glBindVertexArray(0);
    }

    long time = 0;

    public void update()
    {
        time = System.currentTimeMillis() % (1080 * 10);
        rotation = Matrix4f.rotate(time/10, 1, 1, 1);
    }

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            shader.start();

            FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
            rotation.toBuffer(matrix);
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "rotation"), false, matrix);

            //render
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            GL11.glDrawElements(GL11.GL_TRIANGLES, 18, GL11.GL_UNSIGNED_INT, 0);

            shader.stop();

            glDisableVertexAttribArray(0);
            //glBindVertexArray(0);
            //glDisableClientState(GL_COLOR_ARRAY);

            glfwSwapBuffers(window);

            glfwPollEvents();

        }

        glfwTerminate();
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
