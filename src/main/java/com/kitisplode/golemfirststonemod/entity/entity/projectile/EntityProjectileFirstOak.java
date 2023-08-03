package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityProjectileFirstOak extends ArrowEntity
{
    private float attackAOERange;
    private float attackDamage;
    private IronGolemEntity golemOwner;
    private final float attackVerticalRange = 3.0f;

    public EntityProjectileFirstOak(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
        attackAOERange = 1;
        attackDamage = 1;
        golemOwner = null;
    }

    public EntityProjectileFirstOak(World world, double x, double y, double z) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK, world);
        this.setPos(x,y,z);
    }

    public EntityProjectileFirstOak(World world, @NotNull IronGolemEntity owner) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK, world);
        golemOwner = owner;
        this.setPosition(owner.getEyePos());
    }

    public EntityProjectileFirstOak(World world, @NotNull IronGolemEntity owner, float pAoERange, float pDamage)
    {
        this(world, owner);
        attackAOERange = pAoERange;
        attackDamage = pDamage;
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        Entity target = entityHitResult.getEntity();
        // Skip some targets.
        if (target != null)
        {
            if (target instanceof GolemEntity) return;
            if (target instanceof VillagerEntity) return;
            if (target instanceof MerchantEntity) return;
            if (target instanceof PlayerEntity)
            {
                if (golemOwner != null && golemOwner.isPlayerCreated()) return;
            }
        }
        // Then perform the damage.
        super.onEntityHit(entityHitResult);
        attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack asItemStack()
    {
        return null;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
        for (LivingEntity target : targetList)
        {
            // Do not damage targets that are villagers or golems.
            if (target instanceof VillagerEntity) continue;
            if (target instanceof MerchantEntity) continue;
            if (target instanceof GolemEntity) continue;
            // Do not damage players if the golem is player made.
            if (target instanceof PlayerEntity)
            {
                if (golemOwner != null && golemOwner.isPlayerCreated()) continue;
            }
            // Do not damage targets that are too far on the y axis.
            if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

            // Apply damage.
            float forceMultiplier = Math.abs((attackAOERange - this.distanceTo(target)) / attackAOERange);
            float totalDamage = attackDamage * forceMultiplier;
            DamageSource ds;
            if (this.getOwner() == null)
                ds = this.getDamageSources().arrow(this, this);
            else
                ds = this.getDamageSources().arrow(this, this.getOwner());
            target.damage(ds, totalDamage);
//            applyDamageEffects(this, target);
        }
    }
}
