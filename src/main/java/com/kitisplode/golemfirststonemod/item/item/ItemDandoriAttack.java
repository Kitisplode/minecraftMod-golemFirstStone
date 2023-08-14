package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class ItemDandoriAttack extends Item
{
    static private final double dandoriRange = 10;
    static private final int maxUseTime = 72000;
    static private final int cooldownTime = 20;
    static private final int dandoriForceTime = 10;
    static private final double maxAttackRange = 48;
    static private final float attackRingDiameter = 6.0f;
    static private final EntityDimensions entityDimensions = new EntityDimensions(attackRingDiameter, attackRingDiameter, false);

    public ItemDandoriAttack(Settings settings)
    {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return maxUseTime;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        super.usageTick(world,user,stack,remainingUseTicks);
        // Highlight the block and/or enemy the player is looking at.
//        BlockHitResult ray = Item.raycast(world, (PlayerEntity) user, RaycastContext.FluidHandling.ANY);
//        Vec3d pos = ray.getPos();
////        BlockPos blockPos = ray.getBlockPos();
//        if (user.squaredDistanceTo(pos) <= maxAttackRange)
//        {
//
//        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        BlockHitResult ray = ItemDandoriAttack.raycast(world, (PlayerEntity) user, RaycastContext.FluidHandling.ANY, maxAttackRange);
        Vec3d pos = ray.getPos();
        if (user.squaredDistanceTo(pos) <= MathHelper.square(maxAttackRange))
        {
            int actualDandoriForceTime = maxUseTime - dandoriForceTime;
            boolean actualDandoriForce = remainingUseTicks < actualDandoriForceTime;
            int attackers = dandoriAttack(world, user, pos, true);
            if (attackers == 0) user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL, 1.0f, 0.9f);
            else user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN, 1.0f, 1.0f);
            effectRing(world, 20, attackRingDiameter, pos);

        }
        else
        {
            user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL, 1.0f, 0.9f);
        }
        setCooldown(cooldownTime, user);
    }

    private int dandoriAttack(World world, LivingEntity user, Vec3d position, boolean forceDandori)
    {
        // Find the target nearest the given position.
        TargetPredicate tp = TargetPredicate.createNonAttackable().setBaseMaxDistance(attackRingDiameter).setPredicate(entity -> entity instanceof Monster);
        LivingEntity enemy = world.getClosestEntity(LivingEntity.class, tp, null, position.getX(), position.getY(), position.getZ(), entityDimensions.getBoxAt(position));
        if (enemy == null)
        {
            return 0;
        }

        int targetCount = 0;
        List<MobEntity> targetList = world.getNonSpectatingEntities(MobEntity.class, user.getBoundingBox().expand(dandoriRange));
        for (MobEntity target : targetList)
        {
            // Skip the item user.
            if (target == user) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            // Skip piks that are not in dandori mode, unless we're forcing dandori.
            if (!((IEntityDandoriFollower) target).getDandoriState() && !forceDandori) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            boolean targetHasOwner = ((IEntityDandoriFollower) target).getOwner() != null;
            if (targetHasOwner)
            {
                if (!((IEntityDandoriFollower) target).isOwner(user)) continue;
            }
            // Skip iron golems that are not player-made
            if (target instanceof IronGolemEntity)
            {
                if (!((IronGolemEntity) target).isPlayerCreated()) continue;
                    // If the golem is player made but has no owner, just update their owner to us now /shrug
                else if (!targetHasOwner)
                {
                    ((IEntityDandoriFollower) target).setOwner(user);
                }
            }
            // Skip anything that can't target the enemy.
            if (!target.canTarget(enemy)) continue;
//            GolemFirstStoneMod.LOGGER.info("Attacking! " + target.getUuid().toString());
            targetCount++;
            // Make the pik target the enemy we got earlier.
            target.setTarget(enemy);
            ((IEntityDandoriFollower) target).setDandoriState(false);
        }
        return targetCount;
    }

    private void effectRing(World world, int time, float size, Vec3d position)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPosition(position);
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale(size);
            world.spawnEntity(whistleEffect);
        }
    }

    protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling, double range) {
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
        double d = 5.0;
        Vec3d vec3d2 = vec3d.add((double)l * range, (double)m * range, (double)n * range);
        return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }

    private void setCooldown(int pCooldown, LivingEntity user)
    {
        if (user instanceof PlayerEntity)
            ((PlayerEntity)user).getItemCooldownManager().set(this, pCooldown);
    }
}
