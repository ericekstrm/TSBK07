package shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import util.Matrix4f;

public class Shader
{
    private int programID;
    private int vertexID;
    private int fragmentID;

    public static final int POS_ATTRIB = 0;
    public static final int TEX_ATTRIB = 1;

    Matrix4f projectionMatrix = Matrix4f.frustum_new();

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

        //projection matrix
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        projectionMatrix.toBuffer(projection);
        glUniformMatrix4fv(glGetUniformLocation(getProgramID(), "projection"), false, projection);
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
        bindAttribute(POS_ATTRIB, "in_Position");
        bindAttribute(TEX_ATTRIB, "in_Texture");
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
            BufferedReader reader = new BufferedReader(new FileReader("src\\shader\\" + file_name));
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
