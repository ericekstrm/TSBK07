package camera;

import model.Model;
import model.ModelHandler;
import util.Collision;
import util.Matrix4f;
import util.Vector3f;

public class Camera
{

    protected Vector3f position;
    protected Vector3f direction;
    protected Vector3f upVector = new Vector3f(0, 1, 0);

    private float radius = 1f;

    public Camera(Vector3f position, Vector3f lookAt)
    {
        this.position = position;
        this.direction = lookAt.subtract(position).normalize();
    }

    public void setLookAt(Vector3f lookAt)
    {
        this.direction = lookAt.subtract(position).normalize();
    }

    /**
     * Calculates the World-to-View matrix according to the steps on page 53 in
     * "Polygons feel no Pain".
     *
     * @return
     */
    public Matrix4f getWorldtoViewMatrix()
    {
        Vector3f n = direction.scale(-1);
        n = n.normalize();
        Vector3f u = upVector.cross(n);
        u = u.normalize();
        Vector3f v = n.cross(u);

        Matrix4f rotation = new Matrix4f(
                u.x, u.y, u.z, 0,
                v.x, v.y, v.z, 0,
                n.x, n.y, n.z, 0,
                0, 0, 0, 1);
        Matrix4f translation = Matrix4f.translate(-position.x, -position.y, -position.z);

        return rotation.multiply(translation);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public boolean hasCollided(ModelHandler models)
    {
        for (Model m : models.getModels())
        {
            if (Math.abs(position.subtract(m.getPosition()).length()) < m.getMaxRadius() + radius)
            {
                if (Collision.check(this, m))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
