package camera;

import model.Model;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class ProjectionMatrix
{

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

    public ProjectionMatrix()
    {
        Vector3f bottomRightCorner = new Vector3f(rightPlane, bottomPlane, nearPlane).normalize();
        Vector3f bottomLeftCorner = new Vector3f(leftPlane, bottomPlane, nearPlane).normalize();
        Vector3f topRightCorner = new Vector3f(rightPlane, topPlane, nearPlane).normalize();
        Vector3f topLeftCorner = new Vector3f(leftPlane, topPlane, nearPlane).normalize();

        rightNormal = bottomRightCorner.cross(topRightCorner);
        leftNormal = topLeftCorner.cross(bottomLeftCorner);
        topNormal = topRightCorner.cross(topLeftCorner);
        bottomNormal = bottomLeftCorner.cross(bottomRightCorner);
    }

    public static Matrix4f get()
    {
        return projectionMatrix;
    }

    public static float getTopPlane()
    {
        return topPlane;
    }

    public static Vector3f getRightNormal()
    {
        return rightNormal;
    }

    public static Vector3f getLeftNormal()
    {
        return leftNormal;
    }

    public static Vector3f getTopNormal()
    {
        return topNormal;
    }

    public static boolean isModelInFrustum(Model m, Camera c)
    {
        //Convert to same coord system
        Vector3f pos = c.getWorldtoViewMatrix().multiply(m.getPosition().subtract(c.getPosition()));
        
        Vector3f topPos = pos.subtract(topNormal.scale(m.getMaxRadius()));
        if (topPos.dot(topNormal.scale(-1)) < 0)
        {
            return false;
        }
        Vector3f bottomPos = pos.subtract(bottomNormal.scale(m.getMaxRadius()));
        if (bottomPos.dot(bottomNormal.scale(-1)) < 0)
        {
            return false;
        }
        Vector3f leftPos = pos.subtract(leftNormal.scale(m.getMaxRadius()));
        if (leftPos.dot(leftNormal.scale(-1)) < 0)
        {
            return false;
        }
        Vector3f rightPos = pos.subtract(rightNormal.scale(m.getMaxRadius()));
        if (rightPos.dot(rightNormal.scale(-1)) < 0)
        {
            return false;
        }
        return true;
    }
}
