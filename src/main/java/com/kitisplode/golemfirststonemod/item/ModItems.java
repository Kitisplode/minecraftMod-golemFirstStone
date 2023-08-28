package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriAttack;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriCall;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriDig;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriThrow;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    // Create a list of items to be registered
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<Item> ITEM_SPAWN_PAWN_FIRST_DIORITE = ITEMS.register("item_spawn_pawn_first_diorite",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_PAWN_FIRST_DIORITE, 0xDFDFDF, 0xEBEBEB,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_VILLAGER_DANDORI = ITEMS.register("item_spawn_villager_dandori",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_VILLAGER_DANDORI, 0xDFDFDF, 0x38A836,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_DANDORI_CALL = ITEMS.register("item_dandori_call",
            () -> new ItemDandoriCall(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_ATTACK = ITEMS.register("item_dandori_attack",
            () -> new ItemDandoriAttack(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_DIG = ITEMS.register("item_dandori_dig",
            () -> new ItemDandoriDig(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_THROW = ITEMS.register("item_dandori_throw",
            () -> new ItemDandoriThrow(new Item.Properties().stacksTo(1)));


    // Called to actually register the items list.
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }

    public static void registerModelPredicates()
    {
        ItemProperties.register(ITEM_DANDORI_CALL.get(), new ResourceLocation("tooting"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}