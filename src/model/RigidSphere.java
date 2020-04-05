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

    public RigidSphere(RawData data)
    {
        super(data);
    }

    @Override
    public void update(float deltaTime)
    {
        //update state variables
        //we add and move and then check if we should have moved. needs to be done the other way around. 
        //Or maybe the collision just adds a force in the opposite direction
        P = P.add(force.scale(deltaTime));
        L = L.add(torque.scale(deltaTime));
    }

    @Override
    public void collisionCallback(Vector3f point, Vector3f direction)
    {
    	//all of the calculations here assume that the collision was with the terrain.
    	
    	Matrix3f inertiaTensor = orientation.multiply(Ibody).multiply(orientation.transpose());
        Vector3f angularVelocity = inertiaTensor.multiply(L);
        Matrix3f Iinv = inertiaTensor.inverse();
    	
    	Vector3f vPointA = point.divide(mass).add(angularVelocity.cross(point));
    	Vector3f vPointB = new Vector3f(); //the other point is in the stationary terrain
    	
    	//j calculated according to page 139 in 'so how do we make them scream'.
    	float epsilon = 0;
    	float vMinusRel = vPointA.subtract(vPointB).dot(direction);
    	float j = -(epsilon + 1) * vMinusRel / (1/mass + direction.dot(Iinv.multiply(point.cross(direction).cross(point))));
    	Vector3f impulse = direction.scale(j);
    	
    	//P = P.add(impulse);
        //L = L.add(point.cross(impulse));
        
    	//alternative
    	//reflects the momentum in the plane that the sphere collided with.
        Vector3f reflectionP = P.subtract(direction.scale(2 * P.dot(direction)));
        P = reflectionP;
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

	@Override
	public float getCenterOfMassHeight() {
		return r;
	}
}
