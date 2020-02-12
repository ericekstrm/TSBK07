package model;

import util.Matrix4f;

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

    public Matrix4f getModelToViewMatrix()
    {
        Matrix4f rotate = Matrix4f.rotate(rotX, rotY, rotZ);
        Matrix4f scale = Matrix4f.scale(scaleX, scaleY, scaleZ);
        Matrix4f translate = Matrix4f.translate(x, y, z);

        return translate.multiply(rotate).multiply(scale);
    }
}
