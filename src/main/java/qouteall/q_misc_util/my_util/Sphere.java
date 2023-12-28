package qouteall.q_misc_util.my_util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record Sphere(Vec3 center, double radius) {
    
    @Nullable
    public Vec3 projectToSphere(Vec3 pos) {
        Vec3 delta = pos.subtract(center());
        
        if (delta.lengthSqr() < 0.001) {
            // cannot constraint to the sphere
            return null;
        }
        
        return center().add(
            delta.normalize().scale(radius())
        );
    }
    
    @Nullable
    public Circle getIntersectionWithPlane(Plane plane) {
        double distance = plane.getDistanceTo(this.center());
        
        if (distance > this.radius()) {
            // there is no intersection
            return null;
        }
        
        Vec3 circleCenter = plane.getProjection(this.center());
        double newRadius = Math.sqrt(
            this.radius() * this.radius() - distance * distance
        );
        
        return new Circle(plane, circleCenter, newRadius);
    }
    
    @Nullable
    public Vec3 rayTrace(Vec3 lineOrigin, Vec3 lineVec) {
        // fully generated by GitHub Copilot
        
        Vec3 delta = lineOrigin.subtract(center());
        
        double a = lineVec.lengthSqr();
        double b = delta.dot(lineVec) * 2;
        double c = delta.lengthSqr() - radius() * radius();
        
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant < 0) {
            // no intersection
            return null;
        }
        
        double sqrtDiscriminant = Math.sqrt(discriminant);
        
        double t1 = (-b + sqrtDiscriminant) / (2 * a);
        double t2 = (-b - sqrtDiscriminant) / (2 * a);
        
        if (t1 < 0 && t2 < 0) {
            // no intersection
            return null;
        }
        
        if (t1 < 0) {
            return lineOrigin.add(lineVec.scale(t2));
        }
        else if (t2 < 0) {
            return lineOrigin.add(lineVec.scale(t1));
        }
        else {
            return lineOrigin.add(lineVec.scale(Math.min(t1, t2)));
        }
    }
    
    public static Sphere interpolate(Sphere a, Sphere b, double progress) {
        return new Sphere(
            a.center().lerp(b.center(), progress),
            Mth.lerp(progress, a.radius(), b.radius())
        );
    }
}