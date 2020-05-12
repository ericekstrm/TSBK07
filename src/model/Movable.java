package model;

import util.Matrix3f;
import util.Matrix4f;
import util.Vector3f;

public class Movable
{

    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Matrix3f orientation = new Matrix3f();
    protected float scaleX = 1f, scaleY = 1f, scaleZ = 1f;

    public void setPosition(float x, float y, float z)
    {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f pos)
    {
        this.position.x = pos.x;
        this.position.y = pos.y;
        this.position.z = pos.z;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ)
    {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void setRotation(float rotX, float rotY, float rotZ)
    {
        orientation = Matrix3f.rotate(rotX, rotY, rotZ);
    }

    public void setRotation(Matrix3f rotationMatrix)
    {
        orientation = rotationMatrix;
    }

    /**
     * Gives the rotation around each axis in the range of 0-360 degrees.
     * @return 
     */
    public Vector3f getRotation()
    {
        float x = (float) Math.atan2(orientation.m21, orientation.m22);
        float y = (float) Math.atan2(-orientation.m20, Math.sqrt(orientation.m21 * orientation.m21 + orientation.m22 * orientation.m22));
        float z = (float) Math.atan2(orientation.m10, orientation.m00);

        x = (x / ((float) Math.PI * 2)) * 360;
        y = (y / ((float) Math.PI * 2)) * 360;
        z = (z / ((float) Math.PI * 2)) * 360;

        return new Vector3f(x, y, z);
    }

    public void translate(Vector3f translation)
    {
        position.x += translation.x;
        position.y += translation.y;
        position.z += translation.z;
    }

    public void rotate(float x, float y, float z)
    {
        orientation = orientation.multiply(Matrix3f.rotate(x, y, z));
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getScale()
    {
        return new Vector3f(scaleX, scaleY, scaleZ);
    }

    public Matrix4f getModelToWorldMatrix()
    {
        //Matrix4f rotate = Matrix4f.rotate(rotX, rotY, rotZ);
        Matrix4f scale = Matrix4f.scale(scaleX, scaleY, scaleZ);
        Matrix4f translate = Matrix4f.translate(position.x, position.y, position.z);

        return translate.multiply(orientation.toMatrix4f()).multiply(scale);
    }
}
