package framebuffer;

import java.nio.ByteBuffer;
import main.main;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class DepthFrameBuffer
{

    public static final int WIDTH = 4096;
    public static final int HEIGHT = 4096;

    private int frameBuffer;
    private int depthMap;

    public DepthFrameBuffer()
    {
        createFrameBuffer();
        createDepthAttachment(WIDTH, HEIGHT);
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
        //generate name for frame buffer
        frameBuffer = GL30.glGenFramebuffers();

        //create the framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
    }

    private void createDepthAttachment(int width, int height)
    {
        depthMap = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthMap);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height,
                          0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);

        float[] borderColor =
        {
            1, 1, 1, 1
        };
        GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, borderColor);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthMap, 0);

        //specificaly tell opengl we dont want to render any color data.
        GL11.glDrawBuffer(GL30.GL_NONE);
        GL11.glReadBuffer(GL30.GL_NONE);
    }

    public int getDepthMap()
    {
        return depthMap;
    }

    public void unbindFrameBuffer()
    {
        //call to switch to default frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, main.WIDTH, main.HEIGHT);
    }
}
