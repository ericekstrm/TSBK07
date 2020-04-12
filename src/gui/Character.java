package gui;

import java.util.ArrayList;
import java.util.List;
import loader.Loader;
import loader.RawData;
import model.ModelLoader;
import org.lwjgl.opengl.GL30;
import util.Vector2f;

public class Character
{

    //ascii id
    private int id;

    //texture space
    private float xTextureCoord;
    private float yTextureCoord;
    private float xMaxTextureCoord;
    private float yMaxTextureCoord;

    //screen space
    private float xOffset;
    private float yOffset;
    private float sizeX;
    private float sizeY;
    private float xAdvance;

    /**
     * @param id - the ASCII value of the character.
     * @param xTextureCoord - the x texture coordinate for the top left corner
     * of the character in the texture atlas.
     * @param yTextureCoord - the y texture coordinate for the top left corner
     * of the character in the texture atlas.
     * @param xTexSize - the width of the character in the texture atlas.
     * @param yTexSize - the height of the character in the texture atlas.
     * @param xOffset - the x distance from the cursor to the left edge of the
     * character's quad.
     * @param yOffset - the y distance from the cursor to the top edge of the
     * character's quad.
     * @param sizeX - the width of the character's quad in screen space.
     * @param sizeY - the height of the character's quad in screen space.
     * @param xAdvance - how far in pixels the cursor should advance after
     * adding this character.
     */
    protected Character(int id, float xTextureCoord, float yTextureCoord, float xTexSize, float yTexSize,
                        float xOffset, float yOffset, float sizeX, float sizeY, float xAdvance)
    {
        this.id = id;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.xMaxTextureCoord = xTexSize + xTextureCoord;
        this.yMaxTextureCoord = yTexSize + yTextureCoord;
        this.xAdvance = xAdvance;

    }

    protected Character(Character other)
    {
        this.id = other.id;
        this.xTextureCoord = other.xTextureCoord;
        this.yTextureCoord = other.yTextureCoord;
        this.xOffset = other.xOffset;
        this.yOffset = other.yOffset;
        this.sizeX = other.sizeX;
        this.sizeY = other.sizeY;
        this.xMaxTextureCoord = other.xTextureCoord;
        this.yMaxTextureCoord = other.yTextureCoord;
        this.xAdvance = other.xAdvance;
    }

    /**
     *
     *
     * @param position - position of the character. in normalized coordinate
     * space [1, -1].
     * @return a 2D-vector with the x component representing the vaoID of the
     * mesh and the y component represents the number of vertices in the mesh.
     */
    public CharacterMesh createMesh(Vector2f position)
    {
        RawData data = Loader.loadQuad(position.x + xOffset, position.y + yOffset, sizeX, sizeY,
                                       xTextureCoord, yTextureCoord, xMaxTextureCoord, yMaxTextureCoord);
        //add new vao to list
        int vaoID = GL30.glGenVertexArrays();
        List<Integer> activeVBOs = new ArrayList<>();
        GL30.glBindVertexArray(vaoID);

        //add data that is the same for all vaos (this is where there is a lot of memory waste.)
        activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));
        activeVBOs.add(ModelLoader.loadTextureVBO(data.textureCoords));

        //add data that is specific to that vao
        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));

        GL30.glBindVertexArray(0);
        return new CharacterMesh(vaoID, activeVBOs, data.indices.length);
    }

    protected int getId()
    {
        return id;
    }

    protected float getxTextureCoord()
    {
        return xTextureCoord;
    }

    protected float getyTextureCoord()
    {
        return yTextureCoord;
    }

    protected float getXMaxTextureCoord()
    {
        return xMaxTextureCoord;
    }

    protected float getYMaxTextureCoord()
    {
        return yMaxTextureCoord;
    }

    protected float getxOffset()
    {
        return xOffset;
    }

    protected float getyOffset()
    {
        return yOffset;
    }

    protected float getSizeX()
    {
        return sizeX;
    }

    protected float getSizeY()
    {
        return sizeY;
    }

    protected float getxAdvance()
    {
        return xAdvance;
    }
}
