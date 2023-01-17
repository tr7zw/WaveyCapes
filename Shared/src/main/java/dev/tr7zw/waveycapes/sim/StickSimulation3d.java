package dev.tr7zw.waveycapes.sim;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.math.CapePoint;
import dev.tr7zw.waveycapes.math.Vector3;
import net.minecraft.util.Mth;

/**
 * Java port of https://www.youtube.com/watch?v=PGk0rnyTa1U by Sebastian Lague
 * Has some changes like maximizing bends, only designed to simulate a single
 * "rope"(cape). Point 0 is the part fixed to the player
 *
 */
public class StickSimulation3d implements BasicSimulation {

    public List<Point> points = new ArrayList<>();
    public List<Stick> sticks = new ArrayList<>();
    public Vector3 gravityDirection = new Vector3(0, -1, 0);
    public float gravity = WaveyCapesBase.config.gravity;
    public int numIterations = 30;
    private float maxBend = 5;
    public boolean sneaking = false;

    @Override
    public boolean init(int partCount) {
        if(points.size() != partCount) {
            points.clear();
            sticks.clear();
            for (int i = 0; i < partCount; i++) {
                Point point = new Point();
                point.position.y = -i;
                point.locked = i == 0;
                points.add(point);
                if(i > 0) {
                    sticks.add(new Stick(points.get(i-1), point, 1f));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void simulate() {
        //maxBend = WaveyCapesBase.config.maxBend;

        applyGravity();

        preventClipping();

//        preventHardBends();

//        preventSelfClipping();
        applyMotion();
        preventSelfClipping();
        preventHardBends();
        limitLength();
    }

    private void applyGravity() {
        float deltaTime = 50f/1000f; // fixed timescale
        Vector3 down = gravityDirection.clone().mul(gravity * deltaTime);
        Vector3 tmp = new Vector3(0, 0, 0);
        for (Point p : points) {
            if (!p.locked) {
                tmp.copy(p.position);
                // p.position.add(p.position).subtract(p.prevPosition);
                p.position.add(down);
                p.prevPosition.copy(tmp);
            }
        }
    }

    private void applyMotion() {
        // move into correct direction
        for (int i = 0; i < numIterations; i++) {
            for (int x = sticks.size() - 1; x >= 0; x--) {
                Stick stick = sticks.get(x);
                Vector3 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2);
                Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
                if (!stick.pointA.locked) {
                    stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2));
                }
                if (!stick.pointB.locked) {
                    stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2));
                }
            }
        }
    }

    private void limitLength() {
        // fix in the position/length, this prevents it from acting like a spring/stretchy
        for (int x = 0; x < sticks.size(); x++) {
            Stick stick = sticks.get(x);
            Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
            if (!stick.pointB.locked) {
                stick.pointB.position = stick.pointA.position.clone().subtract(stickDir.mul(stick.length));
            }
        }
    }
    
    private void preventSelfClipping() {
        // check the cape parts against each other. Bad implementation
        for (int a = 0; a < points.size(); a++) {
            for(int b = a + 1; b < points.size(); b++) {
                Point pA = points.get(a);
                Point pB = points.get(b);
                Vector3 stickDir = pA.position.clone().subtract(pB.position);
                if(stickDir.sqrMagnitude() < 1.1) {
                    stickDir.normalize();
                    Vector3 centre = pA.position.clone().add(pB.position).div(2);
                    if (!pA.locked) {
                        pA.position = centre.clone().add(stickDir.clone().mul(1f / 2f));
                    }
                    if (!pB.locked) {
                        pB.position = centre.clone().subtract(stickDir.clone().mul(1f / 2f));
                    }
                }
            }
        }
    }

    private void preventHardBends() {
        // Doesnt work like it should at all, but it prevents some folding into itself, so it stays for now
        for (int i = points.size() - 2; i >= 1; i--) {
            double angle = getAngle(points.get(i).position, points.get(i - 1).position, points.get(i + 1).position);
            angle *= 57.2958;
            if (angle > 360) {
                angle -= 360;
            }
            if (angle < -360) {
                angle += 360;
            }
            double abs = Math.abs(angle);
            if (abs < 180 - maxBend) {
                Vector3 replacement = getReplacement(points.get(i).position, points.get(i - 1).position, angle,
                        180 - maxBend + 1);
                points.get(i + 1).position = replacement;
            }
            if (abs > 180 + maxBend) {
                Vector3 replacement = getReplacement(points.get(i).position, points.get(i - 1).position, angle,
                        180 + maxBend - 1);
                points.get(i + 1).position = replacement;
            }
        }
    }

    private void preventClipping() {
        // prevent the cape from clipping into the player
        Point basePoint = points.get(0);
        for (Point p : points) {
            if (p != basePoint && p.position.x - basePoint.position.x > 0) {
                p.position.x = basePoint.position.x;
            }
        }
    }

    private Vector3 getReplacement(Vector3 middle, Vector3 prev, double angle, double target) {
        double theta = target / 57.2958;
        float x = prev.x - middle.x;
        float y = prev.y - middle.y;
        float z = middle.z;
        if (angle < 0) {
            theta *= -1;
        }
        double cs = Math.cos(theta);
        double sn = Math.sin(theta);
        return new Vector3((float) ((x * cs) - (y * sn) + middle.x), (float) ((x * sn) + (y * cs) + middle.y), z);
    }

    private double getAngle(Vector3 middle, Vector3 prev, Vector3 next) {
        return Math.atan2(next.y - middle.y, next.x - middle.x) - Math.atan2(prev.y - middle.y, prev.x - middle.x);
    }
    
    @Override
    public void setGravityDirection(Vector3 gravityDirection) {
        this.gravityDirection = gravityDirection;
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }
    
    @Override
    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }
    
    @Override
    public boolean empty() {
        return sticks.isEmpty();
    }
    
    @Override
    public void applyMovement(Vector3 movement) {
        points.get(0).prevPosition.copy(points.get(0).position);
        points.get(0).position.add(movement);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<CapePoint> getPoints() {
        return (List<CapePoint>)(Object)points;
    }

    public static class Point implements CapePoint {
        public Vector3 position = new Vector3(0, 0, 0);
        public Vector3 prevPosition = new Vector3(0, 0, 0);
        public boolean locked;
        
        @Override
        public float getLerpX(float delta) {
            return Mth.lerp(delta, prevPosition.x, position.x);
        }
        
        @Override
        public float getLerpY(float delta) {
            return Mth.lerp(delta, prevPosition.y, position.y);
        }
        
        @Override
        public float getLerpZ(float delta) {
            return Mth.lerp(delta, prevPosition.z, position.z);
        }
    }

    public static class Stick {
        public Point pointA, pointB;
        public float length;

        public Stick(Point pointA, Point pointB, float length) {
            this.pointA = pointA;
            this.pointB = pointB;
            this.length = length;
        }

    }

}