package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class EntityProjectileFirstOak extends Arrow
{
    private float attackAOERange;
    private float attackDamage;
    private AbstractGolemDandoriFollower golemOwner;
    private static final float attackVerticalRange = 3.0f;

    public EntityProjectileFirstOak(EntityType<? extends Arrow> entityType, Level world) {
        super(entityType, world);
        attackAOERange = 1;
        attackDamage = 1;
        golemOwner = null;
    }

    public EntityProjectileFirstOak(Level world, double x, double y, double z) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), world);
        this.setPos(x,y,z);
    }

    public EntityProjectileFirstOak(Level world, @NotNull AbstractGolemDandoriFollower owner) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), world);
        golemOwner = owner;
        this.setPos(owner.getEyePosition());
    }

    public EntityProjectileFirstOak(Level world, @NotNull AbstractGolemDandoriFollower owner, float pAoERange, float pDamage)
    {
        this(world, owner);
        attackAOERange = pAoERange;
        attackDamage = pDamage;
    }

    @Override
    public void onHitEntity(EntityHitResult pResult)
    {
        Entity target = pResult.getEntity();
        // Skip some targets.
        if (target != null)
        {
            LivingEntity owner = null;
            if (golemOwner != null) owner = golemOwner.getOwner();
            // Do not damage the golem that shot this arrow.
            if (target == golemOwner) return;
            // Do not damage the golem's owner.
            if (target == owner) return;
            if (target instanceof TamableAnimal && ((TamableAnimal)target).getOwner() == owner) return;
            if (target instanceof IEntityDandoriFollower && ((IEntityDandoriFollower)target).getOwner() == owner) return;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && ((EntityPawn)target).getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
            {
                EntityGolemFirstDiorite pawnOwner = (EntityGolemFirstDiorite) pawn.getOwner();
                if (pawnOwner.getOwner() == owner) return;
            }
            // Do not damage villagers.
            if (target instanceof Merchant) return;
        }
        // Then perform the damage.
        super.onHitEntity(pResult);
        attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult)
    {
        super.onHitBlock(pResult);
        attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack getPickupItem()
    {
        return null;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(attackAOERange));
        for (LivingEntity target : targetList)
        {
            LivingEntity owner = null;
            if (golemOwner != null) owner = golemOwner.getOwner();
            // Do not damage the golem that shot this arrow.
            if (target == golemOwner) continue;
            // Do not damage the golem's owner.
            if (target == owner) continue;
            if (target instanceof TamableAnimal && ((TamableAnimal)target).getOwner() == owner) continue;
            if (target instanceof IEntityDandoriFollower && ((IEntityDandoriFollower)target).getOwner() == owner) continue;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && ((EntityPawn)target).getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
            {
                EntityGolemFirstDiorite pawnOwner = (EntityGolemFirstDiorite) pawn.getOwner();
                if (pawnOwner.getOwner() == owner) continue;
            }
            // Do not damage villagers.
            if (target instanceof Merchant) continue;
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
