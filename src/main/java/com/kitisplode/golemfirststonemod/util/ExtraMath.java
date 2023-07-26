package com.kitisplode.golemfirststonemod.util;

import net.minecraft.util.math.Vec3d;

public class ExtraMath
{
    public static double getYawBetweenPoints(Vec3d from, Vec3d to) {
        Vec3d newFrom = new Vec3d(from.getX(), 0, from.getZ());
        Vec3d newTo = new Vec3d(to.getX(), 0, to.getZ());
        return Math.acos(newFrom.dotProduct(newTo) / newFrom.length() / newTo.length());
    }
}
