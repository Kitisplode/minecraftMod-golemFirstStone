package com.kitisplode.golemfirststonemod.entity.entity.effect;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.ArrayList;
import java.util.Collections;

// This entity plays a number of sounds (with specified pitches and volumes) at specified times.
// Inspired by the funny wario laugh sounds in Wario Land 4 where wario's vocal track just repeats but at different pitches lol
public class EntitySoundRepeated extends Entity implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private SoundSource soundCategory;
    private ArrayList<SoundNode> nodes;
    public EntitySoundRepeated(EntityType<?> type, Level world)
    {
        super(type, world);
        nodes = new ArrayList<>();
    }

    public EntitySoundRepeated(Level world, SoundSource soundCategory)
    {
        this(ModEntities.ENTITY_SOUND_REPEATED.get(), world);
        this.soundCategory = soundCategory;
    }
    public void addSoundNode(SoundEvent soundEvent, int timeToPlay, float volume, float pitch)
    {
        SoundNode sn = new SoundNode(this, soundEvent, timeToPlay, volume, pitch);
        if (sn != null) nodes.add(sn);
    }

    public void sortSoundNodes()
    {
        Collections.sort(nodes);
    }

    @Override
    public void tick()
    {
        super.tick();
        for (int i = 0; i < nodes.size(); i++)
        {
            SoundNode sn = nodes.get(i);
            if (sn.isTimeToPlay(this.tickCount))
            {
                sn.play();
                nodes.remove(i);
                i--;
            }
        }
        if (nodes.size() == 0)
        {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {}
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    class SoundNode implements Comparable<SoundNode>
    {
        private final EntitySoundRepeated entity;
        private final SoundEvent soundEvent;
        private final int timeToPlay;
        private final float pitch;
        private final float volume;
        SoundNode(EntitySoundRepeated entity, SoundEvent soundEvent, int timeToPlay, float volume, float pitch)
        {
            this.entity = entity;
            this.soundEvent = soundEvent;
            this.timeToPlay = timeToPlay;
            this.volume = volume;
            this.pitch = pitch;
        }
        public boolean isTimeToPlay(int currentTime)
        {
            return currentTime >= timeToPlay;
        }
        public void play()
        {
            this.entity.level().playSound(this.entity, this.entity.getOnPos(), this.soundEvent, this.entity.soundCategory, this.volume, this.pitch);
        }
        @Override
        public int compareTo(@NotNull EntitySoundRepeated.SoundNode o)
        {
            if (this.timeToPlay < o.timeToPlay) return -1;
            if (this.timeToPlay > o.timeToPlay) return 1;
            return 0;
        }
    }
}
