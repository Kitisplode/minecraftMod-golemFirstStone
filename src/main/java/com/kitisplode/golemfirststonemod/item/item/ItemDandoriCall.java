package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDandoriCall extends Item implements IItemSwingUse
{
    static private final double dandoriRange = 10;
    static private final int maxUseTime = 40;
    static private final int dandoriForceTime = 5;
    static private final int cooldownTime = 20;
    static private final double maxAttackRange = 48;

    static private final int fullDeployTime = 15;
    private int fullDeployTimer = 0;

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
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_4"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_5"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_6"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_call_7"));
    }

    @Override
    public void swing(Player user)
    {
        Level world = user.level();
        //if (!world.isClientSide())
        {
            if (user.isCrouching())
            {
                if (user instanceof IEntityWithDandoriCount player && !world.isClientSide()) player.nextDandoriCurrentType();
            }
            else
            {
                DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
                ClipContext.Fluid fh = ClipContext.Fluid.ANY;
                if (user.isUnderWater()) fh = ClipContext.Fluid.NONE;
                BlockHitResult ray = ExtraMath.playerRaycast(world, user, fh, maxAttackRange);
                if (user.distanceToSqr(ray.getLocation()) <= Mth.square(maxAttackRange))
                {
                    int count = dandoriDeploy(world, user, ray.getBlockPos(), false, currentType, 1);
                    if (count == 0) user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
                    else
                    {
                        user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN.get(), 1.0f, 1.0f);
                        effectDeploy(world, 20, 6, ray.getLocation());
                    }
                    user.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
        if (user.isCrouching()) fullDeployTimer = fullDeployTime;
        else fullDeployTimer = 0;
    }
    @Override
    public void swingTick(Player user)
    {
        Level world = user.level();
        if (fullDeployTimer < fullDeployTime)
        {
            fullDeployTimer++;
            if (fullDeployTimer >= fullDeployTime)
            {
                DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
                ClipContext.Fluid fh = ClipContext.Fluid.ANY;
                if (user.isUnderWater()) fh = ClipContext.Fluid.NONE;
                BlockHitResult ray = ExtraMath.playerRaycast(world, user, fh, maxAttackRange);
                if (user.distanceToSqr(ray.getLocation()) <= Mth.square(maxAttackRange))
                {
                    int count = dandoriDeploy(world, user, ray.getBlockPos(), false, currentType, 1000);
                    if (count == 0) user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
                    else
                    {
                        user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN.get(), 1.0f, 1.0f);
                        effectDeploy(world, 20, 6, ray.getLocation());
                    }
                    user.swing(InteractionHand.MAIN_HAND);
                    user.getCooldowns().addCooldown(this, cooldownTime);
                }
            }
        }
        else if (fullDeployTimer == fullDeployTime)
        {
            fullDeployTimer++;
            user.swing(InteractionHand.MAIN_HAND);
            user.getCooldowns().addCooldown(this, cooldownTime);
        }
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
                int dandoriCount = dandoriWhistle(pLevel, pPlayer, false, IEntityDandoriFollower.DANDORI_STATES.HARD);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) pPlayer).setRecountDandori();
            }
            else
            {
                int dandoriCount = dandoriWhistle(pLevel, pPlayer, true, IEntityDandoriFollower.DANDORI_STATES.OFF);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) pPlayer).setRecountDandori();
            }
        }

        pPlayer.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.4f, 0.8f);
        pPlayer.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.4f, 0.9f);
        pPlayer.startUsingItem(pUsedHand);
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.swing(InteractionHand.MAIN_HAND);
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
                        dandoriWhistle(pLevel, pLivingEntity, true, IEntityDandoriFollower.DANDORI_STATES.HARD);
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
            pLivingEntity.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.4f, 0.8f);
            pLivingEntity.playSound(ModSounds.ITEM_DANDORI_CALL.get(), 0.4f, 0.95f);
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

    private int dandoriWhistle(Level world, LivingEntity user, boolean forceDandori, IEntityDandoriFollower.DANDORI_STATES dandoriValue)
    {
        int targetCount = 0;
        List<Mob> targetList = world.getEntitiesOfClass(Mob.class, user.getBoundingBox().inflate(dandoriRange));
        for (Mob target : targetList)
        {
            // Skip the item user.
            if (target == user) continue;
            // Skip anything the user is currently riding.
            if (target.hasPassenger(user)) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            IEntityDandoriFollower dandoriTarget = (IEntityDandoriFollower) target;
            // Skip things that already have dandori active?
            if (dandoriTarget.getDandoriState() == dandoriValue.ordinal()) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            if (dandoriTarget.getOwner() != user) continue;
            targetCount++;
            // If the pik doesn't have a target, or if we're forcing dandori, activate the pik's dandori mode.
            if (target.getTarget() == null || forceDandori)
                ((IEntityDandoriFollower)target).setDandoriState(dandoriValue.ordinal());
        }
        return targetCount;
    }

    private int dandoriDeploy(Level world, LivingEntity user, BlockPos position, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType, int count)
    {
        if (position == null) return 0;

        int targetCount = 0;
        List<Mob> targetList = world.getEntitiesOfClass(Mob.class, user.getBoundingBox().inflate(dandoriRange * 2));
        for (Mob target : targetList)
        {
            if (targetCount >= count && count > 0) break;
            // Skip the item user.
            if (target == user) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            // Skip piks that are not in dandori mode, unless we're forcing dandori.
            if (((IEntityDandoriFollower) target).isDandoriOff() && !forceDandori) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            if (((IEntityDandoriFollower) target).getOwner() != user) continue;
            // SKip anything that isn't of the player's currently selected type.
            if (!DataDandoriCount.entityIsOfType(currentType, target)) continue;

            targetCount++;
            // Deploy the pik to the given location.
            if (!world.isClientSide())
            {
                ((IEntityDandoriFollower) target).setDeployPosition(position);
                ((IEntityDandoriFollower) target).setDandoriState(IEntityDandoriFollower.DANDORI_STATES.OFF.ordinal());
            }
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

    private void effectDeploy(Level world, int time, float size, Vec3 position)
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
}
