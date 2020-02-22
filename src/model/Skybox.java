package model;

import java.nio.FloatBuffer;
import main.Camera;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import shader.Shader;
import util.Matrix4f;

public class Skybox extends Model
{

    Shader skyboxShader;
    int texID;

    public Skybox(Shader skyboxShader, RawData... datas)
    {
        super(skyboxShader, datas);
        this.skyboxShader = skyboxShader;
        System.out.println(datas[0].indices.length);
    }

    public void render(Camera camera)
    {
        skyboxShader.start();

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        super.render(skyboxShader);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        deactivate();

        skyboxShader.stop();
    }

    public void prepareForRender(Camera camera)
    {
        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        Matrix4f mat = camera.getWorldtoViewMatrix();
        Matrix4f.remove_translation(mat);
        mat.toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(skyboxShader.getProgramID(), "worldToView"), false, worldToView);
    }
}
