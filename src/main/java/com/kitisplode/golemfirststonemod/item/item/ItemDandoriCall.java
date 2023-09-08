package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

    public ItemDandoriCall(Settings settings)
    {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_1"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_2"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_3"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_4"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_5"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_6"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_call_7"));
    }

    @Override
    public void swing(PlayerEntity user)
    {
        World world = user.getWorld();
        //if (!world.isClient())
        {
            if (user.isSneaking())
            {
                if (user instanceof IEntityWithDandoriCount player) player.nextDandoriCurrentType();
            }
            else
            {
                DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
                RaycastContext.FluidHandling fh = RaycastContext.FluidHandling.ANY;
                if (user.isSubmergedInWater()) fh = RaycastContext.FluidHandling.NONE;
                BlockHitResult ray = ExtraMath.playerRaycast(world, user, fh, maxAttackRange);
                if (user.squaredDistanceTo(ray.getPos()) <= MathHelper.square(maxAttackRange))
                {
                    int count = dandoriDeploy(world, user, ray.getBlockPos(), false, currentType, 1);
                    if (count == 0) user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL, 1.0f, 0.9f);
                    else
                    {
                        user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN, 1.0f, 1.0f);
                        effectDeploy(world, 20, 6, ray.getPos());
                    }
                }
            }
        }
        if (user.isSneaking()) fullDeployTimer = fullDeployTime;
        else fullDeployTimer = 0;
    }
    @Override
    public void swingTick(PlayerEntity user)
    {
        World world = user.getWorld();
        if (fullDeployTimer < fullDeployTime)
        {
            fullDeployTimer++;
            if (fullDeployTimer >= fullDeployTime)
            {
                DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
                RaycastContext.FluidHandling fh = RaycastContext.FluidHandling.ANY;
                if (user.isSubmergedInWater()) fh = RaycastContext.FluidHandling.NONE;
                BlockHitResult ray = ExtraMath.playerRaycast(world, user, fh, maxAttackRange);
                if (user.squaredDistanceTo(ray.getPos()) <= MathHelper.square(maxAttackRange))
                {
                    int count = dandoriDeploy(world, user, ray.getBlockPos(), false, currentType, 1000);
                    if (count == 0) user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL, 1.0f, 0.9f);
                    else
                    {
                        user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN, 1.0f, 1.0f);
                        effectDeploy(world, 20, 6, ray.getPos());
                    }
                    user.swingHand(Hand.MAIN_HAND);
                    user.getItemCooldownManager().set(this, cooldownTime);
                }
            }
        }
        else if (fullDeployTimer == fullDeployTime)
        {
            fullDeployTimer++;
            user.swingHand(Hand.MAIN_HAND);
            user.getItemCooldownManager().set(this, cooldownTime);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return maxUseTime;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (!world.isClient())
        {
            if (!user.isSneaking())
            {
                int dandoriCount = dandoriWhistle(world, user, false, true);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) user).setRecountDandori();
            }
            else
            {
                int dandoriCount = dandoriWhistle(world, user, true, false);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) user).setRecountDandori();
            }
            effectWhistle(world, user, dandoriForceTime);
        }

        user.playSound(ModSounds.ITEM_DANDORI_CALL, 0.4f, 0.8f);
        user.playSound(ModSounds.ITEM_DANDORI_CALL, 0.4f, 0.9f);
        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        if (remainingUseTicks % 10 == 0)
        {
            if (!world.isClient())
            {
                int actualDandoriForceTime = maxUseTime - dandoriForceTime;
                if (remainingUseTicks < actualDandoriForceTime)
                {
                    if (!user.isSneaking())
                    {
                        int dandoriCount = dandoriWhistle(world, user, true, true);
                        if (dandoriCount > 0) ((IEntityWithDandoriCount) user).setRecountDandori();
                    }
                    if (remainingUseTicks + 10 >= actualDandoriForceTime)
                    {
                        effectWhistle(world, user, actualDandoriForceTime);
                    }
                }
            }
            else
            {
                spawnParticles(world, user);
            }
            user.playSound(ModSounds.ITEM_DANDORI_CALL, 0.4f, 0.8f);
            user.playSound(ModSounds.ITEM_DANDORI_CALL, 0.4f, 0.95f);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        if (user instanceof PlayerEntity)
            ((PlayerEntity)user).getItemCooldownManager().set(this, cooldownTime);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        if (user instanceof PlayerEntity)
            ((PlayerEntity)user).getItemCooldownManager().set(this, cooldownTime);
    }

    private int dandoriWhistle(World world, LivingEntity user, boolean forceDandori, boolean dandoriValue)
    {
        int targetCount = 0;
        List<MobEntity> targetList = world.getNonSpectatingEntities(MobEntity.class, user.getBoundingBox().expand(dandoriRange));
        for (MobEntity target : targetList)
        {
            // Skip the item user.
            if (target == user) continue;
            // Skip anything the user is currently riding.
            if (target.hasPassenger(user)) continue;
            // Skip anything that doesn't follow dandori rules
            if (!(target instanceof IEntityDandoriFollower)) continue;
            IEntityDandoriFollower dandoriTarget = (IEntityDandoriFollower) target;
            // Skip things that already have dandori active?
            if (dandoriTarget.getDandoriState() == dandoriValue) continue;
            // If the thing has an owner, skip ones unless we are the owner.
            boolean targetHasOwner = dandoriTarget.getOwner() != null;
            if (targetHasOwner)
            {
                if (dandoriTarget.getOwner() != user) continue;
            }
            else continue;

            targetCount++;
            // If the pik doesn't have a target, or if we're forcing dandori, activate the pik's dandori mode.
            if (target.getTarget() == null || forceDandori)
            {
                dandoriTarget.setDandoriState(dandoriValue);
            }
        }
        return targetCount;
    }

    private int dandoriDeploy(World world, LivingEntity user, BlockPos position, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType, int count)
    {
        if (position == null) return 0;

        int targetCount = 0;
        List<MobEntity> targetList = world.getNonSpectatingEntities(MobEntity.class, user.getBoundingBox().expand(dandoriRange * 2));
        for (MobEntity target : targetList)
        {
            if (targetCount >= count && count > 0) break;
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
                if (((IEntityDandoriFollower) target).getOwner() != user) continue;
            }
            else continue;
            // SKip anything that isn't of the player's currently selected type.
            if (!DataDandoriCount.entityIsOfType(currentType, target)) continue;

            targetCount++;
            // Deploy the pik to the given location.
            ((IEntityDandoriFollower) target).setDeployPosition(position);
            ((IEntityDandoriFollower) target).setDandoriState(false);
        }
        return targetCount;
    }

    private void spawnParticles(World world, LivingEntity user)
    {
        Vec3d particlePos = new Vec3d(0,0,1);
        particlePos = particlePos.rotateY(user.getYaw() * -MathHelper.RADIANS_PER_DEGREE);
        particlePos = particlePos.add(user.getVelocity());
        world.addParticle(ParticleTypes.NOTE,
                user.getX() + particlePos.x, user.getEyeY(), user.getZ() + particlePos.z,
                particlePos.x * 3, particlePos.y, particlePos.z * 3);
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

    private void effectDeploy(World world, int time, float size, Vec3d position)
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

}
