package model;

import loader.RawData;
import shader.Shader;
import util.Vector3f;

public abstract class RigidBody extends ColorModel
{

    float mass = 1; //mass of the sphere

    Vector3f P = new Vector3f(); //linear momentum
    Vector3f L = new Vector3f(); //angular momentum

    //Computed quantities
    //In the real application these have to be calcualted based on the suroundings.
    Vector3f force = new Vector3f(0, -3.82f, 0); //gravity!
    Vector3f torque = new Vector3f(); //"rotational force"

    public RigidBody(RawData data)
    {
        super(data);
    }

    public abstract void update(float deltaTime);
    
    public abstract void move(float deltaTime);

    public abstract void collisionCallback(Vector3f point, Vector3f direction);
    
    public abstract float getCenterOfMassHeight();
}
