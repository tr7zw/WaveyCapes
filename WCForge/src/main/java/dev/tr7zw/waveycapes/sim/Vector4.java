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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector4 vector4 = (Vector4) o;
        return Float.compare(vector4.x, x) == 0 && Float.compare(vector4.y, y) == 0
                && Float.compare(vector4.z, z) == 0 && Float.compare(vector4.w, w) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        result = 31 * result + Float.floatToIntBits(w);
        return result;
    }

    @Override
    public String toString() {
        return "Vector4[x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
    }
}
