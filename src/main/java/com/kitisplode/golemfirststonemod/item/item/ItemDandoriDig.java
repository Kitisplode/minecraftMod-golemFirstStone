package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDandoriDig extends Item
{
    static private final double dandoriRange = 10;
    static private final double maxAttackRange = 8;

    public ItemDandoriDig(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_dig_1"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_dig_2"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_dig_3"));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        if (pPlayer.isCrouching())
        {
            if (pPlayer instanceof IEntityWithDandoriCount player) player.nextDandoriCurrentType();
        }
        else
        {
            DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) pPlayer).getDandoriCurrentType();
            ClipContext.Fluid fh = ClipContext.Fluid.ANY;
            if (pPlayer.isUnderWater()) fh = ClipContext.Fluid.NONE;
            BlockHitResult ray = Item.getPlayerPOVHitResult(pLevel, pPlayer, fh);
            Vec3 pos = ray.getLocation();
            if (pPlayer.distanceToSqr(pos) <= Mth.square(5))
            {
                int attackers = dandoriAttackTile(pLevel, pPlayer, ray.getBlockPos(), false, currentType);
                if (attackers == 0) pPlayer.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL.get(), 1.0f, 0.9f);
                else pPlayer.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN.get(), 1.0f, 1.0f);
            }
        }
        pPlayer.startUsingItem(pUsedHand);
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        return InteractionResultHolder.success(itemStack);
    }

    private int dandoriAttackTile(Level world, Player user, BlockPos blockPos, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType)
    {
        TargetingConditions tp = TargetingConditions.forNonCombat().selector(
                entity -> DataDandoriCount.entityIsOfType(currentType, entity)
                        && entity instanceof IEntityDandoriFollower dandoriFollower
                        && dandoriFollower.getOwner() == user
                        && (dandoriFollower.getDandoriState() || forceDandori)
                        && entity instanceof IEntityCanAttackBlocks blockAttacker
                        && blockAttacker.canTargetBlock(blockPos));
        LivingEntity golem = world.getNearestEntity(LivingEntity.class, tp, null, user.getX(),user.getY(),user.getZ(), user.getBoundingBox().inflate(dandoriRange));
        int targetCount = 0;
        if (golem != null)
        {
            IEntityCanAttackBlocks blockAttacker = (IEntityCanAttackBlocks) golem;
            targetCount++;
            blockAttacker.setBlockTarget(blockPos);
            ((IEntityDandoriFollower)golem).setDandoriState(false);
        }
        return targetCount;
    }

}
