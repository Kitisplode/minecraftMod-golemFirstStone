package com.kitisplode.golemfirststonemod.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ExtraMath
{
    public static double getYawBetweenPoints(Vec3 from, Vec3 to) {
        return -((float) Mth.atan2(to.x() - from.x(), to.z() - from.z()));
    }

    public static float changeAngle(float from, float to, float max) {
        float f = Mth.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }
        if (f < -max) {
            f = -max;
        }
        return from + f;
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
}
