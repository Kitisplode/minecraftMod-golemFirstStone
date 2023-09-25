package com.kitisplode.golemfirststonemod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ExtraMath
{
    // Returns the 2d angle between two vectors in radians.
    public static double getYawBetweenPoints(Vec3d from, Vec3d to) {
        return -((float)MathHelper.atan2(to.getX() - from.getX(), to.getZ() - from.getZ()));
    }

    public static double getTrajectorySameHeight(double distance, double speed, double gravity)
    {
        double inside = (gravity * distance) / MathHelper.square(speed);
        double asin = Math.asin(inside);
        if (Double.isNaN(asin)) return Double.NaN;
        return 0.5 * asin;
    }

    public static double getTrajectoryDifferentHeight(double distanceSquared, double height, double speed, double gravity)
    {
        double phase = Math.atan(MathHelper.sqrt((float)distanceSquared) / height);
        double top = ((gravity * distanceSquared) / MathHelper.square(speed)) - height;
        double bottom = MathHelper.sqrt((float) (MathHelper.square(height) + distanceSquared));
        double otherthing = Math.acos(top / bottom);
        if (Double.isNaN(otherthing)) return Double.NaN;

        double angle = (otherthing + phase) / 2;
        return angle;
    }

    public static double getAngleDiff(double a1, double a2)
    {
        if (a1 < 180)
        {
            if (a2 < a1 + 180) return a2 - a1;
            else return a2 - a1 - 360;
        }
        else
        {
            if (a2 > a1 - 180) return a2 - a1;
            else return a2 - a1 + 360;
        }
    }

    public static float changeAngle(float from, float to, float max) {
        float f = MathHelper.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }
        if (f < -max) {
            f = -max;
        }
        return from + f;
    }

    public static BlockHitResult playerRaycast(World world, LivingEntity player, RaycastContext.FluidHandling fluidHandling, double range) {
        float f = player.getPitch();
        float g = player.getYaw();
        Vec3d vec3d = player.getEyePos();
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        Vec3d vec3d2 = vec3d.add((double)l * range, (double)m * range, (double)n * range);
        return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }
}
