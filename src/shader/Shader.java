package shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public abstract class Shader
{

    private int programID;
    private int vertexID;
    private int fragmentID;

    public static final int POS_ATTRIB = 0;
    public static final int TEX_ATTRIB = 1;
    public static final int NORMAL_ATTRIB = 2;
    public static final int COLOR_ATTRIB = 2;

    public Shader(String vertexFile, String fragmentFile)
    {
        vertexID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);
        bindAttributes();

        GL20.glLinkProgram(programID);
        getAllUniformLocations();
    }

    protected int getUniformLocation(String uniformName)
    {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    protected abstract void getAllUniformLocations();

    public void start()
    {
        GL20.glUseProgram(programID);
    }

    public void stop()
    {
        GL20.glUseProgram(0);
    }

    public void cleanUp()
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
        bindAttribute(NORMAL_ATTRIB, "in_Normal");
        bindAttribute(COLOR_ATTRIB, "in_Color");
    }

    protected void bindAttribute(int attribute, String variableName)
    {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    public int getProgramID()
    {
        return programID;
    }

    protected void loadInt(int location, int value)
    {
        GL20.glUniform1i(location, value);
    }

    protected void loadFloat(int location, float value)
    {
        GL20.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        vector.toBuffer(buffer);
        GL20.glUniform3fv(location, buffer);
    }

    protected void loadVector(int location, Vector4f vector)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        vector.toBuffer(buffer);
        GL20.glUniform4fv(location, buffer);
    }

    protected void loadMatrix(int location, Matrix4f matrix)
    {
        FloatBuffer translation = BufferUtils.createFloatBuffer(16);
        matrix.toBuffer(translation);
        //translation.flip();
        glUniformMatrix4fv(location, false, translation);
    }

    protected void loadList3f(int location, float[] list)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(list.length);
        for (float f : list)
        {
            buffer.put(f);
        }
        buffer.flip();
        glUniform3fv(location, buffer);
    }
    
    protected void loadBoolean(int location, boolean bool)
    {
        GL20.glUniform1i(location, bool ? 1 : 0);
    }

    public static int loadShader(String file_name, int type)
    {
        StringBuilder shaderSource = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("src/shaderfiles/" + file_name));
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
}
