package model;

public class RawData {
    
    public float[] vertices;
    public float[] textureCoords;
    public int[] indices;
    public float[] normals;
    int textureID;

    public RawData(float[] vertices, float[] textureCoords, int[] indices, float[] normals, int textureID)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.textureID = textureID;
        this.normals = normals;
    }
}
