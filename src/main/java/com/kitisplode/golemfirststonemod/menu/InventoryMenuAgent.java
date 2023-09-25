package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class InventoryMenuAgent extends ScreenHandler
{
    private final Inventory agentContainer;
    private final EntityGolemAgent agent;

    private static int slotColumns = 5;
    public InventoryMenuAgent(int pContainerId, PlayerInventory pPlayerInventory, Inventory pContainer, final EntityGolemAgent pAgent)
    {
        super(null, pContainerId);

        this.agentContainer = pContainer;
        this.agent = pAgent;

        agentContainer.onOpen(pPlayerInventory.player);
        this.addSlot(new Slot(pContainer, 0, 8, 18)
        {
            public boolean canInsert(ItemStack itemStack)
            {
                return true;
            }
            public boolean isEnabled() {
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
    public boolean canUse(PlayerEntity player) {
        return !this.agent.areInventoriesDifferent(this.agentContainer) && this.agentContainer.canPlayerUse(player) && this.agent.isAlive() && this.agent.distanceTo(player) < 8.0f;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            int i = this.agentContainer.size();
            if (slot < i)
            {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (i <= 2 || !this.insertItem(itemStack2, 2, i, false))
            {
                int k;
                int j = i;
                int l = k = j + 27;
                int m = l + 9;
                if (slot >= l && slot < m ? !this.insertItem(itemStack2, j, k, false) : (slot >= j && slot < k ? !this.insertItem(itemStack2, l, m, false) : !this.insertItem(itemStack2, l, k, false))) {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.agentContainer.onClose(player);
    }
}
