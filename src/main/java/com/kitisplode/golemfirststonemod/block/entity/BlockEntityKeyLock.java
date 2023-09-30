package com.kitisplode.golemfirststonemod.block.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.BlockKeyLock;
import com.kitisplode.golemfirststonemod.block.ModBlockEntities;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class BlockEntityKeyLock extends BlockEntity implements Container, GeoBlockEntity
{
    public static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/item/golem_key.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/dungeons/golem_key.png");
    public static final ResourceLocation TEXTURE_SCARED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/dungeons/golem_key_scared.png");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/item/golem_key.animation.json");

    public static final RawAnimation ANIMATION_EMPTY = RawAnimation.begin().thenPlayAndHold("animation.golem_key.gone");
    public static final RawAnimation ANIMATION_INSERTED = RawAnimation.begin().thenPlayAndHold("animation.golem_key.inserted");
    public static final RawAnimation ANIMATION_INSERTED_LOOP = RawAnimation.begin().thenLoop("animation.golem_key.inserted_loop");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int slotCount = 1;
    private final NonNullList<ItemStack> items = NonNullList.withSize(slotCount, ItemStack.EMPTY);

    public BlockEntityKeyLock(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntities.BLOCK_KEY_LOCK.get(), pPos, pBlockState);
    }

    public void load(CompoundTag pTag) {
        this.items.clear();
        ContainerHelper.loadAllItems(pTag, this.items);
    }

    protected void saveAdditional(CompoundTag pTag) {
        ContainerHelper.saveAllItems(pTag, this.items, true);
    }

    @Override
    public int getContainerSize()
    {
        return slotCount;
    }

    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int pSlot)
    {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount)
    {
        ItemStack itemstack = Objects.requireNonNullElse(this.items.get(pSlot), ItemStack.EMPTY);
        this.items.set(pSlot, ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.updateState();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot)
    {
        return this.removeItem(pSlot, 1);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        if (pStack.is(ModItems.ITEM_GOLEM_KEY.get())) {
            this.items.set(pSlot, pStack);
            this.updateState();
        }
    }

    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent()
    {
        this.items.clear();
    }

    private void updateState()
    {
        BlockState bs = this.getBlockState();
        bs = bs.setValue(BlockKeyLock.POWERED, Boolean.valueOf(!this.isEmpty()));
        Objects.requireNonNull(this.level).setBlock(this.worldPosition, bs, 3);
    }

    public BlockPos getKeyPosition()
    {
        Direction direction = this.getBlockState().getValue(DirectionalBlock.FACING);
        return this.getBlockPos().relative(direction);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        return TEXTURE;
    }
    public ResourceLocation getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            BlockState bs = this.getBlockState();
            if (bs.getValue(BlockKeyLock.POWERED))
            {
                return event.setAndContinue(ANIMATION_INSERTED);
            }
            return event.setAndContinue(ANIMATION_EMPTY);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
