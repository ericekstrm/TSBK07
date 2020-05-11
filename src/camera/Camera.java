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

    //Projection Matrix
    public static final float nearPlane = 1f;
    public static final float farPlane = 1000.0f;
    public static final float rightPlane = 0.5f;
    public static final float leftPlane = -0.5f;
    public static final float topPlane = 0.5f;
    public static final float bottomPlane = -0.5f;
    private static Matrix4f projectionMatrix = Matrix4f.frustum_new(nearPlane, farPlane, rightPlane, leftPlane, topPlane, bottomPlane);
    public static Vector3f rightNormal;
    public static Vector3f leftNormal;
    public static Vector3f topNormal;
    public static Vector3f bottomNormal;

    public Camera(Vector3f position, Vector3f lookAt)
    {
        this.position = position;
        this.direction = lookAt.subtract(position).normalize();
        
        Vector3f bottomRightCorner = new Vector3f(rightPlane, bottomPlane, nearPlane).normalize();
        Vector3f bottomLeftCorner = new Vector3f(leftPlane, bottomPlane, nearPlane).normalize();
        Vector3f topRightCorner = new Vector3f(rightPlane, topPlane, nearPlane).normalize();
        Vector3f topLeftCorner = new Vector3f(leftPlane, topPlane, nearPlane).normalize();

        rightNormal = bottomRightCorner.cross(topRightCorner);
        leftNormal = topLeftCorner.cross(bottomLeftCorner);
        topNormal = topRightCorner.cross(topLeftCorner);
        bottomNormal = bottomLeftCorner.cross(bottomRightCorner);
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

    public Vector3f getDirection()
    {
        return direction;
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
