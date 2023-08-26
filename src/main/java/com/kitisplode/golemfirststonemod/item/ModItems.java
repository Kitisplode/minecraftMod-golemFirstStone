package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriAttack;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriCall;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriDig;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriThrow;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    public static final Item ITEM_SPAWN_PAWN_FIRST_DIORITE = registerItem("item_spawn_pawn_first_diorite",
            new SpawnEggItem(ModEntities.ENTITY_PAWN_FIRST_DIORITE, 0xDFDFDF, 0xEBEBEB,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_VILLAGER_DANDORI = registerItem("item_spawn_villager_dandori",
            new SpawnEggItem(ModEntities.ENTITY_VILLAGER_DANDORI, 0xDFDFDF, 0x38A836,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_GOLEM_COBBLE = registerItem("item_spawn_golem_cobble",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_COBBLE, 0xDFDFDF, 0xAAAAAA,
                    new FabricItemSettings()));
    public static final Item ITEM_DANDORI_CALL = registerItem("item_dandori_call",
            new ItemDandoriCall(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_ATTACK = registerItem("item_dandori_attack",
            new ItemDandoriAttack(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_DIG = registerItem("item_dandori_dig",
            new ItemDandoriDig(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_THROW = registerItem("item_dandori_throw",
            new ItemDandoriThrow(new FabricItemSettings().maxCount(1)));

    private static Item registerItem(String pName, Item pItem)
    {
        return Registry.register(Registries.ITEM, new Identifier(GolemFirstStoneMod.MOD_ID, pName), pItem);
    }

    public static void registerModItems()
    {
        GolemFirstStoneMod.LOGGER.info("Registering Mod Items for " + GolemFirstStoneMod.MOD_ID);
    }

    public static void registerModItemsClient()
    {
        ModelPredicateProviderRegistry.register(ITEM_DANDORI_CALL, new Identifier("tooting"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
