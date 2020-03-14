package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Vector3f;

public class Material
{

    public String name = "";
    public Vector3f Ka = new Vector3f(1,1,1);
    public Vector3f Kd = new Vector3f(1,1,1);
    public Vector3f Ks = new Vector3f();
    public Vector3f Ke = new Vector3f();
    public float Ns = 1;
    public float Ni = 0;
    public float d = 0;
    public float illum = 0;

    public static List<Material> loadMtlFile(String mtlfile)
    {
        List<Material> newMaterials = new ArrayList<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/materials" + mtlfile));
            System.out.println("Loading file: " + mtlfile);
        } catch (FileNotFoundException ex)
        {
            System.out.println("material file not found: " + mtlfile);
            return null;
        }

        Material currentMaterial = null;

        try
        {
            String line = br.readLine();
            while (line != null)
            {

                String[] currentLine = line.replaceAll("\\s+", " ").split(" ");
                if (line.startsWith("newmtl"))
                {
                    if (currentMaterial != null)
                    {
                        newMaterials.add(currentMaterial);
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
                }

                line = br.readLine();
            }

        } catch (IOException ex)
        {
            Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newMaterials;
    }
}
