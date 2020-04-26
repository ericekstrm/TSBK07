package main;

import camera.Camera;
import java.nio.DoubleBuffer;
import light.LightHandler;
import loader.Loader;
import model.ColorModel;
import model.ModelHandler;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import shader.TextureModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class Player
{

    Vector3f position;
    Vector3f direction;

    public Camera thirdPersonCamera;
    public Camera firstPersonCamera;

    float playerSpeed = 1;

    ColorModel model;
    TextureModelShader shader;

    public Player(Vector3f pos, ModelHandler models, Matrix4f projectionMatrix)
    {
        this.position = pos;
        thirdPersonCamera = new Camera(new Vector3f(-30, 30, -30), pos);
        setDirection();
        firstPersonCamera = new Camera(pos.add(new Vector3f(0,3,0)), pos.add(direction));

        model = new ColorModel(Loader.loadObj("character.obj"));
        model.setPosition(position);

        shader = new TextureModelShader(projectionMatrix);
    }

    public void checkInput(long window, TerrainHandler terrain)
    {
        rotateCamera(window);

        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            position = position.add(direction).scale(playerSpeed);
            thirdPersonCamera.position = thirdPersonCamera.position.add(direction).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            position = position.subtract(direction).scale(playerSpeed);
            thirdPersonCamera.position = thirdPersonCamera.position.subtract(direction).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            Vector3f moveDir = direction.cross(new Vector3f(0, 1, 0));
            position = position.subtract(moveDir).scale(playerSpeed);
            thirdPersonCamera.position = thirdPersonCamera.position.subtract(moveDir).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            Vector3f moveDir = direction.cross(new Vector3f(0, 1, 0));
            position = position.add(moveDir).scale(playerSpeed);
            thirdPersonCamera.position = thirdPersonCamera.position.add(moveDir).scale(playerSpeed);
        }
        
        float terrainHeight = terrain.getHeight(position.x, position.z);
        float heightDiff = terrainHeight - position.y;
        position.y = terrainHeight;
        firstPersonCamera.position = position.add(new Vector3f(0,2.5f,0));
        setDirection();

        thirdPersonCamera.position.y += heightDiff;
        thirdPersonCamera.setLookAt(position);
        firstPersonCamera.direction = thirdPersonCamera.direction;
        model.setPosition(position);
    }

    boolean rotating = false;
    Vector2f prevCursor = new Vector2f();

    public void rotateCamera(long window)
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

            thirdPersonCamera.position = thirdPersonCamera.position.subtract(position);

            thirdPersonCamera.position = Matrix4f.rotate(0, (float) diffX, 0).multiply(thirdPersonCamera.position);
            Vector3f dir = thirdPersonCamera.position.cross(thirdPersonCamera.upVector);
            thirdPersonCamera.position = Matrix4f.rotate((float) diffY, dir).multiply(thirdPersonCamera.position);

            thirdPersonCamera.position = thirdPersonCamera.position.add(position);

            //glfwSetCursorPos(window, screenCenter.x, screenCenter.y);
            prevCursor = new Vector2f(x, y);
        } else
        {
            rotating = false;
        }
    }

    public void update(float deltaTime)
    {

    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane, Matrix4f projectionMatrix)
    {
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);
        shader.loadProjectionMatrix(projectionMatrix);
        
        model.render(shader);

        shader.stop();
    }

    public void setDirection()
    {
        direction = thirdPersonCamera.direction;
        direction.y = 0;
        direction = direction.normalize();
    }

    public Camera getCamera()
    {
        return thirdPersonCamera;
    }
}
