package util;

public class Util
{

    public static float rand(float low, float high)
    {
        double r = Math.random() * (high - low) + low;
        return (float) r ;
    }
    
    public static float randu(float i)
    {
        double r = Math.random() * i * 2;
        return (float) r - i;
    }
    
    
    public static void main(String[] args)
    {
        for (int i = 0; i < 100; i++)
        {
            System.out.println(rand(1,5));
        }
    }
}
