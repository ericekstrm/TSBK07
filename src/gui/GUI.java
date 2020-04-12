package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import loader.Texture;
import main.main;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import util.Vector2f;
import util.Vector3f;

public class GUI
{

    Map<String, GUIText> texts = new HashMap<>();
    Font font;

    List<GUIImage> images = new ArrayList<>();

    public GUI()
    {
        font = FontLoader.load("comic_sans", 32);
    }

    /**
     * Adds a line of text to the GUI.Coordinates are specified in the
 normalized coordinate space [1,-1] where the y-axis is inverted.
     *
     * @param textString - the string to be printed
     * @param name - name for finding the text later
     * @param x - x position of the bottom left corner of the text.
     * @param y - y position of the bottom left corner of the text.
     */
    public void addText(String textString, String name, float x, float y)
    {
        GUIText t = new GUIText(textString, font, new Vector2f(x, y));
        texts.put(name, t);
    }

    /**
     * Adds a line of text to the GUI.Coordinates are specified in the
     * normalized coordinate space [1,-1] where the y-axis is inverted.
     *
     * @param textString - the string to be printed
     * @param name - name for finding the text later
     * @param x - x position of the bottom left corner of the text.
     * @param y - y position of the bottom left corner of the text.
     * @param color - color of the text.
     */
    public void addText(String textString, String name, float x, float y, Vector3f color)
    {
        GUIText t = new GUIText(textString, font, new Vector2f(x, y), color);
        texts.put(name, t);
    }
    
    public void setTextString(String name, String newText)
    {
        if(texts.containsKey(name))
        {
            GUIText t = texts.get(name);
            float x = t.position.x;
            float y = t.position.y;
            Vector3f color = t.color;
            texts.remove(name);
            
            addText(newText, name, x, y, color);
        } else 
        {
            
        }
    }

    /**
     * Adds an image to the GUI. the coordinates specifies the upper left corner
     * of the image in window space.
     * 
     * @param texture - the file name of the texture to be added.
     * @param x - x position of the image.
     * @param y - y position of the image.
     * @param width - width of the image.
     * @param height - height of the image.
     */
    public void addImage(String texture, float x, float y, float width, float height)
    {
        float screenWidth = main.WIDTH;
        float screenHeight = main.HEIGHT;
        addImageNormalized(texture, (x / screenWidth) * 2 - 1, (y / screenHeight) * 2 - 1, width / screenWidth * 2, height / screenHeight * 2);
    }

    /**
     * Adds an image to the GUI. the coordinates specifies the upper left corner
     * of the image in normalized coordinate space [1, -1] with -1,-1 in the upper left corner.
     * 
     * @param texture - the file name of the texture to be added.
     * @param x - x position of the image.
     * @param y - y position of the image.
     * @param width - width of the image.
     * @param height - height of the image.
     */
    public void addImageNormalized(String texture, float x, float y, float width, float height)
    {
        images.add(new GUIImage(Texture.load(texture), new Vector2f(x, -y), new Vector2f(width, height)));
    }

    public void render()
    {
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        for (GUIImage i : images)
        {
            i.render();
        }
        
        for (GUIText t : texts.values())
        {
            t.render();
        }

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }
}
