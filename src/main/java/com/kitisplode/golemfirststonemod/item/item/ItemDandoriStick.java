package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ItemDandoriStick extends Item
{
    static private final double dandoriRange = 10;
    static private final int maxUseTime = 40;
    static private final int dandoriForceTime = 20;
    static private final int cooldownTime = 100;

    public ItemDandoriStick(Settings settings)
    {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return maxUseTime;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        effectWhistle(world, user, dandoriForceTime);

        user.playSound(SoundEvents.BLOCK_BELL_USE, 1.0f, 1.0f);
        user.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
        int targetCount = dandoriWhistle(world, user, false);

        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.success(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
//        if (!world.isClient)
        {
            if (remainingUseTicks % 10 == 0)
            {
                int targetCount = 0;
                int actualDandoriForceTime = maxUseTime - dandoriForceTime;
                if (remainingUseTicks < actualDandoriForceTime)
                {
                    if (remainingUseTicks + 10 >= actualDandoriForceTime)
                    {
                        user.playSound(SoundEvents.BLOCK_BELL_USE, 1.0f, 3.0f);
                        user.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0f, 3.0f);
                        effectWhistle(world, user, actualDandoriForceTime);
                    }
                    targetCount = dandoriWhistle(world, user, true);
                }
            }
        }
    }

//    @Override
//    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
//    {
//        if (user instanceof PlayerEntity) {
//            ((PlayerEntity)user).getItemCooldownManager().set(this, cooldownTime);
//        }
//        return stack;
//    }

    private int dandoriWhistle(World world, LivingEntity user, boolean forceDandori)
    {
        int targetCount = 0;
        List<MobEntity> targetList = world.getNonSpectatingEntities(MobEntity.class, user.getBoundingBox().expand(dandoriRange));
        for (MobEntity target : targetList)
        {
            // Skip the item user.
            if (target == user) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            // Skip things that already have dandori active?
            if (((IEntityDandoriFollower) target).getDandoriState()) continue;

            targetCount++;
            // If the pik doesn't have a target, activate the pik's dandori mode.
            if (target.getTarget() == null || forceDandori)
                ((IEntityDandoriFollower)target).setDandoriState(true);
        }
        return targetCount;
    }

    private void effectWhistle(World world, LivingEntity user, int time)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPosition(user.getPos());
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale((float) dandoriRange * 2.0f);
            whistleEffect.setOwner(user);
            world.spawnEntity(whistleEffect);
        }
    }

}
