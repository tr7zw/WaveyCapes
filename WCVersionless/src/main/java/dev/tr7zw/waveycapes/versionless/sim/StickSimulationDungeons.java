package dev.tr7zw.waveycapes.versionless.sim;

public class StickSimulationDungeons extends CapeSimulation {

    private static final float MAX_BEND = 20;

    public StickSimulationDungeons() {
        super(30);
    }

    @Override
    public void simulate() {
        applyGravity();
        preventClipping();
        applyMotion();
        for (int i = 1; i < x.length - 1; i++) {
            preventHardBend(i, i == x.length / 2 ? MAX_BEND : 0);
        }
        preventSelfClipping(10);
        limitLength();
    }

}
