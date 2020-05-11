package camera;

import java.nio.DoubleBuffer;
import model.ModelHandler;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import static org.lwjgl.glfw.GLFW.*;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;

public class BuildCamera extends Camera implements GLFWScrollCallbackI
{

    Vector3f groundPosition = new Vector3f();

    private float speed = 1f;
    private float distance = 50;

    private boolean keyPressedLastTime = false;

    public BuildCamera(Vector3f groundPosition)
    {
        super(new Vector3f(1, 1, 1), groundPosition);
        setDistance();
    }

    double prevX = 500;
    double prevY = 500;

    public void checkInputFlying(long window, ModelHandler models)
    {
        Vector3f frontVector = groundPosition.subtract(position);
        frontVector.y = 0;
        frontVector = frontVector.normalize();

        Vector3f sideVector = frontVector.cross(new Vector3f(0, 1, 0));

        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            groundPosition = groundPosition.add(frontVector.scale(speed));
            position = position.add(frontVector.scale(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            groundPosition = groundPosition.add(frontVector.scale(-speed));
            position = position.add(frontVector.scale(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            groundPosition = groundPosition.add(sideVector.scale(-speed));
            position = position.add(sideVector.scale(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            groundPosition = groundPosition.add(sideVector.scale(speed));
            position = position.add(sideVector.scale(speed));
        }
        setLookAt(groundPosition);
    }

    boolean rotating = false;
    Vector2f prevCursor = new Vector2f();

    public void checkInput(long window, ModelHandler models)
    {

        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        float x = (float) xBuffer.get(0);
        float y = (float) yBuffer.get(0);

        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS)
        {
            if (!rotating)
            {
                rotating = true;
                prevCursor = new Vector2f(x, y);
            }

            float diffX = (prevCursor.x - x) / 5;
            float diffY = -(prevCursor.y - y) / 5;

            position = position.subtract(groundPosition);

            position = Matrix4f.rotate(0, (float) diffX, 0).multiply(position);
            Vector3f dir = position.cross(upVector);
            position = Matrix4f.rotate((float) diffY, dir).multiply(position);

            position = position.add(groundPosition);
            setLookAt(groundPosition);

            //glfwSetCursorPos(window, prevCursor.x, prevCursor.y);
            prevCursor = new Vector2f(x, y);
        } else
        {
            rotating = false;
        }

        checkInputFlying(window, models);
    }

    //callback for scroll wheel
    @Override
    public void invoke(long window, double xoffset, double yoffset)
    {
        distance -= yoffset * 4;
        setDistance();
    }

    private void setDistance()
    {
        if (distance < 10)
        {
            distance = 10;
        }
        speed = distance / 25;
        position = position.subtract(groundPosition);
        position = position.normalize().scale(distance);
        position = position.add(groundPosition);
    }
}
