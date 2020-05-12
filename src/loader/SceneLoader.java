package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Model;
import util.Vector3f;

public class SceneLoader
{

    public static List<Model> loadModels(String filename)
    {
        //load .obj file from disk
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/scenes/" + filename + ".txt"));
            System.out.println("Loading scene: " + filename);

        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return new ArrayList<Model>();
        }

        List<Model> models = new ArrayList<>();

        try
        {
            String line = br.readLine();

            while (line != null)
            {
                String[] currentLine = line.split(" ");
                String start = currentLine[0];
                switch (start)
                {
                    case "m":
                        Model m = new Model(currentLine[1]);
                        Vector3f position = new Vector3f(Float.parseFloat(currentLine[2]),
                                                         Float.parseFloat(currentLine[3]),
                                                         Float.parseFloat(currentLine[4]));
                        Vector3f scale = new Vector3f(Float.parseFloat(currentLine[5]),
                                                      Float.parseFloat(currentLine[6]),
                                                      Float.parseFloat(currentLine[7]));
                        Vector3f rotation = new Vector3f(Float.parseFloat(currentLine[8]),
                                                      Float.parseFloat(currentLine[9]),
                                                      Float.parseFloat(currentLine[10]));
                        System.out.println(rotation.toString());
                        m.setPosition(position);
                        m.setScale(scale.x, scale.y, scale.z);
                        m.setRotation(rotation.x, rotation.y, rotation.z);
                        models.add(m);
                        break;
                    default:
                        throw new AssertionError();
                }
                line = br.readLine();
            }
        } catch (IOException ex)
        {
        }

        return models;
    }
}
