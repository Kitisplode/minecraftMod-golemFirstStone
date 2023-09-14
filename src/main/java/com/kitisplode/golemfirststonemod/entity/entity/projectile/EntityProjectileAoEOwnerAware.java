package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class EntityProjectileAoEOwnerAware extends Arrow
{
    private float attackAOERange;
    private float attackDamage;
    private LivingEntity golemOwner;
    private static final float attackVerticalRange = 3.0f;
    private boolean hasAoE = true;
    private static final int maxAge = 1200;

    public EntityProjectileAoEOwnerAware(EntityType<? extends Arrow> entityType, Level world) {
        super(entityType, world);
        attackAOERange = 1;
        attackDamage = 1;
        golemOwner = null;
    }

    public EntityProjectileAoEOwnerAware(Level world, double x, double y, double z) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), world);
        this.setPos(x,y,z);
    }

    public EntityProjectileAoEOwnerAware(Level world, @NotNull LivingEntity owner) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), world);
        golemOwner = owner;
        this.setPos(owner.getEyePosition());
    }

    public EntityProjectileAoEOwnerAware(Level world, @NotNull LivingEntity owner, float pAoERange, float pDamage)
    {
        this(world, owner);
        attackAOERange = pAoERange;
        attackDamage = pDamage;
    }

    public void setHasAoE(boolean p)
    {
        this.hasAoE = p;
    }
    public void setAoERange(float pAoERange)
    {
        this.attackAOERange = pAoERange;
    }
    public void setOwner(LivingEntity pOwner)
    {
        this.golemOwner = pOwner;
//        super.setOwner(pOwner);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.tickCount > maxAge) this.discard();
    }

    protected boolean canHitEntity(Entity target)
    {
        if (target != null)
        {
            LivingEntity owner = null;
            LivingEntity ownerOwner = null;
            if (golemOwner instanceof IEntityDandoriFollower dandoriFollower) owner = dandoriFollower.getOwner();
            if (owner instanceof IEntityDandoriFollower dandoriOwner) ownerOwner = dandoriOwner.getOwner();

            // Do not damage the golem that shot this arrow.
            if (target == golemOwner) return false;
            // Do not damage the golem's owner.
            if (target == owner) return false;
            if (target instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner) return false;
            if (target instanceof IEntityDandoriFollower dandoriFollower)
            {
                if (dandoriFollower.getOwner() == owner) return false;
                if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner
                        && dandoriFollowerOwner.getOwner() == owner) return false;
                if (dandoriFollower.getOwner() == ownerOwner) return false;
                if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner
                        && dandoriFollowerOwner.getOwner() == ownerOwner) return false;
            }
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite)
            {
                if (firstDiorite.getOwner() == owner) return false;
            }
            // Do not damage villagers.
            if (target instanceof AbstractVillager) return false;
        }
        return super.canHitEntity(target);
    }

    @Override
    public void onHitEntity(EntityHitResult pResult)
    {
        super.onHitEntity(pResult);
        if (this.hasAoE) attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult)
    {
        super.onHitBlock(pResult);
        if (this.hasAoE) attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack getPickupItem()
    {
        return ItemStack.EMPTY;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(attackAOERange));
        for (LivingEntity target : targetList)
        {
            if (!this.canHitEntity(target)) continue;
            // Do not damage targets that are too far on the y axis.
            if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

            // Apply damage.
            float forceMultiplier = Math.abs((attackAOERange - this.distanceTo(target)) / attackAOERange);
            float totalDamage = attackDamage * forceMultiplier;
            DamageSource ds;
            if (this.getOwner() == null)
                ds = this.damageSources().arrow(this, this);
            else
                ds = this.damageSources().arrow(this, this.getOwner());
            target.hurt(ds, totalDamage);
        }
    }

}
