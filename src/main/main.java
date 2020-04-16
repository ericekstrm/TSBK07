package main;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import state.GameState;
import state.MenuState;
import state.State;
import static org.lwjgl.system.MemoryUtil.NULL;

public class main
{

    public static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static long window;

    private static long prevTime = System.currentTimeMillis();

    private static List<State> states = new ArrayList<>();
    private static State currentState;

    public static void main(String[] args)
    {
        initOpenGL();

        states.add(new GameState());
        states.add(new MenuState());
        currentState = states.get(0);

        for (State state : states)
        {
            state.init();
        }

        loop();
    }

    private static void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            long currentTime = System.currentTimeMillis();
            currentState.update(currentTime - prevTime);
            prevTime = currentTime;

            currentState.render(window);

            checkInput();

            updateState();
        }
        
        destroyOpenGL();
    }

    private static void checkInput()
    {
        glfwPollEvents();
        
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
        
        currentState.checkInput(window);
    }

    private static void updateState()
    {
        String newState = currentState.updateState();
        if (!newState.equals(""))
        {
            for (int i = 0; i < states.size(); i++)
            {
                if (states.get(i).name().equals(newState))
                {
                    currentState = states.get(i);
                    return;
                }
            }
        }
    }

    private static void initOpenGL()
    {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //create a window
        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Project", NULL, NULL);
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

        //glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        GL.createCapabilities();
        GL11.glClearColor(91.0f / 255.0f, 142f / 255.0f, 194.0f / 255.0f, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        //for transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    private static void destroyOpenGL()
    {
        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
