package loader;

public class RawData
{

    public float[] vertices;
    public float[] normals;
    public float[] textureCoords;

    public int[] indices;
    public Material material;
    
    public RawData(float[] vertices, float[] normals, float[] textureCoords, int[] indices, Material material)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.normals = normals;
        this.material = material;
    }
}
