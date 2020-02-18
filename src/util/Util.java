package util;

public class Util
{

    public static float rand(int low, int high)
    {
        double r = Math.random() * (high - low);
        return (float) r - (high - low) / 2;
    }
    
    public static float randu(int i)
    {
        double r = Math.random() * i * 2;
        return (float) r - i;
    }
}
