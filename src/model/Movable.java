package model;

import util.Matrix4f;
import util.Vector3f;

public class Movable
{

    float x = 0, y = 0, z;
    float scaleX = 1f, scaleY = 1f, scaleZ = 1f;
    float rotX = 0, rotY = 0, rotZ = 0;

    public void setPosition(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ)
    {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public void setRotation(float rotX, float rotY, float rotZ)
    {
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public void move(Vector3f translation)
    {
        x += translation.x;
        y += translation.y;
        z += translation.z;
    }

    public void rotate(float x, float y, float z)
    {
        rotX += x;
        rotY += y;
        rotZ += z;
    }

    public Vector3f getPosition()
    {
        return new Vector3f(x, y, z);
    }

    public Vector3f getScale()
    {
        return new Vector3f(scaleX, scaleY, scaleZ);
    }

    public Vector3f getRotation()
    {
        return new Vector3f(rotX, rotY, rotZ);
    }

    public Matrix4f getModelToViewMatrix()
    {
        Matrix4f rotate = Matrix4f.rotate(rotX, rotY, rotZ);
        Matrix4f scale = Matrix4f.scale(scaleX, scaleY, scaleZ);
        Matrix4f translate = Matrix4f.translate(x, y, z);

        return translate.multiply(rotate).multiply(scale);
    }
}
