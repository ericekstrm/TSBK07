package light;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import main.Camera;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import shader.Shader;
import util.Matrix4f;
import util.Vector3f;

public class Lights {
    
    Shader lightShader;
    
    List<PositionalLight> pointLights = new ArrayList<>();
    List<DirectionalLight> dirLights = new ArrayList<>();

    public Lights()
    {
        lightShader = new Shader("light.vert", "light.frag");
    }
    
    public void addPosLight(Vector3f pos, Vector3f color)
    {
        pointLights.add(new PositionalLight(pos, color));
    }
    
    public void addDirLight(Vector3f dir, Vector3f color)
    {
        dirLights.add(new DirectionalLight(dir, color));
    }
    
    
    public void render(Camera camera)
    {
        lightShader.start();
        
        
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        camera.getWorldtoViewMatrix().toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(lightShader.getProgramID(), "worldToView"), false, worldToView);

        for (PositionalLight light : pointLights)
        {
            light.render(lightShader);
        }
        
        
        lightShader.stop();
    }
    
    public void moveLight(int index, Matrix4f transform)
    {
        pointLights.get(index).setPosition(transform.multiply(pointLights.get(index).getPosition()));
    }
    
    public void loadLights(Shader shader)
    {
        //Pointlights position
        FloatBuffer pointLightPosArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (PositionalLight light : pointLights)
        {
            Vector3f pos = light.getPosition();
            pointLightPosArr.put(pos.x).put(pos.y).put(pos.z);
        }
        pointLightPosArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightPosArr"), pointLightPosArr);

        //Pointlights color
        FloatBuffer pointLightColorArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (PositionalLight light : pointLights)
        {
            Vector3f color = light.getColor();
            pointLightColorArr.put(color.x).put(color.y).put(color.z);
        }
        pointLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightColorArr"), pointLightColorArr);

        //Directional lights directions
        FloatBuffer dirLightDirArr = BufferUtils.createFloatBuffer(6);
        for (DirectionalLight dirLight : dirLights)
        {
            Vector3f dir = dirLight.getDirection();
            dirLightDirArr.put(dir.x).put(dir.y).put(dir.z);
        }
        dirLightDirArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightDirArr"), dirLightDirArr);

        //Directional lights color
        FloatBuffer dirLightColorArr = BufferUtils.createFloatBuffer(6);
        for (DirectionalLight dirLight : dirLights)
        {
            Vector3f color = dirLight.getColor();
            dirLightColorArr.put(color.x).put(color.y).put(color.z);
        }
        dirLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightColorArr"), dirLightColorArr);
    }
}
