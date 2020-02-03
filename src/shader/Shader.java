package shader;

import java.io.BufferedReader;
import java.io.FileReader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader
{
    private int programID;
    private int vertexID;
    private int fragmentID;

    public Shader(String vertexFile, String fragmentFile)
    {
        vertexID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);
        bindAttributes();
        
        GL20.glLinkProgram(programID);
    }
    
    public void start()
    {
        GL20.glUseProgram(programID);
    }
    
    public void stop()
    {
        GL20.glUseProgram(0);
    }
    
    public void clenUp()
    {
        stop();
        GL20.glDetachShader(programID, vertexID);
        GL20.glDetachShader(programID, fragmentID);
        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);
    }
    
    public void bindAttributes()
    {
        bindAttribute(0, "in_Position");
        bindAttribute(1, "in_Color");
        bindAttribute(2, "in_Texture");
    }
    
    protected void bindAttribute(int attribute, String variableName)
    {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }
    

    public static int loadShader(String file_name, int type)
    {
        StringBuilder shaderSource = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file_name));
            String line;
            while ((line = reader.readLine()) != null)
            {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (Exception e)
        {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader " + file_name);
            System.exit(-1);
        }
        return shaderID;
    }

    public int getProgramID()
    {
        return programID;
    }

    public int getVertexID()
    {
        return vertexID;
    }

    public int getFragmentID()
    {
        return fragmentID;
    }
}
