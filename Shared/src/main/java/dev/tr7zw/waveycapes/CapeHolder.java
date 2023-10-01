package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation.Vector2;
import dev.tr7zw.waveycapes.sim.StickSimulation3d;
import dev.tr7zw.waveycapes.sim.StickSimulationDungeons;
import dev.tr7zw.waveycapes.util.Vector3;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public interface CapeHolder {
    public BasicSimulation getSimulation();
    
    public void setSimulation(BasicSimulation sim);
    
    public default void updateSimulation(AbstractClientPlayer abstractClientPlayer, int partCount) {
        BasicSimulation simulation = getSimulation();
        if(simulation == null || incorrectSimulation(simulation)) {
            simulation = createSimulation();
            setSimulation(simulation);
        }
        if(simulation == null) {
            return;
        }
        boolean dirty = simulation.init(partCount);
        if(dirty) {
            simulation.applyMovement(new Vector3(1f, 1f, 0));
            for(int i = 0; i < 5; i++) { // quickly doing a few simulation steps to get the cape int a stable configuration
                simulate(abstractClientPlayer);
            }
        }
    }
    
    public default boolean incorrectSimulation(BasicSimulation sim) {
        CapeMovement style = WaveyCapesBase.config.capeMovement;
        if(style == CapeMovement.BASIC_SIMULATION && sim.getClass() != StickSimulation.class) {
            return true;
        } else if(style == CapeMovement.BASIC_SIMULATION_3D && sim.getClass() != StickSimulation3d.class) {
            return true;
        } else if(style == CapeMovement.DUNGEONS && sim.getClass() != StickSimulationDungeons.class) {
            return true;
        }
        return false;
    }
    
    public default BasicSimulation createSimulation() {
        CapeMovement style = WaveyCapesBase.config.capeMovement;
        if(style == CapeMovement.BASIC_SIMULATION) {
            return new StickSimulation();
        }
        if(style == CapeMovement.BASIC_SIMULATION_3D) {
            return new StickSimulation3d();
        }
        if(style == CapeMovement.DUNGEONS) {
            return new StickSimulationDungeons();
        }
        return null;
    }
    
    public default void simulate(AbstractClientPlayer abstractClientPlayer) {
        BasicSimulation simulation = getSimulation();
        if(simulation == null || simulation.empty()) {
            return; // no cape, nothing to update
        }
        double d = abstractClientPlayer.xCloak
                - abstractClientPlayer.getX();
        double m = abstractClientPlayer.zCloak
                - abstractClientPlayer.getZ();
        float n = abstractClientPlayer.yBodyRotO + abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO;
        double o = Mth.sin(n * 0.017453292F);
        double p = -Mth.cos(n * 0.017453292F);
        float heightMul = WaveyCapesBase.config.heightMultiplier;
        float straveMul = WaveyCapesBase.config.straveMultiplier;
        if(abstractClientPlayer.isUnderWater()) {
            heightMul *= 2; // let the cape have more drag than the player underwater
        }
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        double fallHack = Mth.clamp((abstractClientPlayer.yo - abstractClientPlayer.getY())*10, 0, 1); 
        if(abstractClientPlayer.isUnderWater()) {
            simulation.setGravity(WaveyCapesBase.config.gravity/10f);
        }else {
            simulation.setGravity(WaveyCapesBase.config.gravity);
        }
        
        Vector3 gravity = new Vector3(0, -1, 0);
        Vector2 strave = new Vector2((float)(abstractClientPlayer.getX() - abstractClientPlayer.xo), (float)(abstractClientPlayer.getZ() - abstractClientPlayer.zo));
        strave.rotateDegrees(-abstractClientPlayer.getYRot());
        double changeX = (d * o + m * p) + fallHack + (abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 3 : 0);
        double changeY = ((abstractClientPlayer.getY() - abstractClientPlayer.yo)*heightMul) + (abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 1 : 0);
        double changeZ =  -strave.x * straveMul;
        simulation.setSneaking(abstractClientPlayer.isCrouching());
        Vector3 change = new Vector3((float)changeX, (float)changeY, (float)changeZ);
        if(abstractClientPlayer.isVisuallySwimming()) {
            float rotation = abstractClientPlayer.getXRot(); // -90 = swimming up, 0 = straight, 90 = down
            // the simulation has the body as reference, so if the player is swimming straight down, gravity needs to point up(the cape should move into the direction of the head, not the feet)
            // offset the rotation to swimming up doesn't rotate the vector at all
            rotation += 90;
            // apply rotation
            gravity.rotateDegrees(rotation);
            
            change.rotateDegrees(rotation);
        }
        simulation.setGravityDirection(gravity);
        
        simulation.applyMovement(change);
        simulation.simulate();
    }
    
}
