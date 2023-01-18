package dev.tr7zw.waveycapes.math;

public interface CapePoint {

    float getLerpX(float delta);

    float getLerpY(float delta);

    float getLerpZ(float delta);

    default Vector3 getLerpedPos(float delta) {
        return new Vector3(getLerpX(delta), getLerpY(delta), getLerpZ(delta));
    }
    
}