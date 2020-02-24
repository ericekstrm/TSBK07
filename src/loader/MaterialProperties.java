package loader;

public class MaterialProperties
{

    public float Ka = 0.2f;
    public float Kd = 1;
    public float Ks = 0.5f;

    public float specularExponent = 4;

    public MaterialProperties()
    {
    }

    public MaterialProperties(float Ka, float Kd, float Ks, float specularExponent)
    {
        this.Ka = Ka;
        this.Kd = Kd;
        this.Ks = Ks;
        this.specularExponent = specularExponent;
    }
}
