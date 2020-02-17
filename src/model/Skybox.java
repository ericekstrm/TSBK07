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
    int texID;

    public Skybox(Shader shader, RawData... datas)
    {
        super(shader, datas);
        System.out.println(datas[0].indices.length);
    }

    @Override
    public void render(Shader shader)
    {
        //activate();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        
        super.render(shader);
        //GL11.glBindTexture(GL13.GL_TEXTURE_2D, texID);

        //GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        deactivate();
    }

    public void prepareForRender(Camera camera, Shader shader)
    {
        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        Matrix4f mat = camera.getWorldtoViewMatrix();
        Matrix4f.remove_translation(mat);
        mat.toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);
    }
}
