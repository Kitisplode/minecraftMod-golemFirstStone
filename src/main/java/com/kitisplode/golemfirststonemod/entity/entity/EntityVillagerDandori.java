package com.kitisplode.golemfirststonemod.entity.entity;

import com.google.common.collect.ImmutableMap;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.HelperItemsForEmeralds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;
import java.util.function.Predicate;

public class EntityVillagerDandori extends AbstractVillager implements GeoEntity
{
    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> DANDORI_TRADES = toIntMap(ImmutableMap.of(
            1, new VillagerTrades.ItemListing[]
                {
                        new HelperItemsForEmeralds(ModItems.ITEM_DANDORI_CALL.get(), 32, 1, 1, 25),
                        new HelperItemsForEmeralds(ModItems.ITEM_DANDORI_DIG.get(), 32, 1, 1, 25),
                        new HelperItemsForEmeralds(ModItems.ITEM_DANDORI_ATTACK.get(), 32, 1, 1, 25),
                        new HelperItemsForEmeralds(ModItems.ITEM_DANDORI_THROW.get(), 32, 1, 1, 25)
                }));
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int piksCountMax = 6;
    private static final double piksSearchRange = 24.0;
    private static final int piksSpawnTime = 100;

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> pMap) {
        return new Int2ObjectOpenHashMap<>(pMap);
    }

    public EntityVillagerDandori(EntityType<? extends AbstractVillager> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 1.0f)
                .build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zombie.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Evoker.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vindicator.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vex.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Pillager.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Illusioner.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zoglin.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.5D));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
//        this.goalSelector.addGoal(2, new WanderingTrader.WanderToPositionGoal(this, 2.0D, 0.35D));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new EntityVillagerDandori.SpawnPiksGoal(this, piksSearchRange, piksCountMax, piksSpawnTime));
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public boolean showProgressBar() {
        return false;
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.6d;
    }

    protected void rewardTradeXp(MerchantOffer pOffer) {
        if (pOffer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    @Override
    protected void updateTrades()
    {
        VillagerTrades.ItemListing[] avillagertrades$itemlisting = DANDORI_TRADES.get(1);
        if (avillagertrades$itemlisting != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addOffersFromItemListings(merchantoffers, avillagertrades$itemlisting, 4);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent)
    {
        return null;
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!itemstack.is(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (pHand == InteractionHand.MAIN_HAND) {
                pPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty()) {
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                if (!this.level().isClientSide) {
                    this.setTradingPlayer(pPlayer);
                    this.openTradingScreen(pPlayer, this.getDisplayName(), 1);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public void push(@NotNull Entity entity)
    {
        if (entity instanceof EntityPawn && ((EntityPawn) entity).getOwner() == this)
            return;
        super.push(entity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            event.getController().setAnimationSpeed(2.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
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
        this.playSound(ModSounds.ENTITY_VILLAGER_DANDORI_PLUCK.get(), 0.2F, this.random.nextFloat() * 0.4f + 0.3F);

        EntityPawn pawn = ModEntities.ENTITY_PAWN_TERRACOTTA.get().create(level());
        if (pawn == null) return null;
        pawn.setOwnerType(EntityPawn.OWNER_TYPES.VILLAGER_DANDORI.ordinal());
        pawn.setOwner(this);
        pawn.setPawnTypePik();
        pawn.setDeltaMovement(0,0.5,0);
        pawn.moveTo(getX(), getY(), getZ(), 0.0f, 0.0F);
        level().addFreshEntity(pawn);

        AreaEffectCloud dust = new AreaEffectCloud(level(), getX(), getY(), getZ());
        dust.setParticle(ParticleTypes.POOF);
        dust.setRadius(1.0f);
        dust.setDuration(1);
        dust.setPos(dust.getX(),dust.getY(),dust.getZ());
        level().addFreshEntity(dust);
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
            this.timer = 0;
        }

        @Override
        public boolean canUse()
        {
            // Scan the nearby area and see if we have enough piks around.
            List<EntityPawn> listPiks = this.villager.level().getEntitiesOfClass(EntityPawn.class, this.villager.getBoundingBox().inflate(this.pikSearchRange), pikPredicate());
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
