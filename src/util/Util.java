package util;

public class Util
{

    public static float rand(float low, float high)
    {
        double r = Math.random() * (high - low) + low;
        return (float) r;
    }

    public static float randu(float i)
    {
        double r = Math.random() * i * 2;
        return (float) r - i;
    }

    public static float[][] fft2D(float[][] inputData)
    {
        int height = inputData.length;
        int width = inputData[0].length;

        float[][] realOut = new float[height][height];
        float[][] imagOut = new float[width][height];
        float[][] amplitudeOut = new float[width][height];

        // Two outer loops iterate on output data.
        for (int yWave = 0; yWave < height; yWave++)
        {
            for (int xWave = 0; xWave < width; xWave++)
            {
                // Two inner loops iterate on input data.
                for (int ySpace = 0; ySpace < height; ySpace++)
                {
                    for (int xSpace = 0; xSpace < width; xSpace++)
                    {
                        // Compute real, imag, and ampltude.
                        realOut[yWave][xWave] += (inputData[ySpace][xSpace] * Math.cos(2 * Math.PI * ((1.0 * xWave * xSpace / width) + (1.0 * yWave * ySpace / height)))) / Math.sqrt(width * height);
                        imagOut[yWave][xWave] -= (inputData[ySpace][xSpace] * Math.sin(2 * Math.PI * ((1.0 * xWave * xSpace / width) + (1.0 * yWave * ySpace / height)))) / Math.sqrt(width * height);
                        amplitudeOut[yWave][xWave] = (float) Math.sqrt(realOut[yWave][xWave] * realOut[yWave][xWave] + imagOut[yWave][xWave] * imagOut[yWave][xWave]);
                    }
                }
            }
        }

        return amplitudeOut;
    }
}
