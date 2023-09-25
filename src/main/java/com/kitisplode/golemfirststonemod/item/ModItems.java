package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.item.*;
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
    public static final Item ITEM_SPAWN_GOLEM_PLANK = registerItem("item_spawn_golem_plank",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_PLANK, 0xDFDFDF, 0xA86836,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_GOLEM_MOSSY = registerItem("item_spawn_golem_mossy",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_MOSSY, 0xDFDFDF, 0x32A852,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_GOLEM_GRINDSTONE = registerItem("item_spawn_golem_grindstone",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_GRINDSTONE, 0xDFDFDF, 0x333333,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_GOLEM_COPPER = registerItem("item_spawn_golem_copper",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_COPPER, 0xDFDFDF, 0xEF8430,
                    new FabricItemSettings()));
    public static final Item ITEM_SPAWN_GOLEM_TUFF = registerItem("item_spawn_golem_tuff",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_TUFF, 0xDFDFDF, 0x424742,
                    new FabricItemSettings()));

    public static final Item ITEM_SPAWN_GOLEM_AGENT = registerItem("item_spawn_golem_agent",
            new SpawnEggItem(ModEntities.ENTITY_GOLEM_AGENT, 0xDFDFDF, 0x424742,
                    new FabricItemSettings()));
    public static final Item ITEM_INSTRUCTION_MOVE_FORWARD = registerItem("item_instruction_move_forward",
            new ItemInstruction(new FabricItemSettings().maxCount(16), "item.golemfirststonemod.item_description.item_instruction_move_forward", 0));
    public static final Item ITEM_INSTRUCTION_TURN_LEFT_90 = registerItem("item_instruction_turn_left_90",
            new ItemInstruction(new FabricItemSettings().maxCount(2), "item.golemfirststonemod.item_description.item_instruction_turn_left_90", 0));
    public static final Item ITEM_INSTRUCTION_TURN_RIGHT_90 = registerItem("item_instruction_turn_right_90",
            new ItemInstruction(new FabricItemSettings().maxCount(2), "item.golemfirststonemod.item_description.item_instruction_turn_right_90", 0));
    public static final Item ITEM_INSTRUCTION_IF_BLOCK = registerItem("item_instruction_if_block",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_if_block", 2));
    public static final Item ITEM_INSTRUCTION_IF_SOLID = registerItem("item_instruction_if_solid",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_if_solid", 1));
    public static final Item ITEM_INSTRUCTION_USE_BLOCK = registerItem("item_instruction_use_block",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_use_block", 0));
    public static final Item ITEM_INSTRUCTION_PLACE_BLOCK = registerItem("item_instruction_place_block",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_place_block", 1));
    public static final Item ITEM_INSTRUCTION_BREAK_BLOCK = registerItem("item_instruction_break_block",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_break_block", 0));
    public static final Item ITEM_INSTRUCTION_USE_TOOL = registerItem("item_instruction_use_tool",
            new ItemInstruction(new FabricItemSettings().maxCount(64), "item.golemfirststonemod.item_description.item_instruction_use_tool", 0));
    public static final Item ITEM_INSTRUCTION_ATTACK = registerItem("item_instruction_attack",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_attack", 0));
    public static final Item ITEM_INSTRUCTION_LOOP = registerItem("item_instruction_loop",
            new ItemInstruction(new FabricItemSettings().maxCount(64), "item.golemfirststonemod.item_description.item_instruction_loop", 0));
    public static final Item ITEM_INSTRUCTION_NOT = registerItem("item_instruction_not",
            new ItemInstruction(new FabricItemSettings().maxCount(1), "item.golemfirststonemod.item_description.item_instruction_not", 1));


    public static final Item ITEM_DANDORI_CALL = registerItem("item_dandori_call",
            new ItemDandoriCall(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_ATTACK = registerItem("item_dandori_attack",
            new ItemDandoriAttack(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_DIG = registerItem("item_dandori_dig",
            new ItemDandoriDig(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_THROW = registerItem("item_dandori_throw",
            new ItemDandoriThrow(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_BANNER = registerItem("item_dandori_banner",
            new ItemDandoriBanner(new FabricItemSettings().maxCount(1)));
    public static final Item ITEM_DANDORI_STAFF = registerItem("item_dandori_staff",
            new ItemDandoriStaff(new FabricItemSettings().maxCount(1)));

    public static final Item ITEM_FLAME_OF_CREATION_BLUE = registerItem("item_flame_of_creation_blue",
            new Item(new FabricItemSettings().maxCount(16)));
    public static final Item ITEM_FLAME_OF_CREATION_RED = registerItem("item_flame_of_creation_red",
            new Item(new FabricItemSettings().maxCount(16)));

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
