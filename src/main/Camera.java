package main;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import util.Matrix4f;
import util.Vector3f;

public class Camera
{

    Vector3f position;
    Vector3f direction;
    Vector3f upVector = new Vector3f(0, 1, 0);

    public Camera(Vector3f position, Vector3f lookAt)
    {
        this.position = position;
        this.direction = lookAt.subtract(position).normalize();
    }

    double prevX = 200;
    double prevY = 200;

    public void checkInput(long window)
    {

        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        double x = (float) xBuffer.get(0);
        double y = yBuffer.get(0);

        direction = Matrix4f.rotate(0, (float) (prevX - x) / 10, 0).multiply(direction);
        Vector3f dir = direction.cross(upVector);
        direction = Matrix4f.rotate((float) (prevY - y) / 10, dir).multiply(direction);
        System.out.println(upVector.dot(direction));
        
        if (Math.abs(upVector.dot(direction)) > 0.7f)
        {
            direction = Matrix4f.rotate((float) (y - prevY) / 10, dir).multiply(direction);
        }

        //prevX = x;
        //prevY = y;
        System.out.println("Mouse Pos: " + x + ", " + y);
        glfwSetCursorPos(window, 200, 200);

        float speed = 0.1f;
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            //forward
            Vector3f movement = direction.scale(speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            Vector3f movement = direction.scale(-speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            Vector3f direction = this.direction.cross(upVector);
            Vector3f movement = direction.scale(-speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            Vector3f direction = this.direction.cross(upVector);
            Vector3f movement = direction.scale(speed);
            move(movement);
        }
    }

    public void move(Vector3f movement)
    {
        position = position.add(movement);
    }

    /**
     * Calculates the World-to-View matrix according to the steps on page 53 in
     * "Polygons feel no Pain".
     *
     * @return
     */
    public Matrix4f getWorldtoViewMatrix()
    {
        Vector3f n = direction.scale(-1);
        Vector3f u = upVector.cross(n);
        Vector3f v = n.cross(u);

        Matrix4f rotation = new Matrix4f(
                u.x, u.y, u.z, 0,
                v.x, v.y, v.z, 0,
                n.x, n.y, n.z, 0,
                0, 0, 0, 1);
        Matrix4f translation = Matrix4f.translate(-position.x, -position.y, -position.z);

        return rotation.multiply(translation);
    }
}
