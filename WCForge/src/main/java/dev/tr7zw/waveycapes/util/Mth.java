package dev.tr7zw.waveycapes.util;

public class Mth {

    public static float lerp(float f, float g, float h) {
        return g + f * (h - g);
    }

    public static double lerp(double d, double e, double f) {
        return e + d * (f - e);
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

    public static float sin(float f) {
        return (float) Math.sin(f);
    }

    public static float cos(float f) {
        return (float) Math.cos(f);
    }

    public static float clamp(float f, float g, float h) {
        if (f < g)
            return g;
        if (f > h)
            return h;
        return f;
    }

    public static double clamp(double d, double e, double f) {
        if (d < e)
            return e;
        if (d > f)
            return f;
        return d;
    }

    public static double atan2(double d, double e) {
        return Math.atan2(d, e);
    }

    public static float fastInvSqrt(float f) {
        float g = 0.5F * f;
        int i = Float.floatToIntBits(f);
        i = 1597463007 - (i >> 1);
        f = Float.intBitsToFloat(i);
        f *= 1.5F - g * f * f;
        return f;
    }

    public static float fastInvCubeRoot(float f) {
        int i = Float.floatToIntBits(f);
        i = 1419967116 - i / 3;
        float g = Float.intBitsToFloat(i);
        g = 0.6666667F * g + 1.0F / 3.0F * g * g * f;
        g = 0.6666667F * g + 1.0F / 3.0F * g * g * f;
        return g;
    }

}
