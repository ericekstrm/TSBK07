package util;

public class Util
{

    public static float rand(int low, int high)
    {
        double r = Math.random() * (high - low - 1);
        return (float) r ;
    }
    
    public static float randu(int i)
    {
        double r = Math.random() * i * 2;
        return (float) r - i;
    }
}
