package model;

import camera.Camera;
import java.nio.FloatBuffer;
import loader.Loader;
import loader.RawData;
import loader.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;
import shader.TextureModelShader;
import util.Matrix4f;

public class Skybox extends TextureModel
{

    public static final int SIZE = 2;

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

    public static final String[] textureFiles =
    {
        "objects/skybox/violentdays_rt.tga",
        "objects/skybox/violentdays_lf.tga",
        "objects/skybox/violentdays_up.tga",
        "objects/skybox/violentdays_dn.tga",
        "objects/skybox/violentdays_bk.tga",
        "objects/skybox/violentdays_ft.tga"
    };

    public static final String[] textureFiles2 =
    {
        "objects/skybox/grimmnight_rt.tga",
        "objects/skybox/grimmnight_lf.tga",
        "objects/skybox/grimmnight_up.tga",
        "objects/skybox/grimmnight_dn.tga",
        "objects/skybox/grimmnight_bk.tga",
        "objects/skybox/grimmnight_ft.tga"
    };

    public static final String[] textureFiles3 =
    {
        "objects/skybox/thinmatrix/right.png",
        "objects/skybox/thinmatrix/left.png",
        "objects/skybox/thinmatrix/top.png",
        "objects/skybox/thinmatrix/bottom.png",
        "objects/skybox/thinmatrix/back.png",
        "objects/skybox/thinmatrix/front.png"
    };

    ModelShader skyboxShader;
    int texID;
    
    public Skybox()
    {
        RawData data = Loader.loadRawData("skybox/skybox.obj", "skybox/SkyBox512.tga");
        this.skyboxShader = new TextureModelShader("skybox.vert", "skybox.frag"); //change to its own shader.

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
        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrOfIndices.add(data.indices.length);

        //materials
        matProperties.add(data.material);

        //texture binding
        texID = Texture.loadCubeMap(textureFiles3);

        GL30.glBindVertexArray(0);

    }

    public void render(Camera camera)
    {
        skyboxShader.start();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(activeVAOs.get(0));
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

        //world-to-view matrix
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        Matrix4f mat = camera.getWorldtoViewMatrix();
        Matrix4f.remove_translation(mat);
        mat.toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(skyboxShader.getProgramID(), "worldToView"), false, worldToView);

        skyboxShader.loadModelToWorldMatrix(getModelToViewMatrix());

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
}
