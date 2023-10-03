package com.kitisplode.golemfirststonemod.entity.entity.golem.other;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.action.AgentFollowProgramGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import com.kitisplode.golemfirststonemod.networking.packet.S2CPacketAgentScreenOpen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;

public class EntityGolemAgent extends AbstractGolemDandoriFollower implements ContainerListener, InventoryCarrier, HasCustomInventoryScreen, GeoEntity, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/golem_agent.geo.json");
    private static final ResourceLocation TEXTURE_OFF = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_off.png");
    private static final ResourceLocation TEXTURE_ON = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent.png");
    public static final ResourceLocation GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_glowmask.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/other/golem_agent.animation.json");

    public static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_agent.idle");
    public static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_agent.walk");

    public static final byte ENTITY_EVENT_TOOL_BROKEN = 47;

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(EntityGolemAgent.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SWINGING_ARM = SynchedEntityData.defineId(EntityGolemAgent.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ARM_SWING = SynchedEntityData.defineId(EntityGolemAgent.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private ItemStack lastHeldItem;
    private static final float armSwingAmount = 0.15f;
    private static final float armSwingAmountStart = 180.0f;
    protected SimpleContainer inventory;
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;
    @Nullable
    public FishingHook fishing;

    public EntityGolemAgent(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setMaxUpStep(0.5f);
        this.createInventory();
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(ACTIVE)) this.entityData.define(ACTIVE, false);
        if (!this.entityData.hasItem(SWINGING_ARM)) this.entityData.define(SWINGING_ARM, false);
        if (!this.entityData.hasItem(ARM_SWING)) this.entityData.define(ARM_SWING, 0.0f);
    }
    public boolean getActive()
    {
        return this.entityData.get(ACTIVE);
    }
    public void setActive(boolean pBoolean)
    {
        this.entityData.set(ACTIVE, pBoolean);
    }
    public float getArmSwing()
    {
        return this.entityData.get(ARM_SWING);
    }
    private void setArmSwing(float pFloat)
    {
        this.entityData.set(ARM_SWING, pFloat);
    }
    public void swingArm()
    {
        this.entityData.set(ARM_SWING, armSwingAmountStart);
    }
    public boolean getSwingingArm()
    {
        return this.entityData.get(SWINGING_ARM);
    }
    public void setSwingingArm(boolean pBoolean)
    {
        this.entityData.set(SWINGING_ARM, pBoolean);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.writeInventoryToTag(pCompound);
    }
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);
        this.readInventoryFromTag(pCompound);
    }

    @Override
    public void writeInventoryToTag(CompoundTag pCompound)
    {
        ListTag listtag = new ListTag();
        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }
        pCompound.put("Items", listtag);
    }
    @Override
    public void readInventoryFromTag(CompoundTag pCompound)
    {
        this.createInventory();
        ListTag listtag = pCompound.getList("Items", 10);
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j < this.inventory.getContainerSize()) {
                this.inventory.setItem(j, ItemStack.of(compoundtag));
            }
        }
    }

    @Override
    public SimpleContainer getInventory()
    {
        return this.inventory;
    }

    public ItemStack getHeldItem()
    {
        return this.inventory.getItem(0);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    public float getMovementSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2,dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 6));

        this.goalSelector.addGoal(2, new AgentFollowProgramGoal(this));
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

//        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
//        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isPushable()
    {
        return !getActive();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!this.getActive())
        {
            BlockPos bp = this.getOnPos();
            if (this.level().hasNeighborSignal(bp))
            {
                this.setActive(true);
            }
        }
        if (this.getArmSwing() > 0.0f)
        {
            float swing = Mth.lerp(armSwingAmount, this.getArmSwing(), 0.0f);
            this.setArmSwing(swing);
            this.setSwingingArm(true);
        }
        else this.setSwingingArm(false);
        if (this.getHeldItem() != null && !(this.getHeldItem().isEmpty()))
        {
            this.lastHeldItem = this.getHeldItem();
        }
    }

    @Override
    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() != null)
        {
            if (this.distanceToSqr(this.getDeployPosition().getCenter()) < 4) this.setDeployPosition(null);
        }
    }

    protected int getInventorySize() {
        return 16;
    }
    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }
    @Override
    public void containerChanged(Container pContainer)
    {
        if (!this.level().isClientSide())
            this.setItemInHand(InteractionHand.MAIN_HAND, this.getHeldItem());
    }
    public boolean hasInventoryChanged(Container pInventory) {
        return this.inventory != pInventory;
    }
    @Override
    public void openCustomInventoryScreen(Player pPlayer)
    {
        if (!this.level().isClientSide) {
            this.openInventory((ServerPlayer) pPlayer);
        }
    }
    private void openInventory(ServerPlayer pPlayer)
    {
        if (pPlayer.containerMenu != pPlayer.inventoryMenu) {
            pPlayer.closeContainer();
        }

        pPlayer.nextContainerCounter();
        ModMessages.sendToPlayer(new S2CPacketAgentScreenOpen(pPlayer.containerCounter, this.inventory.getContainerSize(), this.getId()), pPlayer);
        pPlayer.containerMenu = new InventoryMenuAgent(pPlayer.containerCounter, pPlayer.getInventory(), this.inventory, this);
        pPlayer.initMenu(pPlayer.containerMenu);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(pPlayer, pPlayer.containerMenu));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand)
    {
        if (this.interactIsPlayerHoldingDandoriCall(pPlayer)) return InteractionResult.PASS;
        if (this.getActive()) return InteractionResult.PASS;
        this.openCustomInventoryScreen(pPlayer);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public void handleEntityEvent(byte pId)
    {
        if (pId == ENTITY_EVENT_TOOL_BROKEN)
        {
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F, false);
            }

            this.spawnItemParticles(this.getHeldItem(), 5);
        }
        else super.handleEntityEvent(pId);
    }

    private void spawnItemParticles(ItemStack pStack, int pAmount) {
        for(int i = 0; i < pAmount; ++i) {
            Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3 = vec3.xRot(-this.getXRot() * ((float)Math.PI / 180F));
            vec3 = vec3.yRot(-this.getYRot() * ((float)Math.PI / 180F));
            double d0 = (double)(-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3 vec31 = new Vec3(((double)this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vec31 = vec31.xRot(-this.getXRot() * ((float)Math.PI / 180F));
            vec31 = vec31.yRot(-this.getYRot() * ((float)Math.PI / 180F));
            vec31 = vec31.add(this.getX(), this.getEyeY(), this.getZ());
            if (this.level() instanceof ServerLevel) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, pStack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
            else
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, pStack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }

    }

    public boolean doHurtTarget(Entity pEntity) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (pEntity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)pEntity).getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }
        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            pEntity.setSecondsOnFire(i * 4);
        }
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && pEntity instanceof LivingEntity) {
                ((LivingEntity)pEntity).knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (pEntity instanceof Player) {
                Player player = (Player)pEntity;
                this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }
            this.doEnchantDamageEffects(this, pEntity);
            this.setLastHurtMob(pEntity);
        }
        return flag;
    }

    private void maybeDisableShield(Player pPlayer, ItemStack pMobItemStack, ItemStack pPlayerItemStack) {
        if (!pMobItemStack.isEmpty() && !pPlayerItemStack.isEmpty() && pMobItemStack.getItem() instanceof AxeItem && pPlayerItemStack.is(Items.SHIELD)) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                pPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level().broadcastEntityEvent(pPlayer, (byte)30);
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        if (this.getOwner() == null) return super.canAttack(pTarget);
        if (pTarget instanceof IEntityDandoriFollower dandoriFollower)
        {
            if (this.getOwner() == dandoriFollower.getOwner()) return false;
            if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower targetOwner)
            {
                if (this.getOwner() == targetOwner.getOwner()) return false;
            }
        }
        if (pTarget instanceof TamableAnimal tamableAnimal && this.getOwner() == tamableAnimal.getOwner()) return false;
        return super.canAttack(pTarget);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        if (!this.getActive())
            return TEXTURE_OFF;
        return TEXTURE_ON;
    }
    public ResourceLocation getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
//            EntityGolemAgent pGolem = event.getAnimatable();

            event.getController().setAnimationSpeed(1.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(ANIMATION_WALK);

            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
