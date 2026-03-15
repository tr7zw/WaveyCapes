package dev.tr7zw.waveycapes.sim;

public class Vector4 {
    public float x, y, z, w;

    public Vector4() {
    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4 clone() {
        return new Vector4(x, y, z, w);
    }

    public Vector3 toVec3() {
        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + ", " + w + "]";
    }
}
