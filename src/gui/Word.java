package gui;

import java.util.ArrayList;
import java.util.List;
import util.Vector2f;

public class Word {
    
    int horizontalOffset;
    List<Character> characters = new ArrayList<>();
    List<CharacterMesh> characterMesh = new ArrayList<>();
    float wordWidth;
    
    public Word(String wordString, Font font)
    {
        for (char c : wordString.toCharArray())
        {
            //extract ascii value
            int id = (int) c;
            
            Character character = font.getCharacter(id);
            characters.add(character);
            wordWidth += character.getxAdvance();
        }
    }
      
    public float getWidth()
    {
        return wordWidth;
    }

    void createMesh(Vector2f cursor)
    {
        float internalCursor = 0;
        for (Character c : characters)
        {
            characterMesh.add(c.createMesh(new Vector2f(cursor.x + internalCursor, cursor.y)));
            internalCursor += c.getxAdvance();
        }
    }
}
