package util;

public class Quat
{

    private float w;
    private Vector3f n;

    public Quat(float w, Vector3f n)
    {
        this.w = w;
        this.n = n;
    }

    /**
     * create unit quaternion with rotation <i>angle</i> radians around <i>axis</i>.
     *
     * @param axis
     * @param angle
     */
    public Quat(Vector3f axis, float angle)
    {
        this.w = (float) Math.cos(angle / 2);
        this.n = axis.scale((float) Math.sin(angle / 2));
    }

    public Quat()
    {
        this.w = 1;
        this.n = new Vector3f(0,0,0);
    }

    public float norm()
    {
        return (float) Math.sqrt(w * w + n.x * n.x + n.y * n.y + n.z * n.z);
    }
    
    public void normalize()
    {
        w /= norm();
        n.divide(norm());
    }

    public Quat mult(Quat q)
    {
        return new Quat(w * q.w * -1 * n.dot(q.n), q.n.scale(w).add(n.scale(q.w)).add(n.cross(q.n)));
    }
    
    public Quat add(Quat q)
    {
        return new Quat(w+q.w, n.add(q.n));
    }

    public Quat conj()
    {
        return new Quat(w, n.scale(-1));
    }

    public Quat inv()
    {
        float d = norm() * norm();
        return new Quat(w / d, n.divide(d));
    }

    public Vector3f rotate(Vector3f v)
    {
        Quat rotated = this.mult(new Quat(0, v)).mult(this);
        return rotated.n;
    }
    
    public Quat scale(float scale)
    {
        return new Quat(w * scale, n.scale(scale));
    }
    
    //converts the quaternion into a 4x4 rotation matrix;
    public Matrix4f toRotationMatrix4f()
    {
        return new Matrix4f(1.0f - 2.0f * (n.y * n.y + n.z * n.z), 2.0f * (n.x * n.y - w * n.z),          2.0f * (n.x * n.z + w * n.y),          0.0f,
                            2.0f * (n.x * n.y + w * n.z),          1.0f - 2.0f * (n.x * n.x + n.z * n.z), 2.0f * (n.y * n.z - w * n.x),          0.0f,
                            2.0f * (n.x * n.z - w * n.y),          2.0f * (n.y * n.z + w * n.x),          1.0f - 2.0f * (n.x * n.x + n.y * n.y), 0.0f,
                            0.0f,                                  0.0f,                                  0.0f,                                  1.0f);
    }
    
    //converts the quaternion into a 3x3 rotation matrix;
    public Matrix3f toRotationMatrix3f()
    {
        return new Matrix3f(1.0f - 2.0f * (n.y * n.y + n.z * n.z), 2.0f * (n.x * n.y - w * n.z),          2.0f * (n.x * n.z + w * n.y),
                            2.0f * (n.x * n.y + w * n.z),          1.0f - 2.0f * (n.x * n.x + n.z * n.z), 2.0f * (n.y * n.z - w * n.x),
                            2.0f * (n.x * n.z - w * n.y),          2.0f * (n.y * n.z + w * n.x),          1.0f - 2.0f * (n.x * n.x + n.y * n.y));
    }
}
