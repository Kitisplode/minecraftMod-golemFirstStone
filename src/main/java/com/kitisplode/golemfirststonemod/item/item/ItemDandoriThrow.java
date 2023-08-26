package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDandoriThrow extends Item
{
    static private final int maxUseTime = 72000;
    static private final int cooldownTime = 1;
    static private final double throwSpeed = 2.2;
    static private final double dandoriRange = 10;
    static private final int fullStrengthTime = 10;
    static private final int minStrengthTime = 6;

    public ItemDandoriThrow(Settings settings)
    {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_throw_1"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_throw_2"));
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
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.isSneaking())
        {
            if (user instanceof IEntityWithDandoriCount player) player.nextDandoriCurrentType();
            return TypedActionResult.pass(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        int holdTicks = maxUseTime - remainingUseTicks;
        holdTicks = MathHelper.clamp(holdTicks, minStrengthTime, fullStrengthTime);
        double strength = (double) ((float) holdTicks / (float) fullStrengthTime) * throwSpeed;
        setCooldown(cooldownTime, user);
        DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
        int thrown = dandoriThrow(world, user, strength, false, currentType);
        if (thrown > 0)
        {
            user.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.playSound(ModSounds.ITEM_DANDORI_THROW, 0.4f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.4f));
        }
        user.swingHand(user.getActiveHand());
    }

    private int dandoriThrow(World world, LivingEntity user, double speed, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType)
    {
        TargetPredicate tp = TargetPredicate.createNonAttackable().setPredicate(
                entity -> DataDandoriCount.entityIsOfType(currentType, entity)
                        && ((EntityPawn)entity).getOwner() == user
                        && (((EntityPawn)entity).getDandoriState() || forceDandori));
        EntityPawn pawn = world.getClosestEntity(EntityPawn.class, tp, null, user.getX(),user.getY(),user.getZ(), user.getBoundingBox().expand(dandoriRange));
        int targetCount = 0;
        if (pawn != null)
        {
            targetCount++;
            if (!world.isClient())
            {
                pawn.setPos(user.getX(), user.getEyeY(), user.getZ());
                Vec3d newVelocity = getUserLookAngle(user).normalize().multiply(speed);
                pawn.setVelocity(user.getVelocity().add(newVelocity));
                pawn.setThrown(true);
            }
            pawn.setDandoriState(false);
        }
        return targetCount;
    }

    private Vec3d getUserLookAngle(LivingEntity user)
    {
        float f = user.getPitch();
        float g = user.getYaw();
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        return new Vec3d((double)l, (double)m, (double)n);
    }

    private void setCooldown(int pCooldown, LivingEntity user)
    {
        if (user instanceof PlayerEntity)
            ((PlayerEntity)user).getItemCooldownManager().set(this, pCooldown);
    }
}
