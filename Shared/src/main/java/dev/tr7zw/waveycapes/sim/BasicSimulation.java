package dev.tr7zw.waveycapes.sim;

import java.util.List;

import dev.tr7zw.waveycapes.math.CapePoint;
import dev.tr7zw.waveycapes.math.Vector3;

public interface BasicSimulation {

    void simulate();

    void setGravityDirection(Vector3 gravityDirection);

    float getGravity();

    void setGravity(float gravity);

    boolean isSneaking();

    void setSneaking(boolean sneaking);
    
    /**
     * @return true if it was re-initialized
     */
    boolean init(int partCount);
    
    boolean empty();
    
    void applyMovement(Vector3 movement);
    
    List<CapePoint> getPoints();

}