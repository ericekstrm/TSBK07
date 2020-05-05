package util;

import java.nio.FloatBuffer;
import static util.Matrix4f.rotate;

/**
 * This class represents a 4x4-Matrix. GLSL equivalent to mat4.
 *
 * @author Heiko Brumme
 */
public class Matrix3f
{

    private float m00, m01, m02;
    private float m10, m11, m12;
    private float m20, m21, m22;

    /**
     * Creates a 4x4 identity matrix.
     */
    public Matrix3f()
    {
        setIdentity();
    }

    public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }
    
    public Matrix3f(Matrix3f m)
    {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
    }

    /**
     * Creates a 4x4 matrix with specified columns.
     *
     * @param col1 Vector with values of the first column
     * @param col2 Vector with values of the second column
     * @param col3 Vector with values of the third column
     */
    public Matrix3f(Vector3f col1, Vector3f col2, Vector3f col3)
    {
        m00 = col1.x;
        m10 = col1.y;
        m20 = col1.z;

        m01 = col2.x;
        m11 = col2.y;
        m21 = col2.z;

        m02 = col3.x;
        m12 = col3.y;
        m22 = col3.z;
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    public final void setIdentity()
    {
        m00 = 1f;
        m11 = 1f;
        m22 = 1f;

        m01 = 0f;
        m02 = 0f;
        m10 = 0f;
        m12 = 0f;
        m20 = 0f;
        m21 = 0f;
    }

    /**
     * Adds this matrix to another matrix.
     *
     * @param other The other matrix
     *
     * @return Sum of this + other
     */
    public Matrix3f add(Matrix3f other)
    {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 + other.m00;
        result.m10 = this.m10 + other.m10;
        result.m20 = this.m20 + other.m20;

        result.m01 = this.m01 + other.m01;
        result.m11 = this.m11 + other.m11;
        result.m21 = this.m21 + other.m21;

        result.m02 = this.m02 + other.m02;
        result.m12 = this.m12 + other.m12;
        result.m22 = this.m22 + other.m22;

        return result;
    }

    /**
     * Negates this matrix.
     *
     * @return Negated matrix
     */
    public Matrix3f negate()
    {
        return multiply(-1f);
    }

    /**
     * Subtracts this matrix from another matrix.
     *
     * @param other The other matrix
     *
     * @return Difference of this - other
     */
    public Matrix3f subtract(Matrix3f other)
    {
        return this.add(other.negate());
    }

    /**
     * Multiplies this matrix with a scalar.
     *
     * @param scalar The scalar
     *
     * @return Scalar product of this * scalar
     */
    public Matrix3f multiply(float scalar)
    {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 * scalar;
        result.m10 = this.m10 * scalar;
        result.m20 = this.m20 * scalar;

        result.m01 = this.m01 * scalar;
        result.m11 = this.m11 * scalar;
        result.m21 = this.m21 * scalar;

        result.m02 = this.m02 * scalar;
        result.m12 = this.m12 * scalar;
        result.m22 = this.m22 * scalar;

        return result;
    }

    /**
     * Multiplies this matrix to a vector.
     *
     * @param vector The vector
     *
     * @return Vector product of this * other
     */
    public Vector3f multiply(Vector3f vector)
    {
        float x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z;
        float y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z;
        float z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z;
        return new Vector3f(x, y, z);
    }

    /**
     * Multiplies this matrix to another matrix.
     *
     * @param other The other matrix
     *
     * @return Matrix product of this * other
     */
    public Matrix3f multiply(Matrix3f other)
    {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20;
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20;
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20;

        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21;
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21;
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21;

        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22;
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22;
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22;

        return result;
    }

    /**
     * Transposes this matrix.
     *
     * @return Transposed matrix
     */
    public Matrix3f transpose()
    {
        Matrix3f result = new Matrix3f();

        result.m00 = this.m00;
        result.m10 = this.m01;
        result.m20 = this.m02;

        result.m01 = this.m10;
        result.m11 = this.m11;
        result.m21 = this.m12;

        result.m02 = this.m20;
        result.m12 = this.m21;
        result.m22 = this.m22;

        return result;
    }

    /**
     * Calculates the determinant of the matrix.
     *
     * @return determinant
     */
    public float det()
    {
        return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20);
    }

    public Matrix3f inverse()
    {
        Matrix3f minv = new Matrix3f();
        float det = det();
        minv.m00 = (m11 * m22 - m21 * m12) * det;
        minv.m01 = (m02 * m21 - m01 * m22) * det;
        minv.m02 = (m01 * m12 - m02 * m11) * det;
        minv.m10 = (m12 * m20 - m10 * m22) * det;
        minv.m11 = (m00 * m22 - m02 * m20) * det;
        minv.m12 = (m10 * m02 - m00 * m12) * det;
        minv.m20 = (m10 * m21 - m20 * m11) * det;
        minv.m21 = (m20 * m01 - m00 * m21) * det;
        minv.m22 = (m00 * m11 - m10 * m01) * det;
        return minv;
    }

    public static Matrix3f rotate(float angle, float x, float y, float z)
    {
        Matrix3f rotation = new Matrix3f();

        float c = (float) Math.cos(Math.toRadians(angle));
        float s = (float) Math.sin(Math.toRadians(angle));
        Vector3f vec = new Vector3f(x, y, z);
        if (vec.length() != 1f)
        {
            vec = vec.normalize();
            x = vec.x;
            y = vec.y;
            z = vec.z;
        }

        rotation.m00 = x * x * (1f - c) + c;
        rotation.m10 = y * x * (1f - c) + z * s;
        rotation.m20 = x * z * (1f - c) - y * s;
        rotation.m01 = x * y * (1f - c) - z * s;
        rotation.m11 = y * y * (1f - c) + c;
        rotation.m21 = y * z * (1f - c) + x * s;
        rotation.m02 = x * z * (1f - c) + y * s;
        rotation.m12 = y * z * (1f - c) - x * s;
        rotation.m22 = z * z * (1f - c) + c;

        return rotation;
    }

    public static Matrix3f rotate(float angle, Vector3f direction)
    {
        return rotate(angle, direction.x, direction.y, direction.z);
    }

    /**
     * Creates a rotation matrix that rotates the specified angles around the
     * three base axis.
     *
     * @param xAngle
     * @param yAngle
     * @param zAngle
     * @return
     */
    public static Matrix3f rotate(float xAngle, float yAngle, float zAngle)
    {
        Matrix3f rx = rotate(xAngle, 1, 0, 0);
        Matrix3f ry = rotate(yAngle, 0, 1, 0);
        Matrix3f rz = rotate(zAngle, 0, 0, 1);
        return rx.multiply(ry).multiply(rz);
    }

    /**
     * Stores the matrix in a given Buffer.
     *
     * @param buffer The buffer to store the matrix data
     */
    public void toBuffer(FloatBuffer buffer)
    {
        buffer.put(m00).put(m10).put(m20);
        buffer.put(m01).put(m11).put(m21);
        buffer.put(m02).put(m12).put(m22);
        buffer.flip();
    }

    /**
     * Transforms vector into matrix according to function. Trivial
     * calculations, look at the code to understand what it does.
     *
     * @param v the matrix to transform to v
     *
     * @return the v* matrix
     */
    public static Matrix3f star(Vector3f v)
    {
        return new Matrix3f(0, -v.z, v.y,
                            v.z, 0, -v.x,
                            -v.y, v.x, 0);
    }

    public Matrix4f toMatrix4f()
    {
        return new Matrix4f(m00, m01, m02, 0, m10, m11, m12, 0, m20, m21, m22, 0, 0, 0, 0, 1);
    }

    public static void print(Matrix3f m)
    {
        System.out.println("-----------------");
        System.out.println(m.m00 + " " + m.m01 + " " + m.m02);
        System.out.println(m.m10 + " " + m.m11 + " " + m.m12);
        System.out.println(m.m20 + " " + m.m21 + " " + m.m22);
    }

    /**
     * Approx method for making the matrix orthonormal.
     * 
     */
    public void orthnormalize()
    {
        Vector3f x = new Vector3f(m00, m10, m20);
        Vector3f y = new Vector3f(m01, m11, m21);
        float error = x.dot(y);
        
        Vector3f xOrt = x.subtract(y.scale(error/2));
        Vector3f yOrt = y.subtract(x.scale(error/2));
        Vector3f zOrt = xOrt.cross(yOrt);
        
        xOrt = xOrt.normalize();
        yOrt = yOrt.normalize();
        zOrt = zOrt.normalize();
        
        m00 = xOrt.x;
        m10 = xOrt.y;
        m20 = xOrt.z;
        m01 = yOrt.x;
        m11 = yOrt.y;
        m21 = yOrt.z;
        m02 = zOrt.x;
        m12 = zOrt.y;
        m22 = zOrt.z;
    }
}
