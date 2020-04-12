package gui;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.GUIShader;
import shader.Shader;
import util.Vector2f;
import util.Vector3f;

public class GUIText
{

    public static final float SPACE_LENGTH = 0.1f;

    GUIShader shader;

    String textString;

    Vector3f color = new Vector3f(1, 1, 1);
    Vector2f position;

    Font font;

    List<Word> textLine = new ArrayList<>();

    public GUIText(String text, Font font, Vector2f position)
    {
        this.textString = text;
        this.font = font;
        this.position = position;
        shader = new GUIShader("guiText.vert", "guiText.frag");

        createMesh(textString);
    }

    public GUIText(String text, Font font, Vector2f position, Vector3f color)
    {
        this(text, font, position);
        this.color = color;
    }

    private void createMesh(String text)
    {
        Vector2f currentPosition = new Vector2f(position.x, -position.y);
        //create lines
        for (String wordString : text.split(" "))
        {
            textLine.add(new Word(wordString, font));
        }

        for (Word word : textLine)
        {
            word.createMesh(currentPosition);
            currentPosition.x += word.getWidth() + font.spaceLength;
        }
    }

    public void render()
    {
        shader.start();

        shader.loadTextColor(color);

        for (Word word : textLine)
        {
            for (CharacterMesh c : word.characterMesh)
            {
                GL30.glBindVertexArray(c.vaoID);
                GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
                GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, font.textureID);

                //draw!
                GL11.glDrawElements(GL11.GL_TRIANGLES, c.nrOfIndices, GL11.GL_UNSIGNED_INT, 0);
            }
        }

        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);

        shader.stop();
    }
    
    public void destroy()
    {
        for (Word word : textLine)
        {
            for (CharacterMesh c : word.characterMesh)
            {
                c.destroy();
            }
        }
    }
}
