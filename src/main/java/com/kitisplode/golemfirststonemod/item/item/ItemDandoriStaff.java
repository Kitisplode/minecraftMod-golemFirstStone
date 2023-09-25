package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDandoriStaff extends ItemDandoriCall
{
    public ItemDandoriStaff(Settings settings)
    {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_1"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_2"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_3"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_4"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_5"));
        tooltip.add(Text.translatable("item.golemfirststonemod.item_description.item_dandori_staff_6"));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    protected void playWhistleSound(PlayerEntity pPlayer)
    {
        pPlayer.playSound(ModSounds.ITEM_DANDORI_BANNER_01, 0.8f, 1.0f);
    }
    protected void playWhistleSoundForced(LivingEntity pLivingEntity)
    {
        pLivingEntity.playSound(ModSounds.ITEM_DANDORI_BANNER_02, 0.8f, 1.0f);
    }

    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity targetEntity, Hand hand)
    {
        if (!user.isSneaking())
        {
            if (targetEntity instanceof IEntityDandoriFollower dandoriFollower && dandoriFollower.getOwner() == user)
            {
                if (!targetEntity.hasPassenger(user))
                {
                    if (!user.getWorld().isClient())
                    {
                        if (dandoriFollower.isDandoriOff())
                        {
                            dandoriFollower.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.HARD.ordinal());
                            targetEntity.addStatusEffect(new StatusEffectInstance(glowEffect));
                        }
                        else dandoriFollower.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.OFF.ordinal());
                    }
                    this.playWhistleSound(user);
                }
            }
        }
        user.getItemCooldownManager().set(this, cooldownTime);
        return ActionResult.PASS;
    }

    public TypedActionResult<ItemStack> use(World pLevel,PlayerEntity pPlayer, Hand pUsedHand)
    {
        if (!pLevel.isClient())
        {
            if (pPlayer.isSneaking())
            {
                effectWhistle(pLevel, pPlayer, 5);
                int dandoriCount = dandoriWhistle(pLevel, pPlayer, true, IEntityDandoriFollower.DANDORI_STATES.OFF);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) pPlayer).setRecountDandori();
            }
        }
        this.playWhistleSound(pPlayer);
        pPlayer.getItemCooldownManager().set(this, cooldownTime);
        return TypedActionResult.pass(pPlayer.getStackInHand(pUsedHand));
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        return stack;
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {

    }
}
