package model;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import main.Camera;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import shader.Shader;
import util.Matrix4f;

public class Skybox extends Model
{

    int texID;
    Shader shader;

    /**
     *
     * @param texture
     */
    public Skybox(String[] texture)
    {
        activeVBOs.add(ModelLoader.loadVertexVBO(Shader.POS_ATTRIB, skyboxVertices));
        activeAttribs.add(Shader.POS_ATTRIB);

        GL30.glBindVertexArray(0);

        shader = new Shader("skybox.vert", "skybox.frag");
        loadTexture(texture);
    }

    @Override
    public void render(Shader shader)
    {
        activate();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        deactivate();
        this.shader.stop();
    }

    /**
     * Add textures in this order:
     *
     * GL_TEXTURE_CUBE_MAP_POSITIVE_X	Right GL_TEXTURE_CUBE_MAP_NEGATIVE_X	Left
     * GL_TEXTURE_CUBE_MAP_POSITIVE_Y	Top GL_TEXTURE_CUBE_MAP_NEGATIVE_Y	Bottom
     * GL_TEXTURE_CUBE_MAP_POSITIVE_Z	Back GL_TEXTURE_CUBE_MAP_NEGATIVE_Z	Front
     *
     * @param texture list of all texture file names.
     */
    private void loadTexture(String[] texture)
    {
        ByteBuffer buffer;

        texID = GL11.glGenTextures();
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < texture.length; i++)
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                File file = new File("res\\" + texture[i]);
                String filePath = file.getAbsolutePath();
                buffer = STBImage.stbi_load(filePath, w, h, channels, 4);
                if (buffer == null)
                {
                    throw new Exception("Can't load file " + texture + " " + STBImage.stbi_failure_reason());
                }

                GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, w.get(), h.get(), 0,
                                  GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                STBImage.stbi_image_free(buffer);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR);
        //GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR_MIPMAP_LINEAR);

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_R, GL13.GL_CLAMP_TO_EDGE);

        GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader.getProgramID(), "skybox"), 1);
    }

    public void prepareForRender(Camera camera)
    {
        shader.start();

        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        Matrix4f mat = camera.getWorldtoViewMatrix();
        Matrix4f.remove_translation(mat);
        mat.toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);
    }

    private static final float[] skyboxVertices =
    {
        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f
    };
}
