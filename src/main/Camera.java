package main;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import util.Matrix4f;
import util.Vector3f;

public class Camera
{

    Vector3f position;
    Vector3f lookAt;
    Vector3f upVector = new Vector3f(0, 1, 0);

    public Camera(Vector3f position, Vector3f lookAt)
    {
        this.position = position;
        this.lookAt = lookAt;
    }

    public void checkInput(long window)
    {
        float speed = 0.02f;
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            //forward
            Vector3f direction = lookAt.subtract(position);
            Vector3f movement = direction.scale(speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            Vector3f direction = lookAt.subtract(position);
            Vector3f movement = direction.scale(-speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            Vector3f direction = lookAt.subtract(position).cross(upVector);
            Vector3f movement = direction.scale(-speed);
            move(movement);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            Vector3f direction = lookAt.subtract(position).cross(upVector);
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
        Vector3f n = position.subtract(lookAt).normalize();
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
