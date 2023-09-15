package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InventoryMenuAgent extends AbstractContainerMenu
{
    private final Container agentContainer;
    private final EntityGolemAgent agent;

    private static int slotColumns = 5;

    public InventoryMenuAgent(int pContainerId, Inventory pPlayerInventory, Container pContainer, final EntityGolemAgent pAgent)
    {
        super((MenuType<?>)null, pContainerId);

        this.agentContainer = pContainer;
        this.agent = pAgent;
        pContainer.startOpen(pPlayerInventory.player);
        this.addSlot(new Slot(pContainer, 0, 8, 18)
        {
            public boolean mayPlace(ItemStack itemStack)
            {
                return true;
            }
            public boolean isActive() {
                return true;
            }
        });

        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < slotColumns; ++l) {
                this.addSlot(new Slot(pContainer, 1 + l + k * slotColumns, 80 + l * 18, 18 + k * 18));
            }
        }

        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(pPlayerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(pPlayerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return !this.agent.hasInventoryChanged(this.agentContainer) && this.agentContainer.stillValid(pPlayer) && this.agent.isAlive() && this.agent.distanceTo(pPlayer) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);


        if (slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.agentContainer.getContainerSize();
            if (pIndex < i)
            {
                // Try to move the item to the given slot first
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            // Otherwise, try to move the item to the first slot?
            else if (this.getSlot(0).mayPlace(itemstack1))
            {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (i <= 1 || !this.moveItemStackTo(itemstack1, 1, i, false))
            {
                int j = i + 27;
                int k = j + 9;
                if (pIndex >= j && pIndex < k)
                {
                    if (!this.moveItemStackTo(itemstack1, i, j, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (pIndex >= i && pIndex < j)
                {
                    if (!this.moveItemStackTo(itemstack1, j, k, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.moveItemStackTo(itemstack1, j, j, false))
                {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.agentContainer.stopOpen(pPlayer);
    }
}
