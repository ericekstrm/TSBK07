package particle_system;

import camera.Camera;
import loader.Loader;
import loader.RawData;
import loader.Texture;
import model.Model;
import model.ModelLoader;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import shader.ParticleShader;
import shader.Shader;
import util.Matrix4f;
import util.Vector3f;

public class Particle extends Model
{

    int texID;

    ParticleShader shader;

    public Particle(Matrix4f projectionMatrix, Vector3f position)
    {
        this.position = new Vector3f(position);
        float size = (float) Math.random() + 1;
        RawData d = Loader.loadQuad(-1f / 10, -1f / 10, 1f / 5, 1f / 5);
        //add new vao to list
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        activeVAOs.add(vaoID);

        //add data that is the same for all vaos (this is where there is a lot of memory waste.)
        activeVBOs.add(ModelLoader.loadVertexVBO(d.vertices));
        activeVBOs.add(ModelLoader.loadTextureVBO(d.textureCoords));

        //add data that is specific to that vao
        activeVBOs.add(ModelLoader.loadIndicesVBO(d.indices));
        nrOfIndices.add(d.indices.length);

        texID = Texture.load("objects/sun/smoke_particle.png");

        shader = new ParticleShader(projectionMatrix);

        GL30.glBindVertexArray(0);
    }

    public void render(Camera camera)
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        shader.start();

        GL30.glBindVertexArray(activeVAOs.get(0));
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);

        //draw!
        shader.loadModelToViewMatrix(getModelToWorldFixed(camera.getWorldtoViewMatrix()),
                                     camera.getWorldtoViewMatrix());
        GL31.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(0), GL11.GL_UNSIGNED_INT, 0);

        deactivate();
        shader.stop();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void update(float deltaTime)
    {
    }

    /**
     * calculates the model to world matrix but without the rotation. The result
     * is a quad that is always pointing towards the camera.
     *
     * @param worldToView
     * @return
     */
    private Matrix4f getModelToWorldFixed(Matrix4f worldToView)
    {
        Matrix4f translate = Matrix4f.translate(position.x, position.y, position.z);
        translate.m00 = worldToView.m00;
        translate.m01 = worldToView.m10;
        translate.m02 = worldToView.m20;
        translate.m10 = worldToView.m01;
        translate.m11 = worldToView.m11;
        translate.m12 = worldToView.m21;
        translate.m20 = worldToView.m02;
        translate.m21 = worldToView.m12;
        translate.m22 = worldToView.m22;
        Matrix4f scale = Matrix4f.scale(scaleX, scaleY, scaleZ);

        return translate.multiply(orientation.toMatrix4f()).multiply(scale);
    }
}
