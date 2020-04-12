package main;

import light.LightHandler;
import loader.Loader;
import model.ColorModel;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import shader.ColorModelShader;
import terrain.TerrainHandler;
import util.Vector3f;
import util.Vector4f;

public class Player
{

    Vector3f position;
    float speed = 1;

    ColorModel model;
    ColorModelShader shader;
    Camera camera;

    public Player(Vector3f position, long window)
    {
        this.position = position;

        model = new ColorModel(Loader.loadObj("character.obj"));
        model.setPosition(position);

        shader = new ColorModelShader();

        camera = new Camera(new Vector3f(10, 10, 10), position, window);
    }

    public void checkInput(long window, TerrainHandler terrain)
    {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            position.x += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            position.x -= speed;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            position.z += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            position.z -= speed;
        }
        position.y = terrain.getHeight(position.x, position.z);

        model.setPosition(position);
        camera.setLookAt(position);
    }

    public void update(float deltaTime)
    {

    }

    public void render(LightHandler lights, Vector4f clippingPlane)
    {
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);

        model.render(shader);

        shader.stop();
    }
}
