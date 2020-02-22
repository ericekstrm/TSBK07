package light;

import util.Vector3f;

public class DirectionalLight {
    
    Vector3f direction;
    Vector3f color;

    public DirectionalLight(Vector3f direction, Vector3f color)
    {
        this.direction = direction;
        this.color = color;
    }

    public Vector3f getDirection()
    {
        return direction;
    }

    public Vector3f getColor()
    {
        return color;
    }
}
