package model;

import loader.RawData;
import shader.Shader;
import util.Matrix3f;
import util.Vector3f;

public class RigidSphere extends RigidBody
{

    float r = 0.5f; //radius of the sphere
    float I = 2f / 5f * mass * r * r;//moment of inertia (how easy it is to rotate the body)
    Matrix3f Ibody = new Matrix3f().multiply(I);

    public RigidSphere(Shader shader, RawData data)
    {
        super(shader, data);
    }

    @Override
    public void update(float deltaTime)
    {
        //update state variables
        //we add and move and then check if we should have moved. needs to be done the other way around. 
        //Or maybe the collision just adds a force in the oposite direction
        P = P.add(force.scale(deltaTime));
        L = L.add(torque.scale(deltaTime));

    }

    @Override
    public void collisionCallback(Vector3f point, Vector3f direction)
    {
        P.y *= -0.9f;
    }

    @Override
    public void move(float deltaTime)
    {
        //calculate the derived quantities
        Vector3f velocity = P.divide(mass);
        Matrix3f inertiaTensor = orientation.multiply(Ibody).multiply(orientation.transpose());
        Vector3f angularVelocity = inertiaTensor.multiply(L);

        //position += velocity * dt + position
        position = position.add(velocity.scale(deltaTime));

        Matrix3f orientationDt = orientation.multiply(Matrix3f.star(angularVelocity)).multiply(deltaTime);
        orientation = orientation.add(orientationDt);
        orientation.orthnormalize();
    }
}
