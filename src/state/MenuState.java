package state;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import main.main;
import menu.Button;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MenuState extends State implements GLFWMouseButtonCallbackI
{

    List<Button> buttons = new ArrayList<>();

    @Override
    public void init()
    {
        buttons.add(new Button("Play", -0.9f, -0.9f, "game"));
        buttons.add(new Button("Level Editor", -0.9f, -0.8f, "levelEditor"));
        buttons.add(new Button("Quit", -0.9f, 0f, "quit"));
    }

    @Override
    public void update(float deltaTime)
    {
    }

    @Override
    public void render(long window)
    {
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        for (Button b : buttons)
        {
            b.render();
        }
        glfwSwapBuffers(window);
    }

    @Override
    public void checkInput(long window)
    {
    }

    @Override
    public void activateState(long window, TransitionInformation t)
    {
        glfwSetMouseButtonCallback(window, this);
    }

    @Override
    public TransitionInformation deactivateState(long window)
    {
        glfwSetMouseButtonCallback(window, null);

        TransitionInformation t = new TransitionInformation(name());
        t.put("scene", "test");
        return t;
    }

    @Override
    public String name()
    {
        return "menu";
    }

    /**
     * Mouse click callback.
     *
     */
    @Override
    public void invoke(long window, int button, int action, int mods)
    {
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)
        {
            DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xBuffer, yBuffer);
            float xpos = (float) xBuffer.get(0) / main.WIDTH * 2 - 1;
            float ypos = (float) yBuffer.get(0) / main.HEIGHT * 2 - 1;

            //System.out.println(xpos + " " + ypos);
            for (Button b : buttons)
            {
                if (b.contains(xpos, ypos))
                {
                    changeState = b.getAction();
                    return;
                }
            }
        }
    }
}
