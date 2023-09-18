package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.item.*;
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
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_COBBLE = ITEMS.register("item_spawn_golem_cobble",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_COBBLE, 0xDFDFDF, 0xAAAAAA,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_PLANK = ITEMS.register("item_spawn_golem_plank",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_PLANK, 0xDFDFDF, 0xA86836,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_MOSSY = ITEMS.register("item_spawn_golem_mossy",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_MOSSY, 0xDFDFDF, 0x32A852,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_GRINDSTONE = ITEMS.register("item_spawn_golem_grindstone",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_GRINDSTONE, 0xDFDFDF, 0x333333,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_COPPER = ITEMS.register("item_spawn_golem_copper",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_COPPER, 0xDFDFDF, 0xEF8430,
                    new Item.Properties()));
    public static final RegistryObject<Item> ITEM_SPAWN_GOLEM_TUFF = ITEMS.register("item_spawn_golem_tuff",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_GOLEM_TUFF, 0xDFDFDF, 0x424742,
                    new Item.Properties()));


    public static final RegistryObject<Item> ITEM_DANDORI_CALL = ITEMS.register("item_dandori_call",
            () -> new ItemDandoriCall(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_ATTACK = ITEMS.register("item_dandori_attack",
            () -> new ItemDandoriAttack(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_DIG = ITEMS.register("item_dandori_dig",
            () -> new ItemDandoriDig(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_THROW = ITEMS.register("item_dandori_throw",
            () -> new ItemDandoriThrow(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_DANDORI_BANNER = ITEMS.register("item_dandori_banner",
            () -> new ItemDandoriBanner(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ITEM_FLAME_OF_CREATION_BLUE = ITEMS.register("item_flame_of_creation_blue",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_FLAME_OF_CREATION_RED = ITEMS.register("item_flame_of_creation_red",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ITEM_INSTRUCTION_MOVE_FORWARD = ITEMS.register("item_instruction_move_forward",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_INSTRUCTION_TURN_LEFT_90 = ITEMS.register("item_instruction_turn_left_90",
            () -> new Item(new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ITEM_INSTRUCTION_TURN_RIGHT_90 = ITEMS.register("item_instruction_turn_right_90",
            () -> new Item(new Item.Properties().stacksTo(2)));


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