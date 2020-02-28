package loader;

import java.util.ArrayList;
import java.util.List;

public class RawData
{

    public float[] vertices;
    public float[] normals;
    public float[] textureCoords;

    public List<int[]> indices = new ArrayList<>();
    public List<Texture> textures = new ArrayList<>();
    public List<MaterialProperties> matprop = new ArrayList<>();

    public RawData(float[] vertices, float[] normals, float[] textureCoords, ArrayList<int[]> indices, ArrayList<Texture> textures, ArrayList<MaterialProperties> matProp)
    {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.textures = textures;
        this.normals = normals;
        this.matprop = matProp;
    }
}
