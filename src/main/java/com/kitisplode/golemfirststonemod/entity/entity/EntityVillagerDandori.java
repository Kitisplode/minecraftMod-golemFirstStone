package com.kitisplode.golemfirststonemod.entity.entity;

import com.google.common.collect.ImmutableMap;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;
import java.util.function.Predicate;

public class EntityVillagerDandori
    extends MerchantEntity
    implements GeoEntity
{
    private static final Int2ObjectMap<TradeOffers.Factory[]> DANDORI_TRADES = EntityVillagerDandori.copyToFastUtilMap(ImmutableMap.of(
            1, new TradeOffers.Factory[]
            {
                    new TradeOffers.SellItemFactory(ModItems.ITEM_DANDORI_CALL, 32, 1, 1, 25)
            },
            2, new TradeOffers.Factory[]
            {
                    new TradeOffers.SellItemFactory(ModItems.ITEM_DANDORI_ATTACK,32, 1, 1, 25)
            }));
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int piksCountMax = 6;
    private static final double piksSearchRange = 24.0;
    private static final int piksSpawnTime = 100;

    public EntityVillagerDandori(EntityType<? extends MerchantEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0f);
//                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0f)
//                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
//                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> map) {
        return new Int2ObjectOpenHashMap<TradeOffers.Factory[]>(map);
    }

    @Override
    protected void initGoals()
    {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<ZombieEntity>(this, ZombieEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<EvokerEntity>(this, EvokerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<VindicatorEntity>(this, VindicatorEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<VexEntity>(this, VexEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<PillagerEntity>(this, PillagerEntity.class, 15.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<IllusionerEntity>(this, IllusionerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<ZoglinEntity>(this, ZoglinEntity.class, 10.0f, 0.5, 0.5));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
        this.goalSelector.add(1, new LookAtCustomerGoal(this));
//        this.goalSelector.add(2, new WanderingTraderEntity.WanderToTargetGoal(this, 2.0, 0.35));
        this.goalSelector.add(4, new GoToWalkTargetGoal(this, 0.35));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35));
        this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.goalSelector.add(11, new EntityVillagerDandori.SpawnPiksGoal(this, piksSearchRange, piksCountMax, piksSpawnTime));
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        if (offer.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    protected void fillRecipes() {
        TradeOffers.Factory[] factorys = (TradeOffers.Factory[])EntityVillagerDandori.DANDORI_TRADES.get(1);
        TradeOffers.Factory[] factorys2 = (TradeOffers.Factory[])EntityVillagerDandori.DANDORI_TRADES.get(2);
        if (factorys == null || factorys2 == null) {
            return;
        }
        TradeOfferList tradeOfferList = this.getOffers();
        this.fillRecipesFromPool(tradeOfferList, factorys, 2);
        int i = this.random.nextInt(factorys2.length);
        TradeOffers.Factory factory = factorys2[i];
        TradeOffer tradeOffer = factory.create(this, this.random);
        if (tradeOffer != null) {
            tradeOfferList.add(tradeOffer);
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity)
    {
        return null;
    }

    @Override
    public boolean isLeveledMerchant() {
        return false;
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.6f;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
            if (hand == Hand.MAIN_HAND) {
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (this.getOffers().isEmpty()) {
                return ActionResult.success(this.getWorld().isClient);
            }
            if (!this.getWorld().isClient) {
                this.setCustomer(player);
                this.sendOffers(player, this.getDisplayName(), 1);
            }
            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
        if (entity instanceof EntityPawn && ((EntityPawn) entity).getOwner() == this)
            return;
        super.pushAwayFrom(entity);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            event.getController().setAnimationSpeed(2.00);
            if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.villager_dandori.walk"));
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.villager_dandori.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    private EntityPawn spawnPik()
    {
        this.playSound(ModSounds.ENTITY_VILLAGER_DANDORI_PLUCK, 0.2f, this.random.nextFloat() * 0.4f + 0.3f);

        EntityPawn pawn = ModEntities.ENTITY_PAWN_FIRST_DIORITE.create(getWorld());
        if (pawn == null) return null;
        pawn.setOwnerType(EntityPawn.OWNER_TYPES.VILLAGER_DANDORI.ordinal());
        pawn.setOwner(this);
        pawn.setPawnTypePik();
        pawn.setVelocity(0,0.5,0);
        pawn.refreshPositionAndAngles(getX(), getY(), getZ(), 0.0f, 0.0F);
        getWorld().spawnEntity(pawn);

        AreaEffectCloudEntity dust = new AreaEffectCloudEntity(getWorld(), getX(), getY(), getZ());
        dust.setParticleType(ParticleTypes.POOF);
        dust.setRadius(1.0f);
        dust.setDuration(1);
        dust.setPos(dust.getX(),dust.getY(),dust.getZ());
        getWorld().spawnEntity(dust);
        return pawn;
    }

    class SpawnPiksGoal
    extends Goal
    {
        final EntityVillagerDandori villager;
        final double pikSearchRange;
        final int pikCountMax;
        int timer = 0;
        final int time;

        SpawnPiksGoal(EntityVillagerDandori pVillager, double pPikSearchRange, int pikCountMax, int time)
        {
            this.villager = pVillager;
            this.pikSearchRange = pPikSearchRange;
            this.pikCountMax = pikCountMax;
            this.time = time;
            this.timer = time - 10;
        }

        @Override
        public boolean canStart()
        {
            // Scan the nearby area and see if we have enough piks around.
            List<EntityPawn> listPiks = this.villager.getWorld().getEntitiesByClass(EntityPawn.class, this.villager.getBoundingBox().expand(this.pikSearchRange), pikPredicate());
            return listPiks.size() < this.pikCountMax;
        }

        @Override
        public void tick()
        {
            timer++;
            if (timer > time)
            {
                villager.spawnPik();
                timer = 0;
            }
        }

        private Predicate<EntityPawn> pikPredicate()
        {
            return entity ->
            {
                return entity.getOwner() == this.villager;
            };
        }
    }
}
