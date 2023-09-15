package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus
{
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GolemFirstStoneMod.MOD_ID);
//
//    public static final RegistryObject<MenuType<InventoryMenuAgent>> MENU_INVENTORY_AGENT =
//            registerMenuType(InventoryMenuAgent::new, "menu_inventory_agent");


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                  String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static void registerScreens()
    {
//        MenuScreens.register(MENU_INVENTORY_AGENT.get(), InventoryScreenAgent::new);
    }
}
