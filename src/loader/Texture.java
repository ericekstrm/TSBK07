package loader;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
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
    
    public Texture(int... textureNames)
    {
        for (int texturename : textureNames)
        {
            textureIDs.add(texturename);
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

        TextureBufferData data = loadTexture(texture);

        if (data == null)
        {
            return 0;
        }

        int id = GL11.glGenTextures();
        textureIdMap.put(texture, id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.width, data.height, 0,
                          GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(data.buffer);
        return id;
    }

    /**
     * Order of filenames: Right face, Left face, Top face, Bottom face, Back
     * face, Front face
     *
     * @param textureFiles
     * @return
     */
    public static int loadCubeMap(String[] textureFiles)
    {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++)
        {
            TextureBufferData data = loadTexture(textureFiles[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.width, data.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer);
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        textureIdMap.put("cubemap" + textureFiles[0], texID);
        return texID;
    }

    private static TextureBufferData loadTexture(String textureFile)
    {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            File file = new File("res/" + textureFile);
            String filePath = file.getAbsolutePath();
            buffer = STBImage.stbi_load(filePath, w, h, channels, 4);
            if (buffer == null)
            {
                throw new Exception("Can't load file " + textureFile + " " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            return new TextureBufferData(buffer, width, height);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static class TextureBufferData
    {

        ByteBuffer buffer;

        int width;
        int height;

        public TextureBufferData(ByteBuffer buffer, int width, int height)
        {
            this.buffer = buffer;
            this.width = width;
            this.height = height;
        }
    }
}
