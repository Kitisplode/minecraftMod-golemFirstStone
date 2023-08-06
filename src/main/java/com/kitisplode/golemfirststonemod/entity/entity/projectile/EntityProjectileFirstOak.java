package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
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
    private IronGolem golemOwner;
    private final float attackVerticalRange = 3.0f;

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

    public EntityProjectileFirstOak(Level world, @NotNull IronGolem owner) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), world);
        golemOwner = owner;
        this.setPos(owner.getEyePosition());
    }

    public EntityProjectileFirstOak(Level world, @NotNull IronGolem owner, float pAoERange, float pDamage)
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
            if (target instanceof AbstractGolem) return;
            if (target instanceof AbstractVillager) return;
            if (target instanceof Merchant) return;
            if (target instanceof Player)
            {
                if (golemOwner != null && golemOwner.isPlayerCreated()) return;
            }
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
            // Do not damage targets that are villagers or golems.
            if (target instanceof AbstractVillager) continue;
            if (target instanceof Merchant) continue;
            if (target instanceof AbstractGolem) continue;
            // Do not damage players if the golem is player made.
            if (target instanceof Player)
            {
                if (golemOwner != null && golemOwner.isPlayerCreated()) return;
            }
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
