package com.kitisplode.golemfirststonemod.sound;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds
{
    public static SoundEvent ITEM_DANDORI_CALL = registerSoundEvent("item_dandori_call");
    public static SoundEvent ITEM_DANDORI_ATTACK_FAIL = registerSoundEvent("item_dandori_attack_fail");
    public static SoundEvent ITEM_DANDORI_ATTACK_WIN = registerSoundEvent("item_dandori_attack_win");
    public static SoundEvent ENTITY_VILLAGER_DANDORI_PLUCK = registerSoundEvent("entity_villager_dandori_pluck");

    private static SoundEvent registerSoundEvent(String name)
    {
        Identifier id = new Identifier(GolemFirstStoneMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds()
    {
        GolemFirstStoneMod.LOGGER.info("Registering Sounds for " + GolemFirstStoneMod.MOD_ID);
    }
}
