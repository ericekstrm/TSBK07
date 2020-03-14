package loader;

public class RawData
{

    public float[] vertices;
    public float[] normals;
    public float[] textureCoords;

    public int[] indices;
    public Texture textures;
    public Material material;

    public RawData(float[] vertices, float[] normals, float[] textureCoords, int[] indices, Texture textures, Material material)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.textures = textures;
        this.normals = normals;
        this.material = material;
    }

    RawData(float[] vertices, float[] normals, float[] textureCoords, int[] indices, Material material)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.normals = normals;
        this.material = material;
    }
}
