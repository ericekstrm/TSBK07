package model;

public class RawData {
    
    public float[] vertices;
    public float[] textureCoords;
    public int[] indices;
    int textureID;

    public RawData(float[] vertices, float[] textureCoords, int[] indices, int textureID)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.textureID = textureID;
    }
}
