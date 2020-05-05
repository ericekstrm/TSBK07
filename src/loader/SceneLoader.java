package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SceneLoader
{

    public static void loadScene(String filename)
    {
        String folder = "";
        //load .obj file from disk
        BufferedReader br = null;
        try
        {

            //if no dot is present, it is a folder instead
            folder = filename + "/";
            br = new BufferedReader(new FileReader("res/scenes/" + folder + filename + ".obj"));
            System.out.println("Loading scene: " + filename);

        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return;
        }
        
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
                        
                        break;
                    default:
                        throw new AssertionError();
                }
                

            }
        } catch (IOException ex)
        {
        }
    }
}
