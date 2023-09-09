package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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

public class ItemDandoriThrow extends Item
{
    static private final int maxUseTime = 72000;
    static private final int cooldownTime = 1;
    static private final double throwSpeed = 2.2;
    static private final double dandoriRange = 10;
    static private final int fullStrengthTime = 10;
    static private final int minStrengthTime = 6;

    public ItemDandoriThrow(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_throw_1"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_throw_2"));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return maxUseTime;
    }

    @Override
    @NotNull
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isCrouching())
        {
            if (pPlayer instanceof IEntityWithDandoriCount player) player.nextDandoriCurrentType();
            return InteractionResultHolder.pass(itemStack);
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level world, @NotNull LivingEntity user, int pTimeLeft)
    {
        int holdTicks = maxUseTime - pTimeLeft;
        holdTicks = Mth.clamp(holdTicks, minStrengthTime, fullStrengthTime);
        double strength = (double) ((float) holdTicks / (float) fullStrengthTime) * throwSpeed;
        setCooldown(cooldownTime, user);
        DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
        int thrown = dandoriThrow(world, user, strength, false, currentType);
        if (thrown > 0)
        {
            user.playSound(SoundEvents.SNOWBALL_THROW, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        }
        user.swing(user.getUsedItemHand());
    }

    private int dandoriThrow(Level world, LivingEntity user, double speed, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType)
    {
        TargetingConditions tp = TargetingConditions.forNonCombat().selector(
                entity -> DataDandoriCount.entityIsOfType(currentType, entity)
                        && entity instanceof IEntityDandoriFollower
                        && ((IEntityDandoriFollower)entity).getOwner() == user
                        && ((IEntityDandoriFollower)entity).isThrowable()
                        && (((IEntityDandoriFollower)entity).isDandoriOn() || forceDandori));
        LivingEntity throwableGolem = world.getNearestEntity(LivingEntity.class, tp, null, user.getX(),user.getY(),user.getZ(), user.getBoundingBox().inflate(dandoriRange));
        int targetCount = 0;
        if (throwableGolem != null)
        {
            IEntityDandoriFollower follower = (IEntityDandoriFollower) throwableGolem;
            targetCount++;
            if (!world.isClientSide())
            {
                throwableGolem.setPos(user.getX(), user.getEyeY(), user.getZ());
                Vec3 newVelocity = getUserLookAngle(user).normalize().scale(speed);
                throwableGolem.setDeltaMovement(user.getDeltaMovement().add(newVelocity));
                follower.setThrown(true);
            }
            if (follower instanceof EntityPawn)
                user.playSound(ModSounds.ITEM_DANDORI_THROW.get(), 0.6f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.4f));
            follower.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.OFF.ordinal());
        }
        return targetCount;
    }

    private Vec3 getUserLookAngle(LivingEntity user)
    {
        float f = user.getXRot();
        float g = user.getYRot();
        float h = Mth.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = Mth.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -Mth.cos(-f * ((float)Math.PI / 180));
        float k = Mth.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        return new Vec3((double)l, (double)m, (double)n);
    }

    private void setCooldown(int pCooldown, LivingEntity user)
    {
        if (user instanceof Player)
            ((Player)user).getCooldowns().addCooldown(this, pCooldown);
    }
}
