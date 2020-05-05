package light;

import camera.Camera;
import loader.Loader;
import loader.RawData;
import loader.Texture;
import model.ModelLoader;
import model.Model;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.ParticleShader;
import shader.Shader;
import util.Matrix4f;
import util.Vector3f;

public class Sun extends Model
{

    private float sunHeight = 200;

    int texID;

    ParticleShader shader;

    Vector3f rotationAxis;
    Vector3f color = new Vector3f(1, 0.8f, 0.8f);

    public Sun(Matrix4f projectionMatrix)
    {
        this.position = new Vector3f(0, sunHeight, sunHeight);
        RawData d = Loader.loadQuad(-sunHeight/10, -sunHeight/10, sunHeight/5, sunHeight/5);
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

        texID = Texture.load("objects/sun/flare-sun-lens.png");

        GL30.glBindVertexArray(0);

        setPosition(position);
        rotationAxis = position.normalize();
        rotationAxis.x = 0;
        rotationAxis.y *= -1;
        rotationAxis = rotationAxis.normalize();

        shader = new ParticleShader(projectionMatrix);
    }

    public void render(Camera camera)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        shader.start();

        shader.loadModelToViewMatrix(getModelToWorldFixed(camera.getWorldtoViewMatrix()),
                                     Matrix4f.remove_translation(camera.getWorldtoViewMatrix()));

        GL30.glBindVertexArray(activeVAOs.get(0));
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(0), GL11.GL_UNSIGNED_INT, 0);
        deactivate();
        shader.stop();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    DirectionalLight getDirLight()
    {
        return new DirectionalLight(position.normalize().scale(-1), color);
    }

    @Override
    public void update(float deltaTime)
    {
        //rotation around world
        //setPosition(Matrix4f.rotate(deltaTime, rotationAxis).multiply(position));

        //update light color based on time of day (height of sun)
        float sunFactor = position.y / sunHeight;
        if (sunFactor < 0)
        {
            color = new Vector3f();
        } else
        {
            float c = 1 - (1 / (float) Math.exp(10 * sunFactor));
            color = new Vector3f(c, c * 0.8f, c * 0.8f);
        }
    }

    /**
     * calculates the model to world matrix but without the rotation. so that
     * the result is a quad that is always pointing towards the camera.
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

    /**
     * Returns a Camera positioned at the sun. Used for shadow depth map
     * calculations.
     *
     * @return
     */
    public Camera getSunCamera()
    {
        return new Camera(position, new Vector3f(-20, 0, 0));
    }
}
