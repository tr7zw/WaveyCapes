package dev.tr7zw.waveycapes.versionless.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.tr7zw.waveycapes.versionless.util.CapePoint;
import dev.tr7zw.waveycapes.versionless.util.Mth;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import lombok.Getter;
import lombok.Setter;

/**
 * Java port of https://www.youtube.com/watch?v=PGk0rnyTa1U by Sebastian Lague
 * Has some changes like maximizing bends, only designed to simulate a single
 * "rope"(cape). Point 0 is the part fixed to the player
 *
 */
public abstract class CapeSimulation implements BasicSimulation {

    protected float[] x = new float[0];
    protected float[] y = new float[0];
    protected float[] z = new float[0];
    private float[] prevX = new float[0];
    private float[] prevY = new float[0];
    private float[] prevZ = new float[0];
    private List<CapePoint> points = Collections.emptyList();

    private final int numIterations;
    protected float gravityX = 0;
    protected float gravityY = -1;
    protected float gravityZ = 0;
    @Getter
    @Setter
    private float gravity = 0;
    @Getter
    @Setter
    private boolean sneaking = false;

    private float dirX, dirY, dirZ;

    protected CapeSimulation(int numIterations) {
        this.numIterations = numIterations;
    }

    @Override
    public boolean init(int partCount) {
        if (x.length == partCount) {
            return false;
        }
        x = new float[partCount];
        y = new float[partCount];
        z = new float[partCount];
        prevX = new float[partCount];
        prevY = new float[partCount];
        prevZ = new float[partCount];
        List<CapePoint> views = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            y[i] = -i;
            views.add(new Point(i));
        }
        points = views;
        return true;
    }

    @Override
    public boolean empty() {
        return x.length < 2;
    }

    @Override
    public List<CapePoint> getPoints() {
        return points;
    }

    @Override
    public void setGravityDirection(Vector3 gravityDirection) {
        gravityX = gravityDirection.x;
        gravityY = gravityDirection.y;
        gravityZ = gravityDirection.z;
    }

    @Override
    public void applyMovement(Vector3 movement) {
        move(movement.x, movement.y, movement.z);
    }

    protected void move(float dx, float dy, float dz) {
        prevX[0] = x[0];
        prevY[0] = y[0];
        prevZ[0] = z[0];
        x[0] += dx;
        y[0] += dy;
        z[0] += dz;
    }

    protected void applyGravity() {
        float fall = gravity * (50f / 1000f); // fixed timescale
        for (int i = 1; i < x.length; i++) {
            prevX[i] = x[i];
            prevY[i] = y[i];
            prevZ[i] = z[i];
            x[i] += gravityX * fall;
            y[i] += gravityY * fall;
            z[i] += gravityZ * fall;
        }
    }

    protected void preventClipping() {
        // prevent the cape from clipping into the player
        for (int i = 1; i < x.length; i++) {
            if (x[i] - x[0] > 0) {
                x[i] = x[0];
            }
            float maxZ = ((float) i / (float) x.length) * ((float) i / (float) x.length) * 5;
            float offset = z[0] - z[i];
            if (offset > maxZ) {
                z[i] = z[0] - maxZ;
            }
            if (offset < -maxZ) {
                z[i] = z[0] + maxZ;
            }
        }
    }

    protected void applyMotion() {
        // move into correct direction
        for (int i = 0; i < numIterations; i++) {
            for (int a = x.length - 2; a >= 0; a--) {
                direction(a, a + 1);
                separate(a, a + 1);
            }
        }
    }

    protected void limitLength() {
        // fix in the position/length, this prevents it from acting like a
        // spring/stretchy
        for (int a = 0; a < x.length - 1; a++) {
            direction(a, a + 1);
            x[a + 1] = x[a] - dirX;
            y[a + 1] = y[a] - dirY;
            z[a + 1] = z[a] - dirZ;
        }
    }

    protected void preventSelfClipping(int maxRuns) {
        boolean clipped;
        int runs = 0;
        do {
            clipped = false;
            // check the cape parts against each other. Bad implementation
            for (int a = 0; a < x.length; a++) {
                for (int b = a + 1; b < x.length; b++) {
                    float dx = x[a] - x[b];
                    float dy = y[a] - y[b];
                    float dz = z[a] - z[b];
                    if (dx * dx + dy * dy + dz * dz < 0.99) {
                        clipped = true;
                        runs++;
                        direction(a, b);
                        separate(a, b);
                    }
                }
            }
        } while (clipped && runs < maxRuns);
    }

    // Doesnt work like it should at all, but it prevents some folding into itself,
    // so it stays for now
    protected void preventHardBend(int i, float maxBend) {
        float abx = x[i - 1] - x[i];
        float aby = y[i - 1] - y[i];
        float cbx = x[i - 1] - x[i + 1];
        float cby = y[i - 1] - y[i + 1];

        float dot = (abx * cbx + aby * cby); // dot product
        float cross = (abx * cby - aby * cbx); // cross product

        double angle = Mth.atan2(cross, dot) * 180 / Math.PI;
        if (angle < -maxBend) {
            swing(i, -maxBend * 2);
        }
        if (angle > maxBend) {
            swing(i, maxBend * 2);
        }
    }

    private void swing(int i, float deg) {
        float ox = x[i] - x[i - 1];
        float oy = y[i] - y[i - 1];
        float oz = z[i] - z[i - 1];
        deg = (float) Math.toRadians(deg);
        x[i + 1] = Mth.cos(deg) * ox - Mth.sin(deg) * oy + x[i];
        y[i + 1] = Mth.sin(deg) * ox + Mth.cos(deg) * oy + y[i];
        z[i + 1] = oz + z[i];
    }

    private void separate(int a, int b) {
        float centreX = (x[a] + x[b]) / 2;
        float centreY = (y[a] + y[b]) / 2;
        float centreZ = (z[a] + z[b]) / 2;
        if (a != 0) {
            x[a] = centreX + dirX / 2;
            y[a] = centreY + dirY / 2;
            z[a] = centreZ + dirZ / 2;
        }
        x[b] = centreX - dirX / 2;
        y[b] = centreY - dirY / 2;
        z[b] = centreZ - dirZ / 2;
    }

    private void direction(int a, int b) {
        dirX = x[a] - x[b];
        dirY = y[a] - y[b];
        dirZ = z[a] - z[b];
        float f = Mth.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (f < 1.0E-4F) {
            dirX = 0;
            dirY = 0;
            dirZ = 0;
        } else {
            dirX /= f;
            dirY /= f;
            dirZ /= f;
        }
    }

    private final class Point implements CapePoint {
        private final int index;

        private Point(int index) {
            this.index = index;
        }

        @Override
        public float getLerpX(float delta) {
            return Mth.lerp(delta, prevX[index], x[index]);
        }

        @Override
        public float getLerpY(float delta) {
            return Mth.lerp(delta, prevY[index], y[index]);
        }

        @Override
        public float getLerpZ(float delta) {
            return Mth.lerp(delta, prevZ[index], z[index]);
        }
    }

}
