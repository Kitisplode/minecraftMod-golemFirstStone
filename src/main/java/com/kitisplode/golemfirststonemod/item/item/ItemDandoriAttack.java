package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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

    public ItemDandoriAttack(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return maxUseTime;
    }

    @Override
    @NotNull
    public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        pPlayer.startUsingItem(pUsedHand);
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving, int pTimeLeft)
    {
        BlockHitResult ray = ItemDandoriAttack.raycast(pLevel, (Player) pEntityLiving, ClipContext.Fluid.ANY, maxAttackRange);
        Vec3 pos = ray.getLocation();
        if (pEntityLiving.distanceToSqr(pos) <= Mth.square(maxAttackRange))
        {
            int actualDandoriForceTime = maxUseTime - dandoriForceTime;
            boolean actualDandoriForce = pTimeLeft < actualDandoriForceTime;
            int attackers = dandoriAttack(pLevel, pEntityLiving, pos, true);
            if (attackers == 0) pEntityLiving.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
            else pEntityLiving.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN.get(), 1.0f, 1.0f);
            effectRing(pLevel, 20, attackRingDiameter, pos);

        }
        else
        {
            pEntityLiving.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
        }
        setCooldown(cooldownTime, pEntityLiving);
    }

    private int dandoriAttack(Level world, LivingEntity user, Vec3 position, boolean forceDandori)
    {
        // Find the target nearest the given position.
        TargetingConditions tp = TargetingConditions.forCombat().range(attackRingDiameter).selector(entity -> entity instanceof Enemy);
        LivingEntity enemy = world.getNearestEntity(LivingEntity.class, tp, null, position.x(), position.y(), position.z(), entityDimensions.makeBoundingBox(position));
        if (enemy == null)
        {
            return 0;
        }

        int targetCount = 0;
        List<Mob> targetList = world.getEntitiesOfClass(Mob.class, user.getBoundingBox().inflate(dandoriRange));
        for (Mob target : targetList)
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
            if (target instanceof IronGolem)
            {
                if (!((IronGolem) target).isPlayerCreated()) continue;
                    // If the golem is player made but has no owner, just update their owner to us now /shrug
                else if (!targetHasOwner)
                {
                    ((IEntityDandoriFollower) target).setOwner(user);
                }
            }
            // Skip anything that can't target the enemy.
            if (!target.canAttack(enemy)) continue;
//            GolemFirstStoneMod.LOGGER.info("Attacking! " + target.getUuid().toString());
            targetCount++;
            // Make the pik target the enemy we got earlier.
            target.setTarget(enemy);
            ((IEntityDandoriFollower) target).setDandoriState(false);
        }
        return targetCount;
    }

    private void effectRing(Level world, int time, float size, Vec3 position)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.get().create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPos(position);
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale(size);
            world.addFreshEntity(whistleEffect);
        }
    }

    protected static BlockHitResult raycast(Level world, Player player, ClipContext.Fluid fluidHandling, double range) {
        float f = player.getXRot();
        float g = player.getYRot();
        Vec3 vec3d = player.getEyePosition();
        float h = Mth.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = Mth.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -Mth.cos(-f * ((float)Math.PI / 180));
        float k = Mth.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        double d = 5.0;
        Vec3 vec3d2 = vec3d.add((double)l * range, (double)m * range, (double)n * range);
        return world.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, fluidHandling, player));
    }

    private void setCooldown(int pCooldown, LivingEntity user)
    {
        if (user instanceof Player)
            ((Player)user).getCooldowns().addCooldown(this, pCooldown);
    }
}
