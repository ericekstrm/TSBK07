package gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import loader.Texture;
import util.Vector2f;
import util.Vector3f;

public class FontLoader
{

    public static Font load(String fontName, int fontSize)
    {
        //load .obj file from disk
        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader("res/fonts/" + fontName + ".fnt"));
            System.out.println("Loading font: " + fontName + ".fnt");
        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + fontName + ".fnt");
            return null;
        }

        //skip the first lines (they do not contain relevent character data.
        try
        {
            float spaceLength = 0;
            float defaultFontSize;

            Map<String, Integer> currentLine = getLine(br.readLine());
            defaultFontSize = currentLine.get("size");
            currentLine = getLine(br.readLine());
            Vector2f fontImageSize = new Vector2f(currentLine.get("scaleW"), currentLine.get("scaleH"));
            float baseLine = currentLine.get("base") / fontImageSize.y * fontSize / defaultFontSize;
            br.readLine();
            br.readLine();

            Map<Integer, Character> characters = new HashMap<>();

            String line = br.readLine();
            while (line != null)
            {
                if (!line.startsWith("char"))
                {
                    line = br.readLine();
                    continue;
                }
                currentLine = getLine(line);

                Character c = getCharacter(currentLine, fontImageSize, fontSize / defaultFontSize, baseLine);
                if (c.getId() == 32)
                {
                    spaceLength = c.getxAdvance();
                }
                characters.put(c.getId(), c);

                line = br.readLine();
            }

            Font f = new Font(characters);
            f.setTextureID(Texture.load("../fonts/" + fontName + ".png"));
            f.spaceLength = spaceLength;

            return f;

        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Integer> getLine(String line) throws IOException
    {
        Map<String, Integer> currentLine = new HashMap<>();
        for (String part : line.split(" "))
        {
            if (part.contains("="))
            {
                String[] s = part.split("=");
                try
                {
                    currentLine.put(s[0], Integer.parseInt(s[1]));
                } catch (Exception e)
                {
                    currentLine.put(s[0], 0);
                }
            }
        }
        return currentLine;
    }

    private static Character getCharacter(Map<String, Integer> currentLine, Vector2f fontImageSize, float scaleFactor, float baseLine)
    {
        //the ascii id of character
        int id = currentLine.get("id");

        //texture coordinates of the character. normalized to [1,-1]
        float xTex = currentLine.get("x") / fontImageSize.x;
        float yTex = currentLine.get("y") / fontImageSize.y;

        //the width of the box that should be extracted from the texture. also normalized.
        float texWidth = currentLine.get("width") / fontImageSize.x;
        float texHeight = currentLine.get("height") / fontImageSize.y;

        //the size of the quad in screen space
        float quadWidth = texWidth * scaleFactor;
        float quadHeight = texHeight * scaleFactor;

        //how much to offset the quad in screen space.
        float xOffset = currentLine.get("xoffset") / fontImageSize.x * scaleFactor;
        float yOffset = baseLine - quadHeight - currentLine.get("yoffset") / fontImageSize.y * scaleFactor;

        //how much to advance the cursor to the next character
        float xAdvance = currentLine.get("xadvance") / fontImageSize.x * scaleFactor;
        
        return new Character(id, xTex, yTex, texWidth, texHeight, xOffset, yOffset, quadWidth, quadHeight, xAdvance);
    }
}
