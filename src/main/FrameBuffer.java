package main;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBuffer
{

    public static final int WIDTH = 512;
    public static final int HEIGHT = 512;

    private int frameBuffer;
    private int texture;

    public FrameBuffer()
    {
        createFrameBuffer();
        texture = createTextureAttachment(WIDTH, HEIGHT);
        unbindFrameBuffer();
    }

    public void bindFrameBuffer()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }

    private void createFrameBuffer()
    {
        frameBuffer = GL30.glGenFramebuffers();
        //generate name for frame buffer

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        //create the framebuffer

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        //indicate that we will always render to color attachment 0
    }

    private int createTextureAttachment(int width, int height)
    {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                          0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                                  texture, 0);
        return texture;
    }

    public int getTexture()
    {
        //get the resulting texture
        return texture;
    }

    public void unbindFrameBuffer()
    {
        //call to switch to default frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, main.WIDTH, main.HEIGHT);
    }
}
