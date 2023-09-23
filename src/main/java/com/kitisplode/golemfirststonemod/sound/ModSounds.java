package com.kitisplode.golemfirststonemod.sound;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<SoundEvent> ITEM_DANDORI_CALL = registerSoundEvent("item_dandori_call");
    public static final RegistryObject<SoundEvent> ITEM_DANDORI_BANNER_01 = registerSoundEvent("item_dandori_banner_01");
    public static final RegistryObject<SoundEvent> ITEM_DANDORI_BANNER_02 = registerSoundEvent("item_dandori_banner_02");
    public static final RegistryObject<SoundEvent> ITEM_DANDORI_ATTACK_FAIL = registerSoundEvent("item_dandori_attack_fail");
    public static final RegistryObject<SoundEvent> ITEM_DANDORI_ATTACK_WIN = registerSoundEvent("item_dandori_attack_win");
    public static final RegistryObject<SoundEvent> ITEM_DANDORI_THROW = registerSoundEvent("item_dandori_throw");
    public static final RegistryObject<SoundEvent> ENTITY_VILLAGER_DANDORI_PLUCK = registerSoundEvent("entity_villager_dandori_pluck");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        ResourceLocation id = new ResourceLocation(GolemFirstStoneMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus)
    {
        SOUND_EVENTS.register(eventBus);
    }
}
