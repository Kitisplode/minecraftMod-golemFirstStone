package com.kitisplode.golemfirststonemod.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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

    public static BlockHitResult playerRaycast(Level world, LivingEntity player, ClipContext.Fluid fluidHandling, double range)
    {
        float f = player.getXRot();
        float g = player.getYRot();
        Vec3 vec3d = player.getEyePosition();
        float h = Mth.cos(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float i = Mth.sin(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float j = -Mth.cos(-f * ((float) Math.PI / 180));
        float k = Mth.sin(-f * ((float) Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        Vec3 vec3d2 = vec3d.add((double) l * range, (double) m * range, (double) n * range);
        return world.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, fluidHandling, player));
    }
}
