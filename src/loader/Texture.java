package loader;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Texture
{

    private static HashMap<String, Integer> textureIdMap = new HashMap<>();

    private List<Integer> textureIDs = new ArrayList<>();

    public Texture(String... textureNames)
    {
        for (String texturename : textureNames)
        {
            textureIDs.add(load(texturename));
        }
    }

    public int size()
    {
        return textureIDs.size();
    }

    public int get(int i)
    {
        return textureIDs.get(i);
    }

    public static int load(String texture)
    {
        if (texture.equals(""))
        {
            return 0;
        }
        if (textureIdMap.containsKey(texture))
        {
            return textureIdMap.get(texture);
        }

        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            File file = new File("res/textures/" + texture);
            String filePath = file.getAbsolutePath();
            buffer = STBImage.stbi_load(filePath, w, h, channels, 4);
            if (buffer == null)
            {
                throw new Exception("Can't load file " + texture + " " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            int id = GL11.glGenTextures();
            textureIdMap.put(texture, id);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                              GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            STBImage.stbi_image_free(buffer);
            return id;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
