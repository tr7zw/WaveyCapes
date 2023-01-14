package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation.Point;
import dev.tr7zw.waveycapes.sim.StickSimulation.Stick;
import dev.tr7zw.waveycapes.sim.StickSimulation.Vector2;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public interface CapeHolder {
    public StickSimulation getSimulation();
    
    public default void updateSimulation(AbstractClientPlayer abstractClientPlayer, int partCount) {
        StickSimulation simulation = getSimulation();
        boolean dirty = false;
        if(simulation.points.size() != partCount) {
            simulation.points.clear();
            simulation.sticks.clear();
            for (int i = 0; i < partCount; i++) {
                Point point = new Point();
                point.position.y = -i;
                point.locked = i == 0;
                simulation.points.add(point);
                if(i > 0) {
                    simulation.sticks.add(new Stick(simulation.points.get(i-1), point, 1f));
                }
            }
            dirty = true;
        }
        if(dirty) {
            for(int i = 0; i < 10; i++) // quickly doing a few simulation steps to get the cape int a stable configuration
                simulate(abstractClientPlayer);
        }
    }
    
    public default void simulate(AbstractClientPlayer abstractClientPlayer) {
        StickSimulation simulation = getSimulation();
        if(simulation.points.isEmpty()) {
            return; // no cape, nothing to update
        }
        simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
        double d = abstractClientPlayer.xCloak
                - abstractClientPlayer.getX();
        double m = abstractClientPlayer.zCloak
                - abstractClientPlayer.getZ();
        float n = abstractClientPlayer.yBodyRotO + abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO;
        double o = Mth.sin(n * 0.017453292F);
        double p = -Mth.cos(n * 0.017453292F);
        float heightMul = WaveyCapesBase.config.heightMultiplier;
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        double fallHack = Mth.clamp((abstractClientPlayer.yo - abstractClientPlayer.getY())*10, 0, 1); 
        if(abstractClientPlayer.isUnderWater()) {
            simulation.gravity = WaveyCapesBase.config.gravity/10;
        }else {
            simulation.gravity = WaveyCapesBase.config.gravity;
        }
        
        simulation.gravityDirection.x = 0;
        simulation.gravityDirection.y = -1;
        double changeX = (d * o + m * p) + fallHack + (abstractClientPlayer.isCrouching() && !simulation.sneaking ? 3 : 0);
        double changeY = ((abstractClientPlayer.getY() - abstractClientPlayer.yo)*heightMul) + (abstractClientPlayer.isCrouching() && !simulation.sneaking ? 1 : 0);
        simulation.sneaking = abstractClientPlayer.isCrouching();
        Vector2 change = new Vector2((float)changeX, (float)changeY);
        if(abstractClientPlayer.isVisuallySwimming()) {
            float rotation = abstractClientPlayer.getXRot(); // -90 = swimming up, 0 = straight, 90 = down
            // the simulation has the body as reference, so if the player is swimming straight down, gravity needs to point up(the cape should move into the direction of the head, not the feet)
            // offset the rotation to swimming up doesn't rotate the vector at all
            rotation += 90;
            // apply rotation
            simulation.gravityDirection.rotateDegrees(rotation);
            
            change.rotateDegrees(rotation);
        }

        simulation.points.get(0).position.add(change);
        simulation.simulate();
    }
    
}
