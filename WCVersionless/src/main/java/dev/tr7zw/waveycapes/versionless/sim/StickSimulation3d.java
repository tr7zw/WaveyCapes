package dev.tr7zw.waveycapes.versionless.sim;

public class StickSimulation3d extends CapeSimulation {

    private static final float MAX_BEND = 20;

    public StickSimulation3d() {
        super(3);
    }

    @Override
    public boolean init(int partCount) {
        if (!super.init(partCount)) {
            return false;
        }
        for (int i = 0; i < partCount; i++) {
            x[i] = -i;
        }
        return true;
    }

    @Override
    public void simulate() {
        applyGravity();
        preventClipping();
        preventSelfClipping(32);
        applyMotion();
        preventSelfClipping(32);
        for (int i = 1; i < x.length - 2; i++) {
            preventHardBend(i, MAX_BEND);
        }
        limitLength();
    }

}
