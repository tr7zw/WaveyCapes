package dev.tr7zw.waveycapes.versionless.sim;

import dev.tr7zw.waveycapes.versionless.util.Vector3;

public class StickSimulation extends CapeSimulation {

    private static final float MAX_BEND = 5;

    public StickSimulation() {
        super(3);
    }

    @Override
    public void setGravityDirection(Vector3 gravityDirection) {
        gravityX = gravityDirection.x;
        gravityY = gravityDirection.y;
    }

    @Override
    public void applyMovement(Vector3 movement) {
        move(movement.x, movement.y, 0);
    }

    @Override
    public void simulate() {
        applyGravity();
        preventClipping();
        preventHardBends();
        applyMotion();
        limitLength();
    }

    // Doesnt work like it should at all, but it prevents some folding into itself,
    // so it stays for now
    private void preventHardBends() {
        for (int i = x.length - 2; i >= 1; i--) {
            double angle = Math.atan2(y[i + 1] - y[i], x[i + 1] - x[i]) - Math.atan2(y[i - 1] - y[i], x[i - 1] - x[i]);
            angle *= 57.2958;
            if (angle > 360) {
                angle -= 360;
            }
            if (angle < -360) {
                angle += 360;
            }
            double abs = Math.abs(angle);
            if (abs < 180 - MAX_BEND) {
                replace(i, angle, 180 - MAX_BEND + 1);
            }
            if (abs > 180 + MAX_BEND) {
                replace(i, angle, 180 + MAX_BEND - 1);
            }
        }
    }

    private void replace(int i, double angle, double target) {
        double theta = target / 57.2958;
        float px = x[i - 1] - x[i];
        float py = y[i - 1] - y[i];
        if (angle < 0) {
            theta *= -1;
        }
        double cs = Math.cos(theta);
        double sn = Math.sin(theta);
        x[i + 1] = (float) ((px * cs) - (py * sn) + x[i]);
        y[i + 1] = (float) ((px * sn) + (py * cs) + y[i]);
    }

}
