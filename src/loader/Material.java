package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Vector3f;

public class Material
{
    

    public String name = "";
    public Vector3f Ka = new Vector3f(0.2f, 0.2f, 0.2f);
    public Vector3f Kd = new Vector3f(1, 1, 1);
    public Vector3f Ks = new Vector3f();
    public Vector3f Ke = new Vector3f();
    public float Ns = 1;
    public float Ni = 0;
    public float d = 0;
    public float illum = 0;

    public int Ka_map = 0; // ambient texture map
    public int Kd_map = 0; // diffuse texture map
    public int d_map = 0;  // alpha texture map
    public int bump_map = 0;

    public static Map<String, Material> loadMtlFile(String folder, String mtlfile)
    {
        Map<String, Material> newMaterials = new HashMap<>();
        BufferedReader br = null;
        try
        {
            System.out.println("Loading material: " + mtlfile);
            br = new BufferedReader(new FileReader("res/" + folder + mtlfile));
        } catch (FileNotFoundException ex)
        {
            System.out.println("Material file not found: " + folder + mtlfile);
            return null;
        }

        Material currentMaterial = null;

        try
        {
            String line = br.readLine();
            while (line != null)
            {
                line = line.replaceAll("\\s+", " ").trim();

                String[] currentLine = line.split(" ");
                if (line.startsWith("newmtl"))
                {
                    if (currentMaterial != null)
                    {
                        newMaterials.put(currentMaterial.name, currentMaterial);
                    }
                    currentMaterial = new Material();
                    currentMaterial.name = currentLine[1];
                } else if (line.startsWith("Ka "))
                {
                    currentMaterial.Ka = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                } else if (line.startsWith("Kd "))
                {
                    currentMaterial.Kd = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                } else if (line.startsWith("Ks "))
                {
                    currentMaterial.Ks = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                } else if (line.startsWith("Ke "))
                {
                    currentMaterial.Ke = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                } else if (line.startsWith("Ni "))
                {
                    currentMaterial.Ni = Float.parseFloat(currentLine[1]);
                } else if (line.startsWith("d "))
                {
                    currentMaterial.d = Float.parseFloat(currentLine[1]);
                } else if (line.startsWith("illum "))
                {
                    currentMaterial.illum = Float.parseFloat(currentLine[1]);
                } else if (line.startsWith("map_Ka"))
                {
                    currentMaterial.Ka_map = Texture.load(folder + currentLine[1]);
                } else if (line.startsWith("map_Kd"))
                {
                    currentMaterial.Kd_map = Texture.load(folder + currentLine[1]);
                } else if (line.startsWith("map_d"))
                {
                    currentMaterial.d_map = Texture.load(folder + currentLine[1]);
                } else if (line.startsWith("map_bump") || line.startsWith("bump"))
                {
                    currentMaterial.bump_map = Texture.load(folder + currentLine[1]);
                }

                line = br.readLine();
            }
            newMaterials.put(currentMaterial.name, currentMaterial);

        } catch (IOException ex)
        {
            Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newMaterials;
    }

    public void print()
    {
        System.out.println("=====| " + name + "|========");
        System.out.println("Ka: " + Ka.x + ", " + Ka.y + ", " + Ka.z);
        System.out.println("Kd: " + Kd.x + ", " + Kd.y + ", " + Kd.z);
        System.out.println("Ks: " + Ks.x + ", " + Ks.y + ", " + Ks.z);
        System.out.println("Ke: " + Ke.x + ", " + Ke.y + ", " + Ke.z);
        System.out.println("Ns: " + Ns);
        System.out.println("Ni: " + Ni);
        System.out.println("d: " + d);
        System.out.println("illum: " + illum);
    }
}
