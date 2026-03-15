package dev.tr7zw.waveycapes.sim;

public class Vector3 {
    public float x, y, z;

    public Vector3() {
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    public void copy(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3 add(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3 subtract(Vector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public Vector3 div(float amount) {
        this.x /= amount;
        this.y /= amount;
        this.z /= amount;
        return this;
    }

    public Vector3 mul(float amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
        return this;
    }

    public Vector3 cross(Vector3 vec) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = vec.x;
        float j = vec.y;
        float k = vec.z;
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
        return this;
    }

    public Vector3 normalize() {
        float f = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (f < 1.0E-4F) {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        } else {
            this.x /= f;
            this.y /= f;
            this.z /= f;
        }
        return this;
    }

    public Vector3 rotateDegrees(float deg) {
        float ox = x;
        float oy = y;
        deg = (float) Math.toRadians(deg);
        x = (float) (Math.cos(deg) * ox - Math.sin(deg) * oy);
        y = (float) (Math.sin(deg) * ox + Math.cos(deg) * oy);
        return this;
    }

    public float sqrMagnitude() {
        return x * x + y * y + z * z;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

}
