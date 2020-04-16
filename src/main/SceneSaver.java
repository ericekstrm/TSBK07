package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import light.LightHandler;
import model.Model;
import model.ModelHandler;
import terrain.TerrainHandler;
import water.WaterHandler;

public class SceneSaver
{

    /**
     * Saves all objects of the scene to a text file.
     *
     * File structure:
     *
     *
     *
     * @param sceneName
     * @param m
     * @param t
     * @param l
     * @param w
     * @return true if the scene was saved successfully.
     */
    public static boolean saveScene(String sceneName,
                                    ModelHandler m,
                                    TerrainHandler t,
                                    LightHandler l,
                                    WaterHandler w)
    {

        try
        {
            File myObj = new File("res/scenes/" + sceneName + ".txt");
            if (myObj.createNewFile())
            {
                System.out.println("File created: " + myObj.getName());
            } else
            {
                System.out.println("Scene already exists.");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            FileWriter myWriter = new FileWriter("res/scenes/" + sceneName + ".txt");

            writeModels(myWriter, m);

            //myWriter.write("Files in Java might be tricky, but it is fun enough!");
            //myWriter.write("Files in Java mighnough!");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void writeModels(FileWriter wr, ModelHandler m) throws IOException
    {
        for (Map.Entry<String, Model> model : m.getModels().entrySet())
        {
            String key = model.getKey();
            Model value = model.getValue();

            wr.write(key + " " 
                    + value.getPosition().toString() + " "
                    + value.getScale().toString() + " "
                    + "\n");

        }
    }
}
