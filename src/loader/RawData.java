package loader;

public class RawData
{

    public float[] vertices;
    public float[] normals;
    public float[] tangents;
    public float[] textureCoords;

    public int[] indices;
    public Material material;
    
    public float maxHeight;
    public float maxRadius = 0;
    
    public RawData(float[] vertices, float[] normals, float[] tangents, float[] textureCoords, int[] indices, Material material)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.normals = normals;
        this.tangents = tangents;
        this.material = material;
    }
}
