package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDandoriStaff extends ItemDandoriCall
{
    public ItemDandoriStaff(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_1"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_2"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_3"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_4"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_5"));
        pTooltipComponents.add(Component.translatable("item.golemfirststonemod.item_description.item_dandori_staff_6"));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public int getUseDuration(@NotNull ItemStack pStack) {
        return 0;
    }

    protected void playWhistleSound(Player pPlayer)
    {
        pPlayer.playSound(ModSounds.ITEM_DANDORI_BANNER_01.get(), 0.8f, 1.0f);
    }
    protected void playWhistleSoundForced(LivingEntity pLivingEntity)
    {
        pLivingEntity.playSound(ModSounds.ITEM_DANDORI_BANNER_02.get(), 0.8f, 1.0f);
    }

    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand)
    {
        if (!pPlayer.isCrouching())
        {
            if (pInteractionTarget instanceof IEntityDandoriFollower dandoriFollower && dandoriFollower.getOwner() == pPlayer)
            {
                if (!pInteractionTarget.hasPassenger(pPlayer))
                {
                    if (!pPlayer.level().isClientSide())
                    {
                        if (dandoriFollower.isDandoriOff())
                        {
                            dandoriFollower.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.HARD.ordinal());
                            pInteractionTarget.addEffect(new MobEffectInstance(glowEffect));
                        }
                        else dandoriFollower.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.OFF.ordinal());
                    }
                    this.playWhistleSound(pPlayer);
                }
            }
        }
        pPlayer.getCooldowns().addCooldown(this, cooldownTime);
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        if (!pLevel.isClientSide())
        {
            if (pPlayer.isCrouching())
            {
                effectWhistle(pLevel, pPlayer, 5);
                int dandoriCount = dandoriWhistle(pLevel, pPlayer, true, IEntityDandoriFollower.DANDORI_STATES.OFF);
                if (dandoriCount > 0) ((IEntityWithDandoriCount) pPlayer).setRecountDandori();
            }
        }
        this.playWhistleSound(pPlayer);
        pPlayer.getCooldowns().addCooldown(this, cooldownTime);
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration)
    {
    }

    public ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity)
    {
        return pStack;
    }

    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving, int pTimeLeft)
    {
    }

}
