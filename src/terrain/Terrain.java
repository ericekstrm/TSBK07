package terrain;

import java.nio.FloatBuffer;
import light.Lights;
import main.Camera;
import model.Model;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import shader.Shader;

public class Terrain
{

    //static List<Model> terrainTiles = new ArrayList<>();
    Model terrain;
    Shader shader = new Shader("test.vert", "test.frag");

    public Terrain()
    {
        terrain = new Model(shader, TerrainLoader.generateTerrain("height_map.png", "grass.jpg"));
    }

    public void render(Camera camera, Lights lights)
    {
        shader.start();
        
        lights.loadLights(shader);
        
        FloatBuffer viewPos = BufferUtils.createFloatBuffer(3);
        camera.getPosition().toBuffer(viewPos);
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "viewPos"), viewPos);

        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        camera.getWorldtoViewMatrix().toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);
        
        terrain.render(shader);
        
        
        shader.stop();
    }
    
    public float getHeight(float x, float z)
    {
        float height = 
        
        
        return height;
    }

}
