package model;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import org.lwjgl.opengl.GL30;
import shader.Shader;

public class TexturedModel extends Model
{

    int texID = 0;

    public TexturedModel(float[] vertices, int[] indices, float[] textures)
    {
        super();

        loadVertexVBO(0, vertices);
        activeAttribs.add(0);
        loadIndicesVBO(indices);
        loadTextureVBO(2, textures);
        activeAttribs.add(2);

        GL30.glBindVertexArray(0);
    }

    public void setTexture(String texture, Shader shader)
    {
        texID = Loader.loadTexture(texture);
        glBindTexture(GL_TEXTURE_2D, texID);
        glUniform1i(glGetUniformLocation(shader.getProgramID(), "texUnit"), 0);
    }

    @Override
    public void render(Shader shader)
    {
        FloatBuffer translation = BufferUtils.createFloatBuffer(16);
        getModelToViewMatrix().toBuffer(translation);
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "modelToWorld"), false, translation);

        glBindTexture(GL_TEXTURE_2D, texID);
        super.render(shader);
    }
}
