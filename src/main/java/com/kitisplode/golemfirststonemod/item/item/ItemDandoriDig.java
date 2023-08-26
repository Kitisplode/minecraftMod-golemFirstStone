package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDandoriDig extends Item
{
    static private final double dandoriRange = 10;
    static private final double maxAttackRange = 8;

    public ItemDandoriDig(Settings settings)
    {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_dig_1"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_dig_2"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_dig_3"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (user.isSneaking())
        {
            if (user instanceof IEntityWithDandoriCount player)
            {
                player.nextDandoriCurrentType();
            }
        }
        else
        {
            DataDandoriCount.FOLLOWER_TYPE currentType = ((IEntityWithDandoriCount) user).getDandoriCurrentType();
            RaycastContext.FluidHandling fh = RaycastContext.FluidHandling.ANY;
            if (user.isSubmergedInWater()) fh = RaycastContext.FluidHandling.NONE;
            BlockHitResult ray = raycast(world, user, fh);
            Vec3d pos = ray.getPos();
            if (user.squaredDistanceTo(pos) <= MathHelper.square(5))
            {
                int attackers = dandoriAttackTile(world, user, ray.getBlockPos(), false, currentType);
                if (attackers == 0)
                    user.playSound(ModSounds.ITEM_DANDORI_ATTACK_FAIL, 1.0f, 0.9f);
                else
                    user.playSound(ModSounds.ITEM_DANDORI_ATTACK_WIN, 1.0f, 1.0f);
            }
        }

        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.success(itemStack);
    }
    private int dandoriAttackTile(World world, PlayerEntity user, BlockPos blockPos, boolean forceDandori, DataDandoriCount.FOLLOWER_TYPE currentType)
    {
        TargetPredicate tp = TargetPredicate.createNonAttackable().setPredicate(
                entity -> DataDandoriCount.entityIsOfType(currentType, entity)
                        && ((EntityPawn)entity).getOwner() == user
                        && (((EntityPawn)entity).getDandoriState() || forceDandori)
                        && ((EntityPawn)entity).canTargetBlock(blockPos));
        EntityPawn pawn = world.getClosestEntity(EntityPawn.class, tp, null, user.getX(),user.getY(),user.getZ(), user.getBoundingBox().expand(dandoriRange));
        int targetCount = 0;
        if (pawn != null)
        {
            targetCount++;
            pawn.blockTarget = blockPos;
            pawn.setDandoriState(false);
        }
        return targetCount;
    }
}
