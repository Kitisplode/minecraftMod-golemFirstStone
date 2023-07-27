package com.kitisplode.golemfirststonemod.util;

import net.minecraft.world.phys.Vec3;

public class ExtraMath
{
    public static double getYawBetweenPoints(Vec3 from, Vec3 to) {
        Vec3 newFrom = new Vec3(from.x(), 0, from.z());
        Vec3 newTo = new Vec3(to.x(), 0, to.z());
        return Math.acos(newFrom.dot(newTo) / newFrom.length() / newTo.length());
    }
}
