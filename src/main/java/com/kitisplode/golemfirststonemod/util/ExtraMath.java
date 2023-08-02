package com.kitisplode.golemfirststonemod.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ExtraMath
{
    public static double getYawBetweenPoints(Vec3 from, Vec3 to) {
        return -((float) Mth.atan2(to.x() - from.x(), to.z() - from.z()));
    }
}
