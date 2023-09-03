package com.kitisplode.golemfirststonemod.entity.entity.effect;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
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
    private SoundCategory soundCategory;
    private ArrayList<SoundNode> nodes;
    public EntitySoundRepeated(EntityType<?> type, World world)
    {
        super(type, world);
        nodes = new ArrayList<>();
    }

    public EntitySoundRepeated(World world, SoundCategory soundCategory)
    {
        this(ModEntities.ENTITY_SOUND_REPEATED, world);
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
            if (sn.isTimeToPlay(this.age))
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
    protected void initDataTracker()
    {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
    }

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
        public SoundNode(EntitySoundRepeated entity, SoundEvent soundEvent, int timeToPlay, float volume, float pitch)
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
            this.entity.getWorld().playSound(this.entity, this.entity.getBlockPos(), this.soundEvent, this.entity.soundCategory, this.volume, this.pitch);
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
