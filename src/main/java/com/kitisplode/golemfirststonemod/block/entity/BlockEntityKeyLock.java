package com.kitisplode.golemfirststonemod.block.entity;

import com.kitisplode.golemfirststonemod.block.ModBlockEntities;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class BlockEntityKeyLock extends BlockEntity implements Container
{
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
        bs = bs.setValue(ObserverBlock.POWERED, Boolean.valueOf(!this.isEmpty()));
        Objects.requireNonNull(this.level).setBlock(this.worldPosition, bs, 3);
    }
}
