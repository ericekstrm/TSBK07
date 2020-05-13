package model;

import camera.Camera;
import loader.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import shader.SkyBoxShader;
import util.Matrix4f;
import util.Vector3f;

public class Skybox extends Model
{

    public static final int SIZE = 5;

    SkyBoxShader skyboxShader;
    int texID;

    public Skybox(Matrix4f projectionMatrix)
    {
        this.skyboxShader = new SkyBoxShader(projectionMatrix);

        //add new vao to list
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        activeVAOs.add(vaoID);

        activeVBOs.add(ModelLoader.loadVertexVBO(VERTICES));
        activeVBOs.add(ModelLoader.loadCubeMapTextureVBO(VERTICES));

        //add data that is specific to that vao
        int[] indices = new int[VERTICES.length];
        for (int i = 0; i < VERTICES.length; i++)
        {
            indices[i] = i;
        }
        activeVBOs.add(ModelLoader.loadIndicesVBO(indices));
        nrOfIndices.add(indices.length);


        //texture binding
        texID = Texture.loadCubeMap(miramar);

        GL30.glBindVertexArray(0);
    }

    public void render(Camera camera, Vector3f fogColor)
    {
        skyboxShader.start();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(activeVAOs.get(0));
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

        skyboxShader.loadWorldToViewMatrix(camera.getWorldtoViewMatrix());
        skyboxShader.loadModelToWorldMatrix(getModelToWorldMatrix());
        skyboxShader.loadFogcolor(fogColor);

        glActiveTexture(GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(0), GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        skyboxShader.stop();
    }
    
    private static final float[] VERTICES =
    {
        -SIZE, SIZE, -SIZE,
        -SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, SIZE,
        -SIZE, SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, SIZE
    };

    public static final String[] grimmnight =
    {
        "objects/skybox/grimmnight_rt.tga",
        "objects/skybox/grimmnight_lf.tga",
        "objects/skybox/grimmnight_up.tga",
        "objects/skybox/grimmnight_dn.tga",
        "objects/skybox/grimmnight_bk.tga",
        "objects/skybox/grimmnight_ft.tga"
    };
    
    public static final String[] interstellar =
    {
        "objects/skybox/interstellar_rt.tga",
        "objects/skybox/interstellar_lf.tga",
        "objects/skybox/interstellar_up.tga",
        "objects/skybox/interstellar_dn.tga",
        "objects/skybox/interstellar_bk.tga",
        "objects/skybox/interstellar_ft.tga"
    };
    
    public static final String[] miramar =
    {
        "objects/skybox/miramar_rt.tga",
        "objects/skybox/miramar_lf.tga",
        "objects/skybox/miramar_up.tga",
        "objects/skybox/miramar_dn.tga",
        "objects/skybox/miramar_bk.tga",
        "objects/skybox/miramar_ft.tga"
    };
    
    public static final String[] stormydays =
    {
        "objects/skybox/stormydays_rt.tga",
        "objects/skybox/stormydays_lf.tga",
        "objects/skybox/stormydays_up.tga",
        "objects/skybox/stormydays_dn.tga",
        "objects/skybox/stormydays_bk.tga",
        "objects/skybox/stormydays_ft.tga"
    };
    
    public static final String[] violentdays =
    {
        "objects/skybox/violentdays_rt.tga",
        "objects/skybox/violentdays_lf.tga",
        "objects/skybox/violentdays_up.tga",
        "objects/skybox/violentdays_dn.tga",
        "objects/skybox/violentdays_bk.tga",
        "objects/skybox/violentdays_ft.tga"
    };
}
