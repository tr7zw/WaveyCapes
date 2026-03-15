package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation.Vector2;
import dev.tr7zw.waveycapes.sim.StickSimulation3d;
import dev.tr7zw.waveycapes.sim.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public interface CapeHolder {
    public BasicSimulation getSimulation();

    public void setSimulation(BasicSimulation sim);

    public default void updateSimulation(EntityPlayer abstractClientPlayer, int partCount) {
        BasicSimulation simulation = getSimulation();
        if (simulation == null || incorrectSimulation(simulation)) {
            simulation = createSimulation();
            setSimulation(simulation);
        }
        if (simulation == null) {
            return;
        }
        simulation.init(partCount);
    }

    public default boolean incorrectSimulation(BasicSimulation sim) {
        CapeMovement style = WaveyCapesBase.config.capeMovement;
        if ((style == CapeMovement.BASIC_SIMULATION && sim.getClass() != StickSimulation.class)
                || (style == CapeMovement.BASIC_SIMULATION_3D && sim.getClass() != StickSimulation3d.class)) {
            return true;
        }
        return false;
    }

    public default BasicSimulation createSimulation() {
        CapeMovement style = WaveyCapesBase.config.capeMovement;
        if (style == CapeMovement.BASIC_SIMULATION) {
            return new StickSimulation();
        }
        if (style == CapeMovement.BASIC_SIMULATION_3D) {
            return new StickSimulation3d();
        }
        return null;
    }

    public default void simulate(EntityPlayer abstractClientPlayer) {
        BasicSimulation simulation = getSimulation();
        if (simulation == null || simulation.empty()) {
            return; // no cape, nothing to update
        }
        double d = abstractClientPlayer.chasingPosX
                - abstractClientPlayer.posX;
        double m = abstractClientPlayer.chasingPosZ
                - abstractClientPlayer.posZ;
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float heightMul = WaveyCapesBase.config.heightMultiplier;
        float straveMul = WaveyCapesBase.config.straveMultiplier;
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        double fallHack = MathHelper.clamp_double((abstractClientPlayer.prevPosY - abstractClientPlayer.posY) * 10, 0, 1);
        simulation.setGravity(WaveyCapesBase.config.gravity);

        Vector3 gravity = new Vector3(0, -1, 0);
        Vector2 strave = new Vector2((float) (abstractClientPlayer.posX - abstractClientPlayer.prevPosX),
                (float) (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ));
        strave.rotateDegrees(-abstractClientPlayer.rotationYaw);
        double changeX = (d * o + m * p) + fallHack
                + (abstractClientPlayer.isSneaking() && !simulation.isSneaking() ? 3 : 0);
        double changeY = ((abstractClientPlayer.posY - abstractClientPlayer.prevPosY) * heightMul)
                + (abstractClientPlayer.isSneaking() && !simulation.isSneaking() ? 1 : 0);
        double changeZ = -strave.x * straveMul;
        simulation.setSneaking(abstractClientPlayer.isSneaking());
        Vector3 change = new Vector3((float) changeX, (float) changeY, (float) changeZ);
        simulation.setGravityDirection(gravity);

        simulation.applyMovement(change);
        simulation.simulate();
    }

}
