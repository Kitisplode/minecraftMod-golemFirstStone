package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.google.common.collect.Lists;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityProjectileAoEOwnerAware extends ArrowEntity
{
    private float attackAOERange;
    private float attackDamage;
    private LivingEntity golemOwner;
    private static final float attackVerticalRange = 3.0f;
    private boolean hasAoE = true;
    private int piercingCount = 0;
    private ArrayList<Entity> piercedEntities = null;

    public EntityProjectileAoEOwnerAware(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
        attackAOERange = 1;
        attackDamage = 1;
        golemOwner = null;
    }

    public EntityProjectileAoEOwnerAware(World world, double x, double y, double z) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK, world);
        this.setPos(x,y,z);
    }

    public EntityProjectileAoEOwnerAware(World world, @NotNull LivingEntity owner) {
        this(ModEntities.ENTITY_PROJECTILE_FIRST_OAK, world);
        golemOwner = owner;
        super.setOwner(owner);
        this.setPosition(owner.getEyePos());
    }

    public EntityProjectileAoEOwnerAware(World world, @NotNull LivingEntity owner, float pAoERange, float pDamage)
    {
        this(world, owner);
        attackAOERange = pAoERange;
        attackDamage = pDamage;
    }

    public void setHasAoE(boolean p)
    {
        this.hasAoE = p;
    }
    public void setPiercingCount(int p)
    {
        this.piercingCount = p;
    }

    @Override
    protected boolean canHit(Entity target)
    {
        if (target != null)
        {
            LivingEntity owner = null;
            if (golemOwner != null && golemOwner instanceof IEntityDandoriFollower dandoriFollower) owner = dandoriFollower.getOwner();
            // Do not damage the golem that shot this arrow.
            if (target == golemOwner) return false;
            // Do not damage the golem's owner.
            if (target == owner) return false;
            if (target instanceof TameableEntity && ((TameableEntity)target).getOwner() == owner) return false;
            if (target instanceof IEntityDandoriFollower && ((IEntityDandoriFollower)target).getOwner() == owner) return false;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && ((EntityPawn)target).getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
            {
                EntityGolemFirstDiorite pawnOwner = (EntityGolemFirstDiorite) pawn.getOwner();
                if (pawnOwner != null && pawnOwner.getOwner() == owner) return false;
            }
            // Do not damage villagers.
            if (target instanceof MerchantEntity) return false;
        }
        return super.canHit(target);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        if (this.hasAoE) attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        if (this.hasAoE) attackAOE();
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack asItemStack()
    {
        return ItemStack.EMPTY;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
        for (LivingEntity target : targetList)
        {
            LivingEntity owner = null;
            if (golemOwner != null && golemOwner instanceof IEntityDandoriFollower dandoriFollower) owner = dandoriFollower.getOwner();
            // Do not damage targets that are our owner or are owned by our owner.
            if (owner == target) continue;
            if (target instanceof TameableEntity && ((TameableEntity)target).getOwner() == owner) continue;
            if (target instanceof IEntityDandoriFollower && ((IEntityDandoriFollower)target).getOwner() == owner) continue;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && ((EntityPawn)target).getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
            {
                EntityGolemFirstDiorite pawnOwner = (EntityGolemFirstDiorite) pawn.getOwner();
                if (pawnOwner != null && pawnOwner.getOwner() == owner) continue;
            }
            // Do not damage villagers.
            if (target instanceof MerchantEntity) continue;
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
