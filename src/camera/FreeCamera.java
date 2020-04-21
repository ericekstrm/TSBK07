package camera;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import util.Matrix4f;
import util.Vector3f;

public class FreeCamera extends Camera implements GLFWScrollCallbackI
{

    float speed = 1f;

    boolean keyPressedLastTime = false;

    public FreeCamera(Vector3f position, Vector3f lookAt)
    {
        super(position, lookAt);
    }

    double prevX = 200;
    double prevY = 200;

    public void checkInputFlying(long window)
    {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            //forward
            Vector3f movement = direction.scale(speed);
            position = position.add(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            Vector3f movement = direction.scale(-speed);
            position = position.add(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            Vector3f direction = this.direction.cross(upVector);
            Vector3f movement = direction.scale(-speed);
            position = position.add(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            Vector3f direction = this.direction.cross(upVector);
            Vector3f movement = direction.scale(speed);
            position = position.add(movement);
        }
    }

    public void checkInput(long window)
    {
        //rotation camera
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        double x = (float) xBuffer.get(0);
        double y = yBuffer.get(0);

        direction = Matrix4f.rotate(0, (float) (prevX - x) / 10, 0).multiply(direction);
        Vector3f dir = direction.cross(upVector);
        direction = Matrix4f.rotate((float) (prevY - y) / 10, dir).multiply(direction);

        if (Math.abs(upVector.dot(direction)) > 0.7f)
        {
            direction = Matrix4f.rotate((float) (y - prevY) / 10, dir).multiply(direction);
        }
        glfwSetCursorPos(window, prevX, prevY);

        checkInputFlying(window);
    }

    //callback for scroll wheel
    @Override
    public void invoke(long window, double xoffset, double yoffset)
    {
        if (yoffset < 0)
        {
            speed /= 2;
        } else
        {
            speed *= 2;
        }

        //limit speed to interval [0.1, 10]
        if (speed < 0.0625f)
        {
            speed = 0.0625f;
        }
        if (speed > 16)
        {
            speed = 16;
        }
        System.out.println("Camera speed: " + speed);
    }
}
