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

    // source: blog.harryzhou.info/index.php.2021/04/03/minecraft-projectile-launch-angle-calculation/
    // tx: distance
    // ty: height
    // v: speed?
    // d
    public static Double getNeededAngle(double tx, double ty, double v, double d, double g)
    {
        // Return vertical angle when near the asymptotes.
        if (tx < ty * 0.001)
        {
            return ty>0 ? Math.PI/2.0d : -Math.PI/2.0d;
        }

        double md = 1.0d - d;
        double log_md = Math.log(md);
        double g_d = g / d;
        double theta = Math.atan2(ty, tx);
        double prev_abs_ydif = Double.POSITIVE_INFINITY;

        // 20 iterations max, although it usually converges in 3
        for (int i = 0; i < 20; i++)
        {
            double cost = Math.cos(theta);
            double sint = Math.sin(theta);
            double tant = sint/cost;
            double vx = v * cost;
            double vy = v * sint;
            double y = tx * (g_d + vy) / vx - g_d * Math.log(1 - d * tx / vx) / log_md;
            double ydif = y-ty;
            double abs_ydif = Math.abs(ydif);

            // If it's getting farther away, there's probably no solution.
            if (abs_ydif > prev_abs_ydif)
            {
                return null;
            }
            else if (abs_ydif < 0.0001)
            {
                return theta;
            }

            double dy_dtheta = tx + g * tx * tant / (( -d * tx + v * cost) * log_md) + g * tx * tant / (d * v * cost) + tx * tant * tant;
            theta -= ydif/dy_dtheta;
            prev_abs_ydif = abs_ydif;
        }
        // If exceeded max iterations, return null.
        return null;
    }
}
