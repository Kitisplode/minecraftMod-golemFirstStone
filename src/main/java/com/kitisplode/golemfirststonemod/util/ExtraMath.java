package com.kitisplode.golemfirststonemod.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
            if (a2 < a1 + 180) return Math.abs(a1 - a2);
            else return Math.abs(a1 + 360 - a2);
        }
        else
        {
            if (a2 > a1 - 180) return Math.abs(a1 - a2);
            else return Math.abs(a1 - 360 - a2);
        }
    }
}
