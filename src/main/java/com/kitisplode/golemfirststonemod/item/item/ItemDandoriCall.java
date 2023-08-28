package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDandoriCall extends Item
{
    static private final double dandoriRange = 10;
    static private final int maxUseTime = 40;
    static private final int dandoriForceTime = 5;
    static private final int cooldownTime = 20;

    public ItemDandoriCall(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_1"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_2"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_3"));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return maxUseTime;
    }

    @Override
    @NotNull
    public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        if (!pLevel.isClientSide())
        {
            if (!pPlayer.isCrouching())
            {
                effectWhistle(pLevel, pPlayer, dandoriForceTime);
                dandoriWhistle(pLevel, pPlayer, false, true);
            }
            else
            {
                dandoriWhistle(pLevel, pPlayer, true, false);
            }
        }

        pPlayer.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.5f, 0.8f);
        pPlayer.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.5f, 0.9f);
        pPlayer.startUsingItem(pUsedHand);
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration)
    {
        if (pRemainingUseDuration % 10 == 0)
        {
            if (!pLevel.isClientSide())
            {
                int actualDandoriForceTime = maxUseTime - dandoriForceTime;
                if (pRemainingUseDuration < actualDandoriForceTime)
                {
                    if (!pLivingEntity.isCrouching())
                    {
                        dandoriWhistle(pLevel, pLivingEntity, true, true);
                    }
                    if (pRemainingUseDuration + 10 >= actualDandoriForceTime)
                    {
                        effectWhistle(pLevel, pLivingEntity, actualDandoriForceTime);
                    }
                }
            }
            else
            {
                spawnParticles(pLevel, pLivingEntity);
            }
            pLivingEntity.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.5f, 0.8f);
            pLivingEntity.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.5f, 0.95f);
        }
    }

    @Override
    @NotNull
    public ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity)
    {
        if (pLivingEntity instanceof Player)
            ((Player) pLivingEntity).getCooldowns().addCooldown(this, cooldownTime);
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving, int pTimeLeft)
    {
        if (pEntityLiving instanceof Player)
            ((Player) pEntityLiving).getCooldowns().addCooldown(this, cooldownTime);
    }

    private int dandoriWhistle(Level world, LivingEntity user, boolean forceDandori, boolean dandoriValue)
    {
        int targetCount = 0;
        List<Mob> targetList = world.getEntitiesOfClass(Mob.class, user.getBoundingBox().inflate(dandoriRange));
        for (Mob target : targetList)
        {
            // Skip the item user.
            if (target == user) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            // Skip things that already have dandori active?
            if (((IEntityDandoriFollower) target).getDandoriState() == dandoriValue) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            boolean targetHasOwner = ((IEntityDandoriFollower) target).getOwner() != null;
            if (targetHasOwner)
            {
                if (((IEntityDandoriFollower) target).getOwner() != user) continue;
            }
            // Skip iron golems that are not player-made
            if (target instanceof IronGolem)
            {
                if (!((IronGolem) target).isPlayerCreated() || ((IEntityDandoriFollower) target).getOwner() != user) continue;
            }
            targetCount++;
            // If the pik doesn't have a target, or if we're forcing dandori, activate the pik's dandori mode.
            if (target.getTarget() == null || forceDandori)
                ((IEntityDandoriFollower)target).setDandoriState(dandoriValue);
        }
        return targetCount;
    }

    private void spawnParticles(Level world, LivingEntity user)
    {
        Vec3 particlePos = new Vec3(0,0,1);
        particlePos = particlePos.yRot(user.getYRot() * -Mth.DEG_TO_RAD);
        particlePos = particlePos.add(user.getDeltaMovement());
        world.addParticle(ParticleTypes.NOTE,
                user.getX() + particlePos.x, user.getEyeY(), user.getZ() + particlePos.z,
                particlePos.x * 3, particlePos.y, particlePos.z * 3);
    }

    private void effectWhistle(Level world, LivingEntity user, int time)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.get().create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPos(user.position());
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale((float) dandoriRange * 2.0f);
            whistleEffect.setOwner(user);
            world.addFreshEntity(whistleEffect);
        }
    }
}
