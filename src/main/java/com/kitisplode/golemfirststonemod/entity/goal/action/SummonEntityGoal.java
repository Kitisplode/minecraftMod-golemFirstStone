package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class SummonEntityGoal<T extends PathfinderMob & IEntitySummoner, R extends Entity & IEntityDandoriFollower> extends Goal
{
    protected final T summoner;
    protected final Class<R> targetClass;
    protected final int[] summonStages;
    protected final double pikSearchRange;
    protected final int pikCountMax;
    protected int cooldownTimer = 0;
    protected final int cooldownTime;
    protected int summonTimer;
    protected int summonState = 0;
    protected int previousSummonState = 0;
    protected final int fastRepeatStage;

    public SummonEntityGoal(T pMob, Class<R> spawnClass, int[] pAttackStages, double pPikSearchRange, int pikCountMax, int time, int fastRepeatStage)
    {
        this.summoner = pMob;
        this.targetClass = spawnClass;
        this.summonStages = pAttackStages.clone();
        this.pikSearchRange = pPikSearchRange;
        this.pikCountMax = pikCountMax;
        this.cooldownTime = time;
        this.cooldownTimer = 0;
        this.fastRepeatStage = fastRepeatStage;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (!this.isCooledDown())
        {
            this.cooldownTimer++;
            return false;
        }
        if (!this.summoner.isReadyToSummon()) return false;
        if (!this.checkPikCount()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.summonState != 0;
    }

    @Override
    public void tick()
    {
        if (this.summonTimer > 0)
        {
            this.forceSummon(this.summonTimer - 1);
        }
        // When we actually change state to one where we should attack, do the actual attack.
        if (this.previousSummonState != this.summonState)
        {
            boolean shouldFastRepeat = this.summoner.trySummon(this.summonState);
            if (shouldFastRepeat && this.fastRepeatStage >= 0 && this.checkPikCount() && this.summoner.isReadyToSummon())
            {
                this.forceSummon(this.summonStages[this.fastRepeatStage]-1);
            }
        }
        this.previousSummonState = this.summonState;
        this.summoner.setSummonState(this.summonState);
    }

    @Override
    public void start()
    {
        this.forceSummon(this.summonStages[0]);
        this.summoner.getNavigation().stop();
    }

    @Override
    public void stop()
    {
        this.cooldownTimer = 0;
    }

    private boolean checkPikCount()
    {
        List<R> listPiks = this.summoner.level().getEntitiesOfClass(this.targetClass,
                this.summoner.getBoundingBox().inflate(this.pikSearchRange),
                entity -> entity.getOwner() == this.summoner);
        return listPiks.size() < this.pikCountMax;
    }

    private int calculateCurrentSummonState(int pAttackTimer)
    {
        if (pAttackTimer <= 0)
            return 0;
        for (int i = 1; i < this.summonStages.length; i++)
        {
            if (pAttackTimer < this.summonStages[i-1] && pAttackTimer >= this.summonStages[i]) return i;
        }
        return this.summonStages.length;
    }

    public void forceSummon(int pSummonTimer)
    {
        this.summonTimer = pSummonTimer;
        this.summonState = calculateCurrentSummonState(this.summonTimer);
    }

    public boolean isCooledDown()
    {
        return this.cooldownTimer >= this.cooldownTime;
    }
}
