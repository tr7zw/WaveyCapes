package dev.tr7zw.waveycapes.sim;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;

/**
 * Java port of https://www.youtube.com/watch?v=PGk0rnyTa1U by  Sebastian Lague
 *
 */
public class StickSimulation {

    public List<Point> points = new ArrayList<>();
    public List<Stick> sticks = new ArrayList<>();
    public float gravity = -20f;
    public int numIterations = 64;
    private long lastUpdate = System.currentTimeMillis();
    
    public void simulate() {
        float deltaTime = (System.currentTimeMillis() - lastUpdate) / 1000f;
        lastUpdate = System.currentTimeMillis();
        Vector2 down = new Vector2(0, gravity * deltaTime);
        Vector2 tmp = new Vector2(0, 0);
        for(Point p : points) {
            if(!p.locked) {
                tmp.copy(p.position);
                //p.position.add(p.position).subtract(p.prevPosition);
                p.position.subtract(down);
                p.prevPosition.copy(tmp);
            }
        }
        
        Point basePoint = points.get(0);
        
        for(Point p : points) {
            if(p.position.x - basePoint.position.x > 0) {
                p.position.x = basePoint.position.x;
            }
        }
        
        for(int i = 0; i < numIterations; i++) {
            for(int x = sticks.size()-1; x >= 0; x--) {
                Stick stick = sticks.get(x);
                Vector2 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2);
                Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
                if(!stick.pointA.locked) {
                    stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length/2));
                }
                if(!stick.pointB.locked) {
                    stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length/2));
                }
            }
        }
    }
    
    public static class Point{
        public Vector2 position = new Vector2(0, 0);
        public Vector2 prevPosition = new Vector2(0, 0);
        public boolean locked;
    }
    
    public static class Stick{
        public Point pointA, pointB;
        public float length;
        
        public Stick(Point pointA, Point pointB, float length) {
            this.pointA = pointA;
            this.pointB = pointB;
            this.length = length;
        }
        
    }
    
    public static class Vector2{
        public float x,y;
        
        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public Vector2 clone() {
            return new Vector2(x, y);
        }
        
        public void copy(Vector2 vec) {
            this.x = vec.x;
            this.y = vec.y;
        }
        
        public Vector2 add(Vector2 vec) {
            this.x += vec.x;
            this.y += vec.y;
            return this;
        }
        
        public Vector2 subtract(Vector2 vec) {
            this.x -= vec.x;
            this.y -= vec.y;
            return this;
        }
        
        public Vector2 div(float amount) {
            this.x /= amount;
            this.y /= amount;
            return this;
        }
        
        public Vector2 mul(float amount) {
            this.x *= amount;
            this.y *= amount;
            return this;
        }
        
        public Vector2 normalize() {
            float f = Mth.sqrt(this.x * this.x + this.y * this.y);
            if(f < 1.0E-4F) {
                this.x = 0;
                this.y = 0;
            } else {
                this.x /= f;
                this.y /= f;
            }
            return this;
        }
        
    }

}
