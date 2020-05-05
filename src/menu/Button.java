package menu;

import gui.Font;
import gui.FontLoader;
import gui.GUIText;
import util.Vector2f;

public class Button
{

    public static final Font font = FontLoader.load("comic_sans", 32);

    GUIText text;
    Vector2f position;
    Vector2f size = new Vector2f(0.3f, 0.1f);
    
    String action = "";

    public Button(String text, float x, float y, String action)
    {
        this.position = new Vector2f(x, y);
        this.text = new GUIText(text, font, position);
        this.action = action;
    }

    public void render()
    {
        text.render();
    }

    public boolean contains(float xpos, float ypos)
    {
        return (xpos > position.x && xpos < position.x + size.x &&
                ypos > position.y - size.y && ypos < position.y);
    }
    
    public String text()
    {
        return text.getString();
    }
    
    public Vector2f getPosition()
    {
        return position;
    }
    
    public String getAction()
    {
        return action;
    }
}
