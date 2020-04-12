package gui;

import java.util.HashMap;
import java.util.Map;

public class Font
{

    //list of all the characters that make up the font
    Map<Integer, Character> characters = new HashMap<>();

    public float spaceLength = 0.05f;

    //the texture id of the font bitmap
    int textureID;

    public Font(Map<Integer, Character> characters)
    {
        this.characters = characters;
    }

    public Character getCharacter(int id)
    {
        Character c = characters.get(id);
        if (c == null)
        {
            System.out.println("character with id :" + id + " does not exist!");
        }
        return c;
    }

    public void setTextureID(int textureID)
    {
        this.textureID = textureID;
    }
}
