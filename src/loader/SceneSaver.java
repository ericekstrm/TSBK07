package loader;

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
                System.out.println("Scene created: " + myObj.getName());
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

            myWriter.close();
            System.out.println("Successfully wrote scene to file.");
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void writeModels(FileWriter wr, ModelHandler m) throws IOException
    {
        for (Model model : m.getModels())
        {

            wr.write("m " + model.objectFileName + " " 
                    + model.getPosition().toString() + " "
                    + model.getScale().toString() + " "
                    + model.getRotation().toString() + " "
                    + "\n");
        }
    }
}
